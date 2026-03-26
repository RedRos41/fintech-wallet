package com.uniquindio.fintech.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Representa un beneficio o recompensa que un usuario puede canjear con puntos.
 * <p>Los beneficios tienen un costo en puntos y registran cuándo fueron canjeados.</p>
 */
public class RewardBenefit {

    private final String id;
    private final String name;
    private final String description;
    private final int pointsCost;
    private boolean redeemed;
    private LocalDateTime redeemedDate;
    private final String userId;

    /**
     * Crea un nuevo beneficio de recompensa con identificador autogenerado.
     *
     * @param name        nombre del beneficio
     * @param description descripción del beneficio
     * @param pointsCost  costo en puntos para canjear el beneficio
     * @param userId      cédula del usuario asociado
     */
    public RewardBenefit(String name, String description,
                         int pointsCost, String userId) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.pointsCost = pointsCost;
        this.redeemed = false;
        this.redeemedDate = null;
        this.userId = userId;
    }

    /**
     * Retorna el identificador único del beneficio.
     *
     * @return id del beneficio
     */
    public String getId() {
        return id;
    }

    /**
     * Retorna el nombre del beneficio.
     *
     * @return nombre
     */
    public String getName() {
        return name;
    }

    /**
     * Retorna la descripción del beneficio.
     *
     * @return descripción
     */
    public String getDescription() {
        return description;
    }

    /**
     * Retorna el costo en puntos del beneficio.
     *
     * @return costo en puntos
     */
    public int getPointsCost() {
        return pointsCost;
    }

    /**
     * Indica si el beneficio fue canjeado.
     *
     * @return true si fue canjeado
     */
    public boolean isRedeemed() {
        return redeemed;
    }

    /**
     * Establece el estado de canje del beneficio.
     *
     * @param redeemed true si fue canjeado
     */
    public void setRedeemed(boolean redeemed) {
        this.redeemed = redeemed;
    }

    /**
     * Retorna la fecha y hora en que el beneficio fue canjeado.
     *
     * @return fecha de canje, o null si no ha sido canjeado
     */
    public LocalDateTime getRedeemedDate() {
        return redeemedDate;
    }

    /**
     * Establece la fecha y hora de canje del beneficio.
     *
     * @param redeemedDate fecha de canje
     */
    public void setRedeemedDate(LocalDateTime redeemedDate) {
        this.redeemedDate = redeemedDate;
    }

    /**
     * Retorna la cédula del usuario asociado al beneficio.
     *
     * @return id del usuario
     */
    public String getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RewardBenefit that = (RewardBenefit) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "RewardBenefit{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", pointsCost=" + pointsCost +
                ", redeemed=" + redeemed +
                ", redeemedDate=" + redeemedDate +
                ", userId='" + userId + '\'' +
                '}';
    }
}
