package com.uniquindio.fintech.service;

import com.uniquindio.fintech.model.Transaction;
import com.uniquindio.fintech.model.User;
import com.uniquindio.fintech.model.Wallet;
import com.uniquindio.fintech.model.enums.NotificationType;
import com.uniquindio.fintech.model.enums.TransactionStatus;
import com.uniquindio.fintech.model.enums.TransactionType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Servicio encargado de la gestión de transacciones financieras.
 * <p>Permite realizar depósitos, retiros y transferencias entre billeteras,
 * aplicando validaciones de saldo, límite diario de transacciones,
 * cálculo de comisiones y asignación de puntos.</p>
 */
@Service
public class TransactionService {

    private final DataStore dataStore;
    private final RewardService rewardService;
    private final NotificationService notificationService;

    /**
     * Crea el servicio de transacciones con inyección de dependencias.
     *
     * @param dataStore           almacén central de datos
     * @param rewardService       servicio de recompensas
     * @param notificationService servicio de notificaciones
     */
    public TransactionService(DataStore dataStore,
                              RewardService rewardService,
                              NotificationService notificationService) {
        this.dataStore = dataStore;
        this.rewardService = rewardService;
        this.notificationService = notificationService;
    }

    /**
     * Realiza un depósito en una billetera.
     * <p>Valida el límite diario de transacciones del usuario,
     * incrementa el saldo, registra la transacción en los historiales
     * de la billetera y del usuario, la agrega a la pila de deshacer
     * y calcula los puntos correspondientes.</p>
     *
     * @param walletCode código de la billetera destino
     * @param amount     monto a depositar
     * @return la transacción creada
     * @throws IllegalArgumentException si los datos son inválidos o se excede el límite diario
     */
    public Transaction depositToWallet(String walletCode, double amount) {
        validateAmountPositive(amount);
        Wallet wallet = findActiveWallet(walletCode);
        User user = findOwner(wallet);
        if (isDailyLimitExceeded(user)) {
            return rejectTransaction(user, walletCode, null,
                    amount, TransactionType.DEPOSIT);
        }
        wallet.setBalance(wallet.getBalance() + amount);
        Transaction tx = buildTransaction(TransactionType.DEPOSIT,
                amount, walletCode, null, 0.0);
        int points = rewardService.calculatePointsForTransaction(tx);
        tx.setPointsGenerated(points);
        recordTransaction(wallet, user, tx);
        rewardService.addPoints(user, points);
        return tx;
    }

    /**
     * Realiza un retiro desde una billetera.
     * <p>Valida saldo suficiente y el límite diario, disminuye el saldo,
     * registra la transacción y calcula puntos.</p>
     *
     * @param walletCode código de la billetera origen
     * @param amount     monto a retirar
     * @return la transacción creada
     * @throws IllegalArgumentException si los datos son inválidos, saldo insuficiente
     *                                  o se excede el límite diario
     */
    public Transaction withdrawFromWallet(String walletCode,
                                          double amount) {
        validateAmountPositive(amount);
        Wallet wallet = findActiveWallet(walletCode);
        User user = findOwner(wallet);
        if (isDailyLimitExceeded(user)) {
            return rejectTransaction(user, walletCode, null,
                    amount, TransactionType.WITHDRAWAL);
        }
        validateSufficientBalance(wallet, amount);
        wallet.setBalance(wallet.getBalance() - amount);
        Transaction tx = buildTransaction(TransactionType.WITHDRAWAL,
                amount, walletCode, null, 0.0);
        int points = rewardService.calculatePointsForTransaction(tx);
        tx.setPointsGenerated(points);
        recordTransaction(wallet, user, tx);
        rewardService.addPoints(user, points);
        return tx;
    }

    /**
     * Realiza una transferencia entre dos billeteras de distintos usuarios.
     * <p>Valida saldo suficiente en la billetera origen, calcula comisión
     * según el nivel del remitente, descuenta del origen, acredita al destino
     * (monto menos comisión), registra en ambos historiales, agrega arista
     * al grafo de transferencias y calcula puntos para el remitente.</p>
     *
     * @param sourceWalletCode código de la billetera origen
     * @param targetWalletCode código de la billetera destino
     * @param amount           monto a transferir
     * @return la transacción creada
     * @throws IllegalArgumentException si los datos son inválidos o saldo insuficiente
     */
    public Transaction transferBetweenUsers(String sourceWalletCode,
                                            String targetWalletCode,
                                            double amount) {
        validateAmountPositive(amount);
        Wallet source = findActiveWallet(sourceWalletCode);
        Wallet target = findActiveWallet(targetWalletCode);
        User sender = findOwner(source);
        User receiver = findOwner(target);
        if (isDailyLimitExceeded(sender)) {
            return rejectTransaction(sender, sourceWalletCode,
                    targetWalletCode, amount, TransactionType.TRANSFER);
        }
        validateSufficientBalance(source, amount);
        double commission = amount * sender.getLevel().getCommissionRate();
        double receivedAmount = amount - commission;
        source.setBalance(source.getBalance() - amount);
        target.setBalance(target.getBalance() + receivedAmount);
        Transaction tx = buildTransaction(TransactionType.TRANSFER,
                amount, sourceWalletCode, targetWalletCode, commission);
        int points = rewardService.calculatePointsForTransaction(tx);
        tx.setPointsGenerated(points);
        recordTransfer(source, target, sender, receiver, tx);
        addTransferEdge(sender.getId(), receiver.getId(), amount);
        rewardService.addPoints(sender, points);
        return tx;
    }

    /**
     * Cuenta las transacciones realizadas por un usuario en el día actual.
     *
     * @param user el usuario cuyas transacciones se cuentan
     * @return cantidad de transacciones hoy
     */
    public int countTodayTransactions(User user) {
        if (user == null) {
            throw new IllegalArgumentException(
                    "El usuario no puede ser nulo");
        }
        LocalDate today = LocalDate.now();
        int count = 0;
        for (Transaction tx : user.getTransactionHistory()) {
            if (tx.getDate().toLocalDate().equals(today)
                    && tx.getStatus() == TransactionStatus.COMPLETED) {
                count++;
            }
        }
        return count;
    }

    // ---- Métodos privados auxiliares ----

    private boolean isDailyLimitExceeded(User user) {
        int todayCount = countTodayTransactions(user);
        return todayCount >= user.getLevel().getMaxDailyTransactions();
    }

    private Transaction rejectTransaction(User user,
                                          String sourceCode,
                                          String targetCode,
                                          double amount,
                                          TransactionType type) {
        Transaction tx = new Transaction(LocalDateTime.now(), type,
                amount, sourceCode, targetCode,
                TransactionStatus.REJECTED, 0, 0.0,
                "Rechazada: límite diario de transacciones excedido");
        user.getTransactionHistory().addLast(tx);
        notificationService.sendNotification(user.getId(),
                NotificationType.OPERATION_REJECTED,
                "Transacción rechazada: ha excedido el límite diario de "
                + user.getLevel().getMaxDailyTransactions()
                + " transacciones para el nivel "
                + user.getLevel().name());
        return tx;
    }

    private Transaction buildTransaction(TransactionType type,
                                         double amount,
                                         String sourceCode,
                                         String targetCode,
                                         double commission) {
        return new Transaction(LocalDateTime.now(), type, amount,
                sourceCode, targetCode, TransactionStatus.COMPLETED,
                0, commission, type.name());
    }

    private void recordTransaction(Wallet wallet, User user,
                                   Transaction tx) {
        wallet.getTransactionHistory().addLast(tx);
        user.getTransactionHistory().addLast(tx);
        user.getUndoStack().push(tx);
    }

    private void recordTransfer(Wallet source, Wallet target,
                                User sender, User receiver,
                                Transaction tx) {
        source.getTransactionHistory().addLast(tx);
        target.getTransactionHistory().addLast(tx);
        sender.getTransactionHistory().addLast(tx);
        receiver.getTransactionHistory().addLast(tx);
        sender.getUndoStack().push(tx);
    }

    private void addTransferEdge(String senderId, String receiverId,
                                 double amount) {
        if (!dataStore.getTransferGraph().hasEdge(senderId, receiverId)) {
            dataStore.getTransferGraph().addEdge(
                    senderId, receiverId, amount);
        }
    }

    private Wallet findActiveWallet(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException(
                    "El código de la billetera no puede ser nulo o vacío");
        }
        if (!dataStore.getWalletsByCode().containsKey(code)) {
            throw new IllegalArgumentException(
                    "No existe una billetera con el código: " + code);
        }
        Wallet wallet = dataStore.getWalletsByCode().get(code);
        if (!wallet.isActive()) {
            throw new IllegalArgumentException(
                    "La billetera " + code + " está desactivada");
        }
        return wallet;
    }

    private User findOwner(Wallet wallet) {
        return dataStore.getUsersById().get(wallet.getOwnerId());
    }

    private void validateAmountPositive(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "El monto debe ser mayor a cero: " + amount);
        }
    }

    private void validateSufficientBalance(Wallet wallet, double amount) {
        if (wallet.getBalance() < amount) {
            throw new IllegalArgumentException(
                    "Saldo insuficiente en la billetera "
                    + wallet.getCode() + ". Saldo: "
                    + wallet.getBalance() + ", requerido: " + amount);
        }
    }
}
