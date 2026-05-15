package com.uniquindio.fintech.model;

import java.util.Objects;

/**
 * Wrapper de {@link Transaction} ordenable por monto descendente.
 * <p>Diseñado para usarse en estructuras que requieren elementos
 * {@code Comparable}, como la cola de prioridad propia del proyecto,
 * cuando se necesita obtener las transacciones de mayor valor primero.</p>
 */
public class TransactionAmountEntry implements Comparable<TransactionAmountEntry> {

    private final Transaction transaction;

    /**
     * Crea una nueva entrada envolviendo la transacción dada.
     *
     * @param transaction la transacción a envolver
     */
    public TransactionAmountEntry(Transaction transaction) {
        this.transaction = transaction;
    }

    /**
     * Retorna la transacción envuelta.
     *
     * @return la transacción
     */
    public Transaction getTransaction() {
        return transaction;
    }

    /**
     * Compara dos entradas por monto descendente. En caso de empate
     * desempata por el id de la transacción para garantizar un orden total.
     *
     * @param other la otra entrada
     * @return valor negativo si esta tiene mayor monto, positivo si menor
     */
    @Override
    public int compareTo(TransactionAmountEntry other) {
        int cmp = Double.compare(other.transaction.getAmount(),
                this.transaction.getAmount());
        if (cmp != 0) {
            return cmp;
        }
        return this.transaction.getId().compareTo(other.transaction.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionAmountEntry that = (TransactionAmountEntry) o;
        return Objects.equals(transaction.getId(), that.transaction.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(transaction.getId());
    }
}
