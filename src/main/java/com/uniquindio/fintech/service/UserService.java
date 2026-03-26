package com.uniquindio.fintech.service;

import com.uniquindio.fintech.datastructures.list.SimpleLinkedList;
import com.uniquindio.fintech.model.User;
import com.uniquindio.fintech.model.UserPointsEntry;
import com.uniquindio.fintech.model.Wallet;
import com.uniquindio.fintech.model.enums.UserLevel;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de la gestión de usuarios del sistema.
 * <p>Permite registrar, buscar, actualizar y eliminar usuarios,
 * así como consultar la lista completa de usuarios registrados.</p>
 */
@Service
public class UserService {

    private final DataStore dataStore;

    /**
     * Crea el servicio de usuarios con inyección del almacén de datos.
     *
     * @param dataStore almacén central de datos
     */
    public UserService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    /**
     * Registra un nuevo usuario en el sistema con nivel BRONZE.
     * <p>Valida que no exista un usuario con la misma cédula.
     * Además, agrega el vértice al grafo de transferencias
     * y la entrada inicial al ranking de puntos.</p>
     *
     * @param id    cédula del usuario
     * @param name  nombre completo
     * @param email correo electrónico
     * @param phone número de teléfono
     * @return el usuario creado
     * @throws IllegalArgumentException si la cédula ya está registrada
     */
    public User registerUser(String id, String name,
                             String email, String phone) {
        validateNotBlank(id, "La cédula del usuario");
        validateNotBlank(name, "El nombre del usuario");
        validateNotBlank(email, "El correo electrónico");
        validateNotBlank(phone, "El número de teléfono");
        if (dataStore.getUsersById().containsKey(id)) {
            throw new IllegalArgumentException(
                    "Ya existe un usuario registrado con la cédula: " + id);
        }
        User user = new User(id, name, email, phone);
        user.setLevel(UserLevel.BRONZE);
        dataStore.getUsersById().put(id, user);
        dataStore.getTransferGraph().addVertex(id);
        dataStore.getPointsRanking().insert(
                new UserPointsEntry(id, 0));
        return user;
    }

    /**
     * Busca un usuario por su cédula.
     *
     * @param id cédula del usuario
     * @return el usuario encontrado
     * @throws IllegalArgumentException si el usuario no existe
     */
    public User findUserById(String id) {
        validateNotBlank(id, "La cédula del usuario");
        if (!dataStore.getUsersById().containsKey(id)) {
            throw new IllegalArgumentException(
                    "No existe un usuario con la cédula: " + id);
        }
        return dataStore.getUsersById().get(id);
    }

    /**
     * Elimina un usuario del sistema.
     * <p>Valida que ninguna de sus billeteras tenga saldo antes de eliminarlo.
     * Remueve también su vértice del grafo y su entrada del ranking.</p>
     *
     * @param id cédula del usuario a eliminar
     * @throws IllegalArgumentException si el usuario no existe o tiene saldo
     */
    public void deleteUser(String id) {
        User user = findUserById(id);
        for (Wallet wallet : user.getWallets()) {
            if (wallet.getBalance() > 0) {
                throw new IllegalArgumentException(
                        "No se puede eliminar el usuario " + id
                        + " porque la billetera " + wallet.getCode()
                        + " tiene saldo: " + wallet.getBalance());
            }
        }
        for (Wallet wallet : user.getWallets()) {
            dataStore.getWalletsByCode().remove(wallet.getCode());
        }
        dataStore.getUsersById().remove(id);
        dataStore.getTransferGraph().removeVertex(id);
        dataStore.getPointsRanking().delete(
                new UserPointsEntry(id, user.getPoints()));
    }

    /**
     * Retorna la lista de todos los usuarios registrados en el sistema.
     *
     * @return lista enlazada simple de usuarios
     */
    public SimpleLinkedList<User> getAllUsers() {
        return dataStore.getUsersById().values();
    }

    /**
     * Actualiza los datos personales de un usuario existente.
     *
     * @param id    cédula del usuario
     * @param name  nuevo nombre
     * @param email nuevo correo electrónico
     * @param phone nuevo número de teléfono
     * @return el usuario actualizado
     * @throws IllegalArgumentException si el usuario no existe
     */
    public User updateUser(String id, String name,
                           String email, String phone) {
        User user = findUserById(id);
        validateNotBlank(name, "El nombre del usuario");
        validateNotBlank(email, "El correo electrónico");
        validateNotBlank(phone, "El número de teléfono");
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        return user;
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
