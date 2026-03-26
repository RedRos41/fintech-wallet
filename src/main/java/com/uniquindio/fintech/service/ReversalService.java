package com.uniquindio.fintech.service;

import com.uniquindio.fintech.model.Transaction;
import com.uniquindio.fintech.model.User;
import com.uniquindio.fintech.model.UserPointsEntry;
import com.uniquindio.fintech.model.Wallet;
import com.uniquindio.fintech.model.enums.NotificationType;
import com.uniquindio.fintech.model.enums.TransactionStatus;
import com.uniquindio.fintech.model.enums.TransactionType;
import com.uniquindio.fintech.model.enums.UserLevel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Servicio encargado de revertir la última operación de un usuario.
 * <p>Desapila la última transacción completada de la pila de deshacer,
 * devuelve los saldos afectados, reembolsa la comisión si corresponde,
 * resta los puntos generados, sincroniza el BST y recalcula el nivel.</p>
 */
@Service
public class ReversalService {

    private final DataStore dataStore;
    private final NotificationService notificationService;

    /**
     * Crea el servicio de reversión con inyección de dependencias.
     *
     * @param dataStore           almacén central de datos
     * @param notificationService servicio de notificaciones
     */
    public ReversalService(DataStore dataStore,
                           NotificationService notificationService) {
        this.dataStore = dataStore;
        this.notificationService = notificationService;
    }

    /**
     * Revierte la última operación del usuario.
     * <p>Desapila de la pila de deshacer, revierte el saldo de las
     * billeteras involucradas, reembolsa la comisión al remitente en
     * caso de transferencia, resta los puntos generados, actualiza
     * el BST de ranking y recalcula el nivel del usuario.
     * Marca la transacción como REVERSED y la agrega al historial.</p>
     *
     * @param userId cédula del usuario cuya última operación se revierte
     * @return la transacción revertida
     * @throws IllegalArgumentException si el usuario no existe
     *                                  o no tiene operaciones para deshacer
     */
    public Transaction reverseLastOperation(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException(
                    "El identificador del usuario no puede ser nulo o vacío");
        }
        User user = findUserOrThrow(userId);
        if (user.getUndoStack().isEmpty()) {
            throw new IllegalArgumentException(
                    "El usuario " + userId
                    + " no tiene operaciones para deshacer");
        }
        Transaction tx = user.getUndoStack().pop();
        reverseBalances(tx);
        subtractPoints(user, tx.getPointsGenerated());
        tx.setStatus(TransactionStatus.REVERSED);
        user.getTransactionHistory().addLast(tx);
        notificationService.sendNotification(userId,
                NotificationType.SECURITY_ALERT,
                "Se ha revertido la transacción " + tx.getId()
                + " de tipo " + tx.getType().name());
        return tx;
    }

    /**
     * Revierte los saldos de las billeteras involucradas en la transacción.
     *
     * @param tx la transacción a revertir
     */
    private void reverseBalances(Transaction tx) {
        if (tx.getType() == TransactionType.DEPOSIT) {
            reverseDeposit(tx);
        } else if (tx.getType() == TransactionType.WITHDRAWAL) {
            reverseWithdrawal(tx);
        } else if (tx.getType() == TransactionType.TRANSFER) {
            reverseTransfer(tx);
        }
    }

    /**
     * Revierte un depósito restando el monto de la billetera origen.
     *
     * @param tx la transacción de depósito a revertir
     */
    private void reverseDeposit(Transaction tx) {
        Wallet wallet = findWalletOrThrow(tx.getSourceWalletCode());
        wallet.setBalance(wallet.getBalance() - tx.getAmount());
    }

    /**
     * Revierte un retiro sumando el monto a la billetera origen.
     *
     * @param tx la transacción de retiro a revertir
     */
    private void reverseWithdrawal(Transaction tx) {
        Wallet wallet = findWalletOrThrow(tx.getSourceWalletCode());
        wallet.setBalance(wallet.getBalance() + tx.getAmount());
    }

    /**
     * Revierte una transferencia devolviendo el monto al origen
     * y restando el monto recibido del destino.
     *
     * @param tx la transacción de transferencia a revertir
     */
    private void reverseTransfer(Transaction tx) {
        Wallet source = findWalletOrThrow(tx.getSourceWalletCode());
        Wallet target = findWalletOrThrow(tx.getTargetWalletCode());
        double commission = tx.getCommissionCharged();
        double receivedAmount = tx.getAmount() - commission;
        source.setBalance(source.getBalance() + tx.getAmount());
        target.setBalance(target.getBalance() - receivedAmount);
    }

    /**
     * Resta puntos al usuario, actualiza el BST y recalcula el nivel.
     *
     * @param user   el usuario al que se le restan puntos
     * @param points cantidad de puntos a restar
     */
    private void subtractPoints(User user, int points) {
        int oldPoints = user.getPoints();
        int newPoints = Math.max(0, oldPoints - points);
        user.setPoints(newPoints);
        dataStore.getPointsRanking().delete(
                new UserPointsEntry(user.getId(), oldPoints));
        dataStore.getPointsRanking().insert(
                new UserPointsEntry(user.getId(), newPoints));
        user.setLevel(UserLevel.fromPoints(newPoints));
    }

    /**
     * Busca un usuario por cédula o lanza excepción si no existe.
     *
     * @param userId cédula del usuario
     * @return el usuario encontrado
     */
    private User findUserOrThrow(String userId) {
        if (!dataStore.getUsersById().containsKey(userId)) {
            throw new IllegalArgumentException(
                    "No existe un usuario con la cédula: " + userId);
        }
        return dataStore.getUsersById().get(userId);
    }

    /**
     * Busca una billetera por código o lanza excepción si no existe.
     *
     * @param code código de la billetera
     * @return la billetera encontrada
     */
    private Wallet findWalletOrThrow(String code) {
        if (!dataStore.getWalletsByCode().containsKey(code)) {
            throw new IllegalArgumentException(
                    "No existe una billetera con el código: " + code);
        }
        return dataStore.getWalletsByCode().get(code);
    }
}
