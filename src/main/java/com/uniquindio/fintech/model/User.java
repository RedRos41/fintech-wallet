package com.uniquindio.fintech.model;

import com.uniquindio.fintech.datastructures.list.DoublyLinkedList;
import com.uniquindio.fintech.datastructures.list.SimpleLinkedList;
import com.uniquindio.fintech.datastructures.stack.Stack;
import com.uniquindio.fintech.model.enums.UserLevel;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa un usuario del sistema de billeteras fintech.
 * <p>Contiene información personal, billeteras, historial de transacciones,
 * pila de deshacer, notificaciones, beneficios canjeados, puntos y nivel.</p>
 */
public class User {

    private final String id;
    private String name;
    private String email;
    private String phone;
    private final LocalDateTime registrationDate;
    private final SimpleLinkedList<Wallet> wallets;
    private final DoublyLinkedList<Transaction> transactionHistory;
    private final Stack<Transaction> undoStack;
    private final SimpleLinkedList<Notification> notifications;
    private final SimpleLinkedList<RewardBenefit> redeemedBenefits;
    private int points;
    private UserLevel level;

    /**
     * Crea un nuevo usuario con la información proporcionada.
     *
     * @param id    cédula del usuario
     * @param name  nombre completo
     * @param email correo electrónico
     * @param phone número de teléfono
     */
    public User(String id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.registrationDate = LocalDateTime.now();
        this.wallets = new SimpleLinkedList<>();
        this.transactionHistory = new DoublyLinkedList<>();
        this.undoStack = new Stack<>();
        this.notifications = new SimpleLinkedList<>();
        this.redeemedBenefits = new SimpleLinkedList<>();
        this.points = 0;
        this.level = UserLevel.BRONZE;
    }

    /**
     * Retorna la cédula del usuario.
     *
     * @return id del usuario
     */
    public String getId() {
        return id;
    }

    /**
     * Retorna el nombre del usuario.
     *
     * @return nombre
     */
    public String getName() {
        return name;
    }

    /**
     * Establece el nombre del usuario.
     *
     * @param name nuevo nombre
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retorna el correo electrónico del usuario.
     *
     * @return correo electrónico
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece el correo electrónico del usuario.
     *
     * @param email nuevo correo electrónico
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Retorna el número de teléfono del usuario.
     *
     * @return teléfono
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Establece el número de teléfono del usuario.
     *
     * @param phone nuevo número de teléfono
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Retorna la fecha y hora de registro del usuario.
     *
     * @return fecha de registro
     */
    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    /**
     * Retorna la lista de billeteras del usuario.
     *
     * @return lista enlazada simple de billeteras
     */
    public SimpleLinkedList<Wallet> getWallets() {
        return wallets;
    }

    /**
     * Retorna el historial de transacciones del usuario.
     *
     * @return lista doblemente enlazada de transacciones
     */
    public DoublyLinkedList<Transaction> getTransactionHistory() {
        return transactionHistory;
    }

    /**
     * Retorna la pila de transacciones para deshacer.
     *
     * @return pila de transacciones
     */
    public Stack<Transaction> getUndoStack() {
        return undoStack;
    }

    /**
     * Retorna las notificaciones del usuario.
     *
     * @return lista enlazada simple de notificaciones
     */
    public SimpleLinkedList<Notification> getNotifications() {
        return notifications;
    }

    /**
     * Retorna los beneficios canjeados por el usuario.
     *
     * @return lista enlazada simple de beneficios canjeados
     */
    public SimpleLinkedList<RewardBenefit> getRedeemedBenefits() {
        return redeemedBenefits;
    }

    /**
     * Retorna los puntos acumulados del usuario.
     *
     * @return puntos
     */
    public int getPoints() {
        return points;
    }

    /**
     * Establece los puntos acumulados del usuario.
     *
     * @param points nuevos puntos
     */
    public void setPoints(int points) {
        this.points = points;
    }

    /**
     * Retorna el nivel actual del usuario.
     *
     * @return nivel del usuario
     */
    public UserLevel getLevel() {
        return level;
    }

    /**
     * Establece el nivel del usuario.
     *
     * @param level nuevo nivel
     */
    public void setLevel(UserLevel level) {
        this.level = level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", registrationDate=" + registrationDate +
                ", points=" + points +
                ", level=" + level +
                '}';
    }
}
