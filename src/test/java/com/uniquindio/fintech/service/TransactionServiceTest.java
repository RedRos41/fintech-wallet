package com.uniquindio.fintech.service;

import com.uniquindio.fintech.model.Transaction;
import com.uniquindio.fintech.model.User;
import com.uniquindio.fintech.model.Wallet;
import com.uniquindio.fintech.model.enums.TransactionStatus;
import com.uniquindio.fintech.model.enums.WalletType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para {@link TransactionService}.
 * <p>Utiliza instancias reales de {@link DataStore} y estructuras personalizadas
 * sin mocks, verificando depósitos, retiros, transferencias, límites y grafo.</p>
 */
class TransactionServiceTest {

    private DataStore dataStore;
    private TransactionService transactionService;
    private UserService userService;
    private WalletService walletService;

    @BeforeEach
    void setUp() {
        dataStore = new DataStore();
        NotificationService notificationService =
                new NotificationService(dataStore);
        RewardService rewardService =
                new RewardService(dataStore, notificationService);
        transactionService = new TransactionService(
                dataStore, rewardService, notificationService);
        userService = new UserService(dataStore);
        walletService = new WalletService(dataStore);
    }

    /**
     * Verifica que un depósito actualiza el saldo y genera puntos.
     */
    @Test
    void testDepositUpdatesBalanceAndPoints() {
        User user = userService.registerUser(
                "1001", "Ana", "ana@mail.com", "3001111111");
        Wallet wallet = walletService.createWallet(
                "1001", "Ahorros", WalletType.SAVINGS);

        Transaction tx = transactionService.depositToWallet(
                wallet.getCode(), 500.0);

        assertEquals(500.0, wallet.getBalance(), 0.01);
        assertEquals(TransactionStatus.COMPLETED, tx.getStatus());
        assertTrue(tx.getPointsGenerated() > 0);
        assertTrue(user.getPoints() > 0);
    }

    /**
     * Verifica que un retiro con saldo insuficiente lanza excepción.
     */
    @Test
    void testWithdrawInsufficientBalance_Rejected() {
        userService.registerUser(
                "1002", "Luis", "luis@mail.com", "3002222222");
        Wallet wallet = walletService.createWallet(
                "1002", "Diario", WalletType.DAILY);
        transactionService.depositToWallet(wallet.getCode(), 100.0);

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.withdrawFromWallet(
                        wallet.getCode(), 500.0));
    }

    /**
     * Verifica que una transferencia aplica comisión del nivel Bronze (5%).
     */
    @Test
    void testTransferAppliesCommission() {
        userService.registerUser(
                "1003", "Maria", "maria@mail.com", "3003333333");
        userService.registerUser(
                "1004", "Pedro", "pedro@mail.com", "3004444444");
        Wallet source = walletService.createWallet(
                "1003", "Origen", WalletType.SAVINGS);
        Wallet target = walletService.createWallet(
                "1004", "Destino", WalletType.SAVINGS);
        transactionService.depositToWallet(source.getCode(), 1000.0);

        Transaction tx = transactionService.transferBetweenUsers(
                source.getCode(), target.getCode(), 200.0);

        double expectedCommission = 200.0 * 0.05;
        assertEquals(expectedCommission, tx.getCommissionCharged(), 0.01);
        double expectedTargetBalance = 200.0 - expectedCommission;
        assertEquals(expectedTargetBalance, target.getBalance(), 0.01);
    }

    /**
     * Verifica que al exceder el límite diario se rechaza la transferencia.
     */
    @Test
    void testTransferExceedsDailyLimit_Rejected() {
        userService.registerUser(
                "1005", "Carlos", "carlos@mail.com", "3005555555");
        userService.registerUser(
                "1006", "Laura", "laura@mail.com", "3006666666");
        Wallet source = walletService.createWallet(
                "1005", "Origen", WalletType.SAVINGS);
        Wallet target = walletService.createWallet(
                "1006", "Destino", WalletType.SAVINGS);
        transactionService.depositToWallet(source.getCode(), 10000.0);

        // Bronze level allows 5 daily transactions; deposit was 1
        for (int i = 0; i < 4; i++) {
            transactionService.transferBetweenUsers(
                    source.getCode(), target.getCode(), 10.0);
        }

        Transaction rejected = transactionService.transferBetweenUsers(
                source.getCode(), target.getCode(), 10.0);
        assertEquals(TransactionStatus.REJECTED, rejected.getStatus());
    }

    /**
     * Verifica que una transferencia registra una arista en el grafo.
     */
    @Test
    void testTransferRecordsEdgeInGraph() {
        userService.registerUser(
                "1007", "Elena", "elena@mail.com", "3007777777");
        userService.registerUser(
                "1008", "Juan", "juan@mail.com", "3008888888");
        Wallet source = walletService.createWallet(
                "1007", "Origen", WalletType.SAVINGS);
        Wallet target = walletService.createWallet(
                "1008", "Destino", WalletType.SAVINGS);
        transactionService.depositToWallet(source.getCode(), 1000.0);

        transactionService.transferBetweenUsers(
                source.getCode(), target.getCode(), 100.0);

        assertTrue(dataStore.getTransferGraph()
                .hasEdge("1007", "1008"));
    }
}
