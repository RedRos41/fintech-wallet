package com.uniquindio.fintech.model;

import java.util.Objects;

/**
 * Entrada que asocia un usuario con sus puntos acumulados.
 * <p>Diseñada como wrapper para su uso en un árbol binario de búsqueda (BST),
 * se ordena primero por puntos y luego por identificador de usuario
 * para garantizar unicidad en el árbol.</p>
 */
public class UserPointsEntry implements Comparable<UserPointsEntry> {

    private final String userId;
    private int points;

    /**
     * Crea una nueva entrada de puntos de usuario.
     *
     * @param userId cédula del usuario
     * @param points puntos acumulados
     */
    public UserPointsEntry(String userId, int points) {
        this.userId = userId;
        this.points = points;
    }

    /**
     * Retorna la cédula del usuario.
     *
     * @return id del usuario
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Retorna los puntos acumulados.
     *
     * @return puntos
     */
    public int getPoints() {
        return points;
    }

    /**
     * Establece los puntos acumulados.
     *
     * @param points nuevos puntos
     */
    public void setPoints(int points) {
        this.points = points;
    }

    /**
     * Compara esta entrada con otra, primero por puntos y luego por userId.
     * <p>Esto garantiza un orden total consistente para el BST,
     * evitando colisiones cuando dos usuarios tienen los mismos puntos.</p>
     *
     * @param other la otra entrada a comparar
     * @return valor negativo, cero o positivo según el orden natural
     */
    @Override
    public int compareTo(UserPointsEntry other) {
        int cmp = Integer.compare(this.points, other.points);
        if (cmp != 0) {
            return cmp;
        }
        return this.userId.compareTo(other.userId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPointsEntry that = (UserPointsEntry) o;
        return points == that.points
                && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, points);
    }

    @Override
    public String toString() {
        return "UserPointsEntry{" +
                "userId='" + userId + '\'' +
                ", points=" + points +
                '}';
    }
}
