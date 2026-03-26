package com.uniquindio.fintech.model.enums;

/**
 * Niveles de usuario basados en puntos acumulados.
 * <p>Cada nivel define un rango de puntos, una tasa de comisión
 * y un límite máximo de transacciones diarias.</p>
 */
public enum UserLevel {

    /** Nivel bronce: 5% de comisión, máximo 5 transacciones diarias. */
    BRONZE(0, 500, 0.05, 5),

    /** Nivel plata: 3% de comisión, máximo 15 transacciones diarias. */
    SILVER(501, 1000, 0.03, 15),

    /** Nivel oro: 1% de comisión, máximo 30 transacciones diarias. */
    GOLD(1001, 5000, 0.01, 30),

    /** Nivel platino: 0% de comisión, máximo 100 transacciones diarias. */
    PLATINUM(5001, Integer.MAX_VALUE, 0.0, 100);

    private final int minPoints;
    private final int maxPoints;
    private final double commissionRate;
    private final int maxDailyTransactions;

    /**
     * Constructor del nivel de usuario.
     *
     * @param minPoints            puntos mínimos requeridos para este nivel
     * @param maxPoints            puntos máximos del rango de este nivel
     * @param commissionRate       tasa de comisión aplicada (0.0 a 1.0)
     * @param maxDailyTransactions número máximo de transacciones diarias permitidas
     */
    UserLevel(int minPoints, int maxPoints,
              double commissionRate, int maxDailyTransactions) {
        this.minPoints = minPoints;
        this.maxPoints = maxPoints;
        this.commissionRate = commissionRate;
        this.maxDailyTransactions = maxDailyTransactions;
    }

    /**
     * Retorna los puntos mínimos requeridos para alcanzar este nivel.
     *
     * @return puntos mínimos del nivel
     */
    public int getMinPoints() {
        return minPoints;
    }

    /**
     * Retorna los puntos máximos del rango de este nivel.
     *
     * @return puntos máximos del nivel
     */
    public int getMaxPoints() {
        return maxPoints;
    }

    /**
     * Retorna la tasa de comisión aplicada en este nivel.
     *
     * @return tasa de comisión (valor entre 0.0 y 1.0)
     */
    public double getCommissionRate() {
        return commissionRate;
    }

    /**
     * Retorna el número máximo de transacciones diarias permitidas.
     *
     * @return límite de transacciones diarias
     */
    public int getMaxDailyTransactions() {
        return maxDailyTransactions;
    }

    /**
     * Determina el nivel de usuario correspondiente a la cantidad de puntos dada.
     *
     * @param points los puntos acumulados del usuario
     * @return el {@link UserLevel} correspondiente al rango de puntos
     * @throws IllegalArgumentException si los puntos son negativos
     */
    public static UserLevel fromPoints(int points) {
        if (points < 0) {
            throw new IllegalArgumentException(
                    "Los puntos no pueden ser negativos: " + points);
        }
        for (UserLevel level : values()) {
            if (points >= level.minPoints && points <= level.maxPoints) {
                return level;
            }
        }
        return PLATINUM;
    }
}
