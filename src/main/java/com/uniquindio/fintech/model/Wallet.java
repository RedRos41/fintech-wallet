package com.uniquindio.fintech.model;

import com.uniquindio.fintech.datastructures.list.DoublyLinkedList;
import com.uniquindio.fintech.model.enums.WalletType;

import java.util.Objects;
import java.util.UUID;

/**
 * Representa una billetera virtual dentro del sistema.
 * <p>Cada billetera tiene un código generado automáticamente, un tipo,
 * un saldo y un historial de transacciones.</p>
 */
public class Wallet {

    private final String code;
    private String name;
    private WalletType type;
    private double balance;
    private boolean active;
    private final String ownerId;
    private final DoublyLinkedList<Transaction> transactionHistory;

    /**
     * Crea una nueva billetera con código autogenerado.
     *
     * @param name    nombre de la billetera
     * @param type    tipo de billetera
     * @param ownerId cédula del propietario
     */
    public Wallet(String name, WalletType type, String ownerId) {
        this.code = UUID.randomUUID().toString();
        this.name = name;
        this.type = type;
        this.balance = 0.0;
        this.active = true;
        this.ownerId = ownerId;
        this.transactionHistory = new DoublyLinkedList<>();
    }

    /**
     * Retorna el código único de la billetera.
     *
     * @return código de la billetera
     */
    public String getCode() {
        return code;
    }

    /**
     * Retorna el nombre de la billetera.
     *
     * @return nombre
     */
    public String getName() {
        return name;
    }

    /**
     * Establece el nombre de la billetera.
     *
     * @param name nuevo nombre
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retorna el tipo de billetera.
     *
     * @return tipo de billetera
     */
    public WalletType getType() {
        return type;
    }

    /**
     * Establece el tipo de billetera.
     *
     * @param type nuevo tipo
     */
    public void setType(WalletType type) {
        this.type = type;
    }

    /**
     * Retorna el saldo actual de la billetera.
     *
     * @return saldo
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Establece el saldo de la billetera.
     *
     * @param balance nuevo saldo
     */
    public void setBalance(double balance) {
        this.balance = balance;
    }

    /**
     * Indica si la billetera está activa.
     *
     * @return true si la billetera está activa
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Establece el estado de activación de la billetera.
     *
     * @param active true para activar, false para desactivar
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Retorna la cédula del propietario de la billetera.
     *
     * @return id del propietario
     */
    public String getOwnerId() {
        return ownerId;
    }

    /**
     * Retorna el historial de transacciones de la billetera.
     *
     * @return lista doblemente enlazada de transacciones
     */
    public DoublyLinkedList<Transaction> getTransactionHistory() {
        return transactionHistory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wallet wallet = (Wallet) o;
        return Objects.equals(code, wallet.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", balance=" + balance +
                ", active=" + active +
                ", ownerId='" + ownerId + '\'' +
                '}';
    }
}
