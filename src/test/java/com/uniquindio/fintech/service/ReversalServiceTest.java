package com.uniquindio.fintech.service;

import com.uniquindio.fintech.model.Transaction;
import com.uniquindio.fintech.model.User;
import com.uniquindio.fintech.model.Wallet;
import com.uniquindio.fintech.model.enums.TransactionStatus;
import com.uniquindio.fintech.model.enums.UserLevel;
import com.uniquindio.fintech.model.enums.WalletType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para {@link ReversalService}.
 * <p>Utiliza instancias reales de {@link DataStore} y estructuras personalizadas
 * sin mocks, verificando restauración de saldos, resta de puntos,
 * recálculo de nivel y manejo de pila vacía.</p>
 */
class ReversalServiceTest {

    private DataStore dataStore;
    private ReversalService reversalService;
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
        reversalService = new ReversalService(
                dataStore, notificationService);
        userService = new UserService(dataStore);
        walletService = new WalletService(dataStore);
    }

    /**
     * Verifica que la reversión de una transferencia restaura los saldos
     * de las billeteras origen y destino.
     */
    @Test
    void testReversalRestoresBalances() {
        userService.registerUser(
                "3001", "Ana", "ana@mail.com", "3001111111");
        userService.registerUser(
                "3002", "Luis", "luis@mail.com", "3002222222");
        Wallet source = walletService.createWallet(
                "3001", "Origen", WalletType.SAVINGS);
        Wallet target = walletService.createWallet(
                "3002", "Destino", WalletType.SAVINGS);
        transactionService.depositToWallet(source.getCode(), 1000.0);

        transactionService.transferBetweenUsers(
                source.getCode(), target.getCode(), 200.0);
        double sourceAfterTransfer = source.getBalance();
        double targetAfterTransfer = target.getBalance();

        Transaction reversed = reversalService.reverseLastOperation("3001");

        assertEquals(TransactionStatus.REVERSED, reversed.getStatus());
        assertEquals(sourceAfterTransfer + 200.0,
                source.getBalance(), 0.01);
        double commission = 200.0 * 0.05;
        assertEquals(targetAfterTransfer - (200.0 - commission),
                target.getBalance(), 0.01);
    }

    /**
     * Verifica que la reversión resta los puntos generados y recalcula el nivel.
     */
    @Test
    void testReversalSubtractsPointsRecalculatesLevel() {
        User user = userService.registerUser(
                "3003", "Maria", "maria@mail.com", "3003333333");
        Wallet wallet = walletService.createWallet(
                "3003", "Ahorros", WalletType.SAVINGS);

        transactionService.depositToWallet(wallet.getCode(), 50000.0);
        int pointsAfterDeposit = user.getPoints();
        assertTrue(pointsAfterDeposit > 0);

        Transaction reversed = reversalService.reverseLastOperation("3003");

        assertEquals(TransactionStatus.REVERSED, reversed.getStatus());
        assertTrue(user.getPoints() < pointsAfterDeposit);
        assertEquals(UserLevel.fromPoints(user.getPoints()),
                user.getLevel());
    }

    /**
     * Verifica que revertir con la pila vacía lanza excepción.
     */
    @Test
    void testReversalWithEmptyStack_ThrowsError() {
        userService.registerUser(
                "3004", "Pedro", "pedro@mail.com", "3004444444");

        assertThrows(IllegalArgumentException.class, () ->
                reversalService.reverseLastOperation("3004"));
    }
}
