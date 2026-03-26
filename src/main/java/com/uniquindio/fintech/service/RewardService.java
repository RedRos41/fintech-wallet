package com.uniquindio.fintech.service;

import com.uniquindio.fintech.datastructures.list.SimpleLinkedList;
import com.uniquindio.fintech.model.Notification;
import com.uniquindio.fintech.model.RewardBenefit;
import com.uniquindio.fintech.model.Transaction;
import com.uniquindio.fintech.model.User;
import com.uniquindio.fintech.model.UserPointsEntry;
import com.uniquindio.fintech.model.enums.NotificationType;
import com.uniquindio.fintech.model.enums.UserLevel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Servicio encargado de la gestión de recompensas y puntos de los usuarios.
 * <p>Calcula puntos por transacciones, actualiza el ranking en el BST,
 * gestiona el canje de beneficios y recalcula niveles de usuario.</p>
 */
@Service
public class RewardService {

    private final DataStore dataStore;
    private final NotificationService notificationService;

    /**
     * Crea el servicio de recompensas con inyección de dependencias.
     *
     * @param dataStore           almacén central de datos
     * @param notificationService servicio de notificaciones
     */
    public RewardService(DataStore dataStore,
                         NotificationService notificationService) {
        this.dataStore = dataStore;
        this.notificationService = notificationService;
    }

    /**
     * Calcula los puntos generados por una transacción según su tipo.
     * <p>DEPOSIT: 1 punto por cada 100 unidades.
     * WITHDRAWAL: 2 puntos por cada 100 unidades.
     * TRANSFER: 3 puntos por cada 100 unidades.
     * SCHEDULED_PAYMENT: puntos según tipo base + 5 de bonificación.</p>
     *
     * @param tx la transacción a evaluar
     * @return la cantidad de puntos generados
     * @throws IllegalArgumentException si la transacción es nula
     */
    public int calculatePointsForTransaction(Transaction tx) {
        if (tx == null) {
            throw new IllegalArgumentException(
                    "La transacción no puede ser nula");
        }
        int basePoints = (int) (tx.getAmount() / 100);
        switch (tx.getType()) {
            case DEPOSIT:
                return basePoints;
            case WITHDRAWAL:
                return basePoints * 2;
            case TRANSFER:
                return basePoints * 3;
            case SCHEDULED_PAYMENT:
                return basePoints + 5;
            default:
                return 0;
        }
    }

    /**
     * Agrega puntos a un usuario, actualiza el ranking en el BST
     * y recalcula su nivel. Notifica si sube de nivel.
     *
     * @param user   el usuario al que se le agregan los puntos
     * @param points cantidad de puntos a agregar
     * @throws IllegalArgumentException si el usuario es nulo o los puntos son negativos
     */
    public void addPoints(User user, int points) {
        if (user == null) {
            throw new IllegalArgumentException(
                    "El usuario no puede ser nulo");
        }
        if (points < 0) {
            throw new IllegalArgumentException(
                    "Los puntos a agregar no pueden ser negativos: " + points);
        }
        int oldPoints = user.getPoints();
        UserLevel oldLevel = user.getLevel();
        int newPoints = oldPoints + points;
        user.setPoints(newPoints);
        updatePointsRanking(user, oldPoints, newPoints);
        if (user.getLevel().ordinal() > oldLevel.ordinal()) {
            notificationService.sendNotification(user.getId(),
                    NotificationType.LEVEL_UP,
                    "¡Felicidades! Has subido al nivel "
                    + user.getLevel().name());
        }
    }

    /**
     * Canjea un beneficio del catálogo para un usuario.
     * <p>Valida que el usuario tenga puntos suficientes, resta los puntos,
     * sincroniza el BST, recalcula el nivel, marca el beneficio como canjeado
     * y lo agrega a la lista de beneficios canjeados del usuario.</p>
     *
     * @param userId    cédula del usuario
     * @param benefitId identificador del beneficio a canjear
     * @throws IllegalArgumentException si el usuario o beneficio no existen,
     *                                  o si los puntos son insuficientes
     */
    public void redeemBenefit(String userId, String benefitId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException(
                    "El identificador del usuario no puede ser nulo o vacío");
        }
        if (benefitId == null || benefitId.isBlank()) {
            throw new IllegalArgumentException(
                    "El identificador del beneficio no puede ser nulo o vacío");
        }
        User user = findUserOrThrow(userId);
        RewardBenefit benefit = findBenefitOrThrow(benefitId);
        if (user.getPoints() < benefit.getPointsCost()) {
            throw new IllegalArgumentException(
                    "Puntos insuficientes. Se requieren "
                    + benefit.getPointsCost()
                    + " pero el usuario tiene " + user.getPoints());
        }
        int oldPoints = user.getPoints();
        int newPoints = oldPoints - benefit.getPointsCost();
        user.setPoints(newPoints);
        updatePointsRanking(user, oldPoints, newPoints);
        benefit.setRedeemed(true);
        benefit.setRedeemedDate(LocalDateTime.now());
        user.getRedeemedBenefits().addLast(benefit);
        notificationService.sendNotification(userId,
                NotificationType.BENEFIT_REDEEMED,
                "Has canjeado el beneficio: " + benefit.getName());
    }

    /**
     * Actualiza la entrada del usuario en el BST de ranking de puntos.
     * <p>Elimina la entrada anterior, inserta la nueva y recalcula el nivel.</p>
     *
     * @param user      el usuario cuyo ranking se actualiza
     * @param oldPoints puntos anteriores
     * @param newPoints puntos nuevos
     */
    public void updatePointsRanking(User user, int oldPoints,
                                    int newPoints) {
        dataStore.getPointsRanking().delete(
                new UserPointsEntry(user.getId(), oldPoints));
        dataStore.getPointsRanking().insert(
                new UserPointsEntry(user.getId(), newPoints));
        user.setLevel(UserLevel.fromPoints(newPoints));
    }

    /**
     * Retorna el ranking de puntos en orden (inorden del BST).
     *
     * @return lista enlazada simple de entradas de puntos ordenadas
     */
    public SimpleLinkedList<UserPointsEntry> getRankingInOrder() {
        return dataStore.getPointsRanking().inOrder();
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
     * Busca un beneficio en el catálogo por su id o lanza excepción.
     *
     * @param benefitId identificador del beneficio
     * @return el beneficio encontrado
     */
    private RewardBenefit findBenefitOrThrow(String benefitId) {
        for (RewardBenefit b : dataStore.getBenefitCatalog()) {
            if (b.getId().equals(benefitId)) {
                return b;
            }
        }
        throw new IllegalArgumentException(
                "No existe un beneficio con el identificador: " + benefitId);
    }
}
