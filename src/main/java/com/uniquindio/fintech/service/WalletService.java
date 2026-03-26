package com.uniquindio.fintech.service;

import com.uniquindio.fintech.datastructures.list.SimpleLinkedList;
import com.uniquindio.fintech.model.User;
import com.uniquindio.fintech.model.Wallet;
import com.uniquindio.fintech.model.enums.WalletType;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de la gestión de billeteras virtuales.
 * <p>Permite crear, buscar, desactivar billeteras y consultar
 * las billeteras de un usuario específico.</p>
 */
@Service
public class WalletService {

    private final DataStore dataStore;

    /**
     * Crea el servicio de billeteras con inyección del almacén de datos.
     *
     * @param dataStore almacén central de datos
     */
    public WalletService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    /**
     * Crea una nueva billetera para un usuario existente.
     * <p>Genera un código único automáticamente, la agrega a la lista
     * de billeteras del usuario y al registro global de billeteras.</p>
     *
     * @param ownerId identificador del usuario propietario
     * @param name    nombre de la billetera
     * @param type    tipo de billetera
     * @return la billetera creada
     * @throws IllegalArgumentException si el usuario no existe o los datos son inválidos
     */
    public Wallet createWallet(String ownerId, String name,
                               WalletType type) {
        validateNotBlank(ownerId, "El identificador del propietario");
        validateNotBlank(name, "El nombre de la billetera");
        if (type == null) {
            throw new IllegalArgumentException(
                    "El tipo de billetera no puede ser nulo");
        }
        if (!dataStore.getUsersById().containsKey(ownerId)) {
            throw new IllegalArgumentException(
                    "No existe un usuario con la cédula: " + ownerId);
        }
        User owner = dataStore.getUsersById().get(ownerId);
        Wallet wallet = new Wallet(name, type, ownerId);
        owner.getWallets().addLast(wallet);
        dataStore.getWalletsByCode().put(wallet.getCode(), wallet);
        return wallet;
    }

    /**
     * Busca una billetera por su código único.
     *
     * @param code código de la billetera
     * @return la billetera encontrada
     * @throws IllegalArgumentException si la billetera no existe
     */
    public Wallet findWalletByCode(String code) {
        validateNotBlank(code, "El código de la billetera");
        if (!dataStore.getWalletsByCode().containsKey(code)) {
            throw new IllegalArgumentException(
                    "No existe una billetera con el código: " + code);
        }
        return dataStore.getWalletsByCode().get(code);
    }

    /**
     * Desactiva una billetera existente.
     * <p>Valida que el saldo sea cero antes de desactivarla.</p>
     *
     * @param code código de la billetera a desactivar
     * @throws IllegalArgumentException si la billetera no existe o tiene saldo
     */
    public void deactivateWallet(String code) {
        Wallet wallet = findWalletByCode(code);
        if (wallet.getBalance() > 0) {
            throw new IllegalArgumentException(
                    "No se puede desactivar la billetera " + code
                    + " porque tiene saldo: " + wallet.getBalance());
        }
        wallet.setActive(false);
    }

    /**
     * Retorna todas las billeteras de un usuario específico.
     *
     * @param userId cédula del usuario
     * @return lista enlazada simple de billeteras del usuario
     * @throws IllegalArgumentException si el usuario no existe
     */
    public SimpleLinkedList<Wallet> getUserWallets(String userId) {
        validateNotBlank(userId, "El identificador del usuario");
        if (!dataStore.getUsersById().containsKey(userId)) {
            throw new IllegalArgumentException(
                    "No existe un usuario con la cédula: " + userId);
        }
        return dataStore.getUsersById().get(userId).getWallets();
    }

    /**
     * Valida que un campo de texto no sea nulo ni vacío.
     *
     * @param value     valor a validar
     * @param fieldName nombre del campo para el mensaje de error
     */
    private void validateNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " no puede ser nulo o vacío");
        }
    }
}
