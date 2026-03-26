package com.uniquindio.fintech.config;

import com.uniquindio.fintech.model.RewardBenefit;
import com.uniquindio.fintech.model.ScheduledOperation;
import com.uniquindio.fintech.model.User;
import com.uniquindio.fintech.model.enums.TransactionType;
import com.uniquindio.fintech.model.enums.WalletType;
import com.uniquindio.fintech.service.DataStore;
import com.uniquindio.fintech.service.FraudDetectionService;
import com.uniquindio.fintech.service.RewardService;
import com.uniquindio.fintech.service.ScheduledOperationService;
import com.uniquindio.fintech.service.TransactionService;
import com.uniquindio.fintech.service.UserService;
import com.uniquindio.fintech.service.WalletService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Inicializador de datos de prueba para la aplicación fintech.
 * <p>Carga usuarios, billeteras, transacciones, operaciones programadas,
 * beneficios y ejecuta detección de fraude sobre los datos iniciales.</p>
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final WalletService walletService;
    private final TransactionService transactionService;
    private final RewardService rewardService;
    private final ScheduledOperationService scheduledOperationService;
    private final FraudDetectionService fraudDetectionService;
    private final DataStore dataStore;

    /** Códigos de billeteras principales indexados por usuario (0-9). */
    private final String[] primaryWallets = new String[10];

    /** Códigos de billeteras secundarias indexados por usuario (0-9). */
    private final String[] secondaryWallets = new String[10];

    /** Cédulas de los 10 usuarios del Eje Cafetero. */
    private final String[] userIds = {
            "1094900001", "1094900002", "1094900003", "1094900004",
            "1094900005", "1094900006", "1094900007", "1094900008",
            "1094900009", "1094900010"
    };

    /**
     * Crea el inicializador de datos con inyección de todos los servicios.
     *
     * @param userService               servicio de usuarios
     * @param walletService             servicio de billeteras
     * @param transactionService        servicio de transacciones
     * @param rewardService             servicio de recompensas
     * @param scheduledOperationService servicio de operaciones programadas
     * @param fraudDetectionService     servicio de detección de fraudes
     * @param dataStore                 almacén central de datos
     */
    public DataInitializer(UserService userService,
                           WalletService walletService,
                           TransactionService transactionService,
                           RewardService rewardService,
                           ScheduledOperationService scheduledOperationService,
                           FraudDetectionService fraudDetectionService,
                           DataStore dataStore) {
        this.userService = userService;
        this.walletService = walletService;
        this.transactionService = transactionService;
        this.rewardService = rewardService;
        this.scheduledOperationService = scheduledOperationService;
        this.fraudDetectionService = fraudDetectionService;
        this.dataStore = dataStore;
    }

    @Override
    public void run(String... args) {
        initializeBenefitCatalog();
        initializeUsers();
        initializeWallets();
        seedDeposits();
        seedWithdrawals();
        seedInternalTransfers();
        seedUserTransfers();
        seedSuspiciousTransactions();
        adjustPointsForLevelDistribution();
        seedScheduledOperations();
        fraudDetectionService.analyzeAllUsers();
    }

    // ── Catálogo de beneficios ──────────────────────────────────

    private void initializeBenefitCatalog() {
        addBenefit("Transferencia sin comisión", 100,
                "La próxima transferencia no cobra comisión");
        addBenefit("Bono de saldo $5,000", 250,
                "Se acreditan $5,000 en una billetera a elección");
        addBenefit("Doble puntos por 24h", 500,
                "Las próximas transacciones generan el doble de puntos");
        addBenefit("Aumento límite diario +10", 300,
                "Se aumenta el límite diario de transacciones por 7 días");
        addBenefit("Transferencia premium", 750,
                "Transferencia sin límite de monto por una vez");
    }

    private void addBenefit(String name, int cost, String desc) {
        dataStore.getBenefitCatalog().addLast(
                new RewardBenefit(name, desc, cost, "SYSTEM"));
    }

    // ── Usuarios ────────────────────────────────────────────────

    private void initializeUsers() {
        reg(0, "Carlos Alberto Ríos",
                "carlos.rios@mail.com", "3101234501");
        reg(1, "María Fernanda Ocampo",
                "maria.ocampo@mail.com", "3151234502");
        reg(2, "Juan David Henao",
                "juan.henao@mail.com", "3201234503");
        reg(3, "Luisa Marcela Gallego",
                "luisa.gallego@mail.com", "3111234504");
        reg(4, "Andrés Felipe Valencia",
                "andres.valencia@mail.com", "3161234505");
        reg(5, "Natalia Andrea Marín",
                "natalia.marin@mail.com", "3051234506");
        reg(6, "Diego Alejandro Cardona",
                "diego.cardona@mail.com", "3181234507");
        reg(7, "Paola Andrea Gutiérrez",
                "paola.gutierrez@mail.com", "3121234508");
        reg(8, "Sebastián Camilo Londoño",
                "sebastian.londono@mail.com", "3171234509");
        reg(9, "Valentina Ríos Salazar",
                "valentina.rios@mail.com", "3141234510");
    }

    private void reg(int i, String name, String email, String phone) {
        userService.registerUser(userIds[i], name, email, phone);
    }

    // ── Billeteras ──────────────────────────────────────────────

    private void initializeWallets() {
        for (int i = 0; i < 10; i++) {
            createWalletsForUser(i);
        }
    }

    private void createWalletsForUser(int i) {
        String uid = userIds[i];
        primaryWallets[i] = walletService.createWallet(
                uid, "Principal", WalletType.SAVINGS).getCode();
        secondaryWallets[i] = walletService.createWallet(
                uid, "Diario", WalletType.DAILY).getCode();
        walletService.createWallet(uid, "Compras", WalletType.SHOPPING);
        if (i % 2 == 0) {
            walletService.createWallet(
                    uid, "Transporte", WalletType.TRANSPORT);
        }
        if (i % 3 == 0) {
            walletService.createWallet(
                    uid, "Inversión", WalletType.INVESTMENT);
        }
    }

    // ── Depósitos ───────────────────────────────────────────────
    // DEPOSIT points = amount / 100.
    // Each user gets 2 deposits (primary + secondary) = 2 txs.
    // Users 0-3 get 1 extra deposit = 3 txs total.

    private void seedDeposits() {
        seedPrimaryDeposits();
        seedSecondaryDeposits();
        seedExtraDeposits();
    }

    private void seedPrimaryDeposits() {
        for (int i = 0; i < 10; i++) {
            transactionService.depositToWallet(
                    primaryWallets[i], 10_000);
        }
    }

    private void seedSecondaryDeposits() {
        for (int i = 0; i < 10; i++) {
            transactionService.depositToWallet(
                    secondaryWallets[i], 5_000);
        }
    }

    private void seedExtraDeposits() {
        transactionService.depositToWallet(primaryWallets[0], 80_000);
        transactionService.depositToWallet(primaryWallets[1], 60_000);
        transactionService.depositToWallet(primaryWallets[2], 40_000);
        transactionService.depositToWallet(primaryWallets[3], 30_000);
    }

    // ── Retiros ─────────────────────────────────────────────────
    // Users 0-3: 4th tx.  Users 4-7: 3rd tx.

    private void seedWithdrawals() {
        transactionService.withdrawFromWallet(primaryWallets[0], 2_000);
        transactionService.withdrawFromWallet(primaryWallets[1], 1_500);
        transactionService.withdrawFromWallet(primaryWallets[2], 1_000);
        transactionService.withdrawFromWallet(primaryWallets[3], 500);
        transactionService.withdrawFromWallet(primaryWallets[4], 500);
        transactionService.withdrawFromWallet(secondaryWallets[5], 300);
        transactionService.withdrawFromWallet(primaryWallets[6], 400);
        transactionService.withdrawFromWallet(secondaryWallets[7], 200);
    }

    // ── Transferencias internas ─────────────────────────────────
    // Users 0-1: 5th tx (they'll be at BRONZE limit).
    // IMPORTANT: internal transfer adds 2 entries to user's history
    // (sender + receiver are same person), so this uses 2 tx "slots".
    // After this: user 0 has 4+2=6, user 1 has 4+2=6.
    // But the limit check happens BEFORE recording (count=4, 4<5 OK).

    private void seedInternalTransfers() {
        transactionService.transferBetweenUsers(
                primaryWallets[0], secondaryWallets[0], 2_000);
        transactionService.transferBetweenUsers(
                primaryWallets[1], secondaryWallets[1], 2_000);
    }

    // ── Transferencias entre usuarios ───────────────────────────
    // After deposits/withdrawals/internal:
    // User 0: 6 txs, User 1: 6 txs (already over BRONZE limit)
    // User 2: 4 txs, User 3: 4 txs
    // User 4: 3 txs, User 5: 3 txs
    // User 6: 3 txs, User 7: 3 txs
    // User 8: 2 txs, User 9: 2 txs
    //
    // Users 0,1 can only be receivers from now on.
    // Users 2-9 can still send (under limit 5).
    // Each send adds +1 to sender history, each receive adds +1.
    // We must keep each BRONZE user's count < 5 at each send check.

    private void seedUserTransfers() {
        seedTransferWave1();
        seedTransferWave2();
        seedTransferWave3();
    }

    /**
     * Wave 1: cada usuario 2-9 envía una transferencia.
     * Después: U2=5, U3=5, U4=4, U5=4, U6=4, U7=4, U8=3, U9=3.
     * (Receivers: 0,1 receive but are already over limit.)
     */
    private void seedTransferWave1() {
        transfer(2, 0, 3_000);
        transfer(3, 1, 2_500);
        transfer(4, 0, 2_000);
        transfer(5, 1, 1_800);
        transfer(6, 2, 1_500);
        transfer(7, 3, 1_200);
        transfer(8, 4, 1_000);
        transfer(9, 5, 800);
    }

    /**
     * Wave 2: usuarios 4-9 envían una segunda transferencia.
     * Check at send time:
     * U4: receives from U8 in wave1 => 4+1recv=5 at check?
     * No: U4 sends at count=4, then receives from U8 after.
     * Order matters. U4 sends first (count=4, OK), then U8->U4.
     * After wave2: U4=5+1recv=6, U5=5+1recv=6, etc.
     */
    private void seedTransferWave2() {
        transfer(4, 2, 1_500);
        transfer(5, 3, 1_200);
        transfer(6, 0, 1_000);
        transfer(7, 1, 900);
        transfer(8, 6, 700);
        transfer(9, 7, 600);
    }

    /**
     * Wave 3: usuarios 8,9 envían una tercera transferencia.
     * U8 at check: 2(dep)+1(w1 send)+1(w2 send)+1(w1 recv from 7)
     *   +1(w2 recv from 5) = hmm let me recount...
     * U8: 2 deposits + 0 withdrawals = 2.
     *   W1: send(8,4) => count=2, OK, +1=3.
     *   W2: send(8,6) => count=3, OK, +1=4.
     *   Receives: from (7,8) in w1? No, wave1 has (7,3) not (7,8).
     *   No receives for U8 in waves 1-2. So U8=4.
     *   W3: send(8,5) => count=4, OK, +1=5.
     */
    private void seedTransferWave3() {
        transfer(8, 5, 500);
        transfer(9, 0, 400);
    }

    private void transfer(int from, int to, double amount) {
        transactionService.transferBetweenUsers(
                primaryWallets[from], primaryWallets[to], amount);
    }

    // ── Transacciones sospechosas ───────────────────────────────
    // Before suspicious txs, boost users 8 and 9 to SILVER
    // (limit 15) so the high-frequency transfers don't get rejected.

    private void seedSuspiciousTransactions() {
        boostToSilverForFraudTest();
        seedHighFrequencyFraud();
        seedAnomalousAmountFraud();
    }

    private void boostToSilverForFraudTest() {
        User u8 = userService.findUserById(userIds[8]);
        int need8 = Math.max(0, 510 - u8.getPoints());
        if (need8 > 0) {
            rewardService.addPoints(u8, need8);
        }
        User u9 = userService.findUserById(userIds[9]);
        int need9 = Math.max(0, 510 - u9.getPoints());
        if (need9 > 0) {
            rewardService.addPoints(u9, need9);
        }
    }

    /**
     * Usuario 8: depósito de fondos seguido de 6 transferencias
     * rápidas al mismo destino para disparar la alerta de alta
     * frecuencia ({@literal >}5 transacciones en 1 minuto).
     */
    private void seedHighFrequencyFraud() {
        transactionService.depositToWallet(
                primaryWallets[8], 100_000);
        for (int i = 0; i < 6; i++) {
            transactionService.transferBetweenUsers(
                    primaryWallets[8], primaryWallets[0], 1_000);
        }
    }

    /**
     * Usuario 9: depósito de monto 5x mayor al promedio para
     * disparar la alerta de monto anómalo (umbral 3x en el servicio).
     */
    private void seedAnomalousAmountFraud() {
        transactionService.depositToWallet(
                primaryWallets[9], 500_000);
    }

    // ── Ajuste de puntos para niveles ───────────────────────────
    // Se ejecuta DESPUÉS de todas las transacciones para fijar
    // cada usuario en el nivel deseado.
    // Platinum >=5001 | Gold 1001-5000 | Silver 501-1000 | Bronze 0-500
    // Users 0,1 -> Platinum | 2,3 -> Gold | 4,5 -> Silver | 6,7,8,9 -> Bronze

    private void adjustPointsForLevelDistribution() {
        boostToTarget(0, 5500);
        boostToTarget(1, 5200);
        boostToTarget(2, 2500);
        boostToTarget(3, 1500);
        boostToTarget(4, 800);
        boostToTarget(5, 600);
        capToBronze(6);
        capToBronze(7);
        capToBronze(8);
        capToBronze(9);
    }

    private void boostToTarget(int index, int target) {
        User user = userService.findUserById(userIds[index]);
        int current = user.getPoints();
        if (current < target) {
            rewardService.addPoints(user, target - current);
        }
    }

    private void capToBronze(int index) {
        User user = userService.findUserById(userIds[index]);
        int current = user.getPoints();
        if (current > 400) {
            user.setPoints(400);
            rewardService.updatePointsRanking(
                    user, current, 400);
        }
    }

    // ── Operaciones programadas ─────────────────────────────────
    // 10 operaciones: 4 pasadas (ya vencidas) y 6 futuras.

    private void seedScheduledOperations() {
        LocalDateTime now = LocalDateTime.now();
        seedPastScheduled(now);
        seedFutureScheduled(now);
    }

    private void seedPastScheduled(LocalDateTime now) {
        scheduledOperationService.scheduleOperation(
                new ScheduledOperation(now.minusDays(3),
                        TransactionType.DEPOSIT, 5_000,
                        primaryWallets[0], null,
                        "Depósito programado semanal"));
        scheduledOperationService.scheduleOperation(
                new ScheduledOperation(now.minusDays(1),
                        TransactionType.DEPOSIT, 3_000,
                        primaryWallets[1], null,
                        "Depósito programado quincenal"));
        scheduledOperationService.scheduleOperation(
                new ScheduledOperation(now.minusHours(6),
                        TransactionType.WITHDRAWAL, 2_000,
                        primaryWallets[2], null,
                        "Retiro programado mensual"));
        scheduledOperationService.scheduleOperation(
                new ScheduledOperation(now.minusHours(2),
                        TransactionType.TRANSFER, 2_500,
                        primaryWallets[3], primaryWallets[4],
                        "Pago de arriendo programado"));
    }

    private void seedFutureScheduled(LocalDateTime now) {
        scheduledOperationService.scheduleOperation(
                new ScheduledOperation(now.plusDays(1),
                        TransactionType.DEPOSIT, 10_000,
                        primaryWallets[5], null,
                        "Depósito nómina próxima"));
        scheduledOperationService.scheduleOperation(
                new ScheduledOperation(now.plusDays(3),
                        TransactionType.TRANSFER, 4_500,
                        primaryWallets[6], primaryWallets[7],
                        "Pago servicio mensual"));
        scheduledOperationService.scheduleOperation(
                new ScheduledOperation(now.plusDays(7),
                        TransactionType.WITHDRAWAL, 1_500,
                        primaryWallets[8], null,
                        "Retiro para gastos semanales"));
        scheduledOperationService.scheduleOperation(
                new ScheduledOperation(now.plusDays(14),
                        TransactionType.TRANSFER, 6_000,
                        primaryWallets[9], primaryWallets[0],
                        "Transferencia cuota préstamo"));
        scheduledOperationService.scheduleOperation(
                new ScheduledOperation(now.plusDays(30),
                        TransactionType.DEPOSIT, 20_000,
                        primaryWallets[4], null,
                        "Depósito ahorro programado"));
        scheduledOperationService.scheduleOperation(
                new ScheduledOperation(now.plusHours(12),
                        TransactionType.TRANSFER, 3_500,
                        primaryWallets[1], primaryWallets[3],
                        "Pago servicios públicos"));
    }
}
