package com.uniquindio.fintech.service;

import com.uniquindio.fintech.model.RewardBenefit;
import com.uniquindio.fintech.model.Transaction;
import com.uniquindio.fintech.model.User;
import com.uniquindio.fintech.model.UserPointsEntry;
import com.uniquindio.fintech.model.enums.TransactionStatus;
import com.uniquindio.fintech.model.enums.TransactionType;
import com.uniquindio.fintech.model.enums.UserLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para {@link RewardService}.
 * <p>Utiliza instancias reales de {@link DataStore} y estructuras personalizadas
 * sin mocks, verificando cálculo de puntos, subida de nivel, canje de beneficios
 * y sincronización del BST.</p>
 */
class RewardServiceTest {

    private DataStore dataStore;
    private RewardService rewardService;
    private NotificationService notificationService;
    private UserService userService;

    @BeforeEach
    void setUp() {
        dataStore = new DataStore();
        notificationService = new NotificationService(dataStore);
        rewardService = new RewardService(dataStore, notificationService);
        userService = new UserService(dataStore);
    }

    /**
     * Verifica que los puntos se calculan correctamente según el tipo de transacción.
     */
    @Test
    void testPointsCalculatedCorrectlyByType() {
        Transaction deposit = new Transaction(
                LocalDateTime.now(), TransactionType.DEPOSIT, 500.0,
                "W1", null, TransactionStatus.COMPLETED, 0, 0.0, "dep");
        Transaction withdrawal = new Transaction(
                LocalDateTime.now(), TransactionType.WITHDRAWAL, 500.0,
                "W1", null, TransactionStatus.COMPLETED, 0, 0.0, "wd");
        Transaction transfer = new Transaction(
                LocalDateTime.now(), TransactionType.TRANSFER, 500.0,
                "W1", "W2", TransactionStatus.COMPLETED, 0, 0.0, "tf");

        assertEquals(5, rewardService.calculatePointsForTransaction(deposit));
        assertEquals(10, rewardService.calculatePointsForTransaction(withdrawal));
        assertEquals(15, rewardService.calculatePointsForTransaction(transfer));
    }

    /**
     * Verifica que al acumular suficientes puntos se detecta la subida de nivel.
     */
    @Test
    void testLevelUpDetected() {
        User user = userService.registerUser(
                "2001", "Ana", "ana@mail.com", "3001111111");
        assertEquals(UserLevel.BRONZE, user.getLevel());

        // Bronze -> Silver at 501 points
        rewardService.addPoints(user, 501);

        assertEquals(UserLevel.SILVER, user.getLevel());
        assertEquals(501, user.getPoints());
    }

    /**
     * Verifica que el canje de beneficio resta puntos y sincroniza el BST.
     */
    @Test
    void testRedeemBenefitSubtractsPointsSyncsBST() {
        User user = userService.registerUser(
                "2002", "Luis", "luis@mail.com", "3002222222");
        rewardService.addPoints(user, 600);

        RewardBenefit benefit = new RewardBenefit(
                "Descuento 10%", "Descuento en compras", 100, "2002");
        dataStore.getBenefitCatalog().addLast(benefit);

        rewardService.redeemBenefit("2002", benefit.getId());

        assertEquals(500, user.getPoints());
        assertTrue(benefit.isRedeemed());
        assertNotNull(benefit.getRedeemedDate());
        assertTrue(dataStore.getPointsRanking().contains(
                new UserPointsEntry("2002", 500)));
    }

    /**
     * Verifica que el canje con puntos insuficientes lanza excepción.
     */
    @Test
    void testRedeemWithInsufficientPoints_Rejected() {
        User user = userService.registerUser(
                "2003", "Maria", "maria@mail.com", "3003333333");
        rewardService.addPoints(user, 50);

        RewardBenefit benefit = new RewardBenefit(
                "Premium", "Beneficio premium", 200, "2003");
        dataStore.getBenefitCatalog().addLast(benefit);

        assertThrows(IllegalArgumentException.class, () ->
                rewardService.redeemBenefit("2003", benefit.getId()));
    }
}
