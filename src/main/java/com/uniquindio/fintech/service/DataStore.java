package com.uniquindio.fintech.service;

import com.uniquindio.fintech.datastructures.graph.Graph;
import com.uniquindio.fintech.datastructures.hash.HashTable;
import com.uniquindio.fintech.datastructures.list.SimpleLinkedList;
import com.uniquindio.fintech.datastructures.queue.PriorityQueue;
import com.uniquindio.fintech.datastructures.queue.Queue;
import com.uniquindio.fintech.datastructures.tree.BinarySearchTree;
import com.uniquindio.fintech.model.AuditEvent;
import com.uniquindio.fintech.model.Notification;
import com.uniquindio.fintech.model.RewardBenefit;
import com.uniquindio.fintech.model.ScheduledOperation;
import com.uniquindio.fintech.model.User;
import com.uniquindio.fintech.model.UserPointsEntry;
import com.uniquindio.fintech.model.Wallet;
import org.springframework.stereotype.Service;

/**
 * Almacén central de datos de la aplicación (singleton).
 * <p>Contiene todas las estructuras de datos principales del sistema:
 * usuarios, billeteras, operaciones programadas, grafo de transferencias,
 * ranking de puntos, notificaciones pendientes, registro de auditoría
 * y catálogo de beneficios.</p>
 */
@Service
public class DataStore {

    private final HashTable<String, User> usersById;
    private final HashTable<String, Wallet> walletsByCode;
    private final PriorityQueue<ScheduledOperation> scheduledOps;
    private final Graph<String> transferGraph;
    private final BinarySearchTree<UserPointsEntry> pointsRanking;
    private final Queue<Notification> pendingNotifications;
    private final SimpleLinkedList<AuditEvent> auditLog;
    private final SimpleLinkedList<RewardBenefit> benefitCatalog;

    /**
     * Inicializa todas las estructuras de datos vacías.
     */
    public DataStore() {
        this.usersById = new HashTable<>();
        this.walletsByCode = new HashTable<>();
        this.scheduledOps = new PriorityQueue<>();
        this.transferGraph = new Graph<>();
        this.pointsRanking = new BinarySearchTree<>();
        this.pendingNotifications = new Queue<>();
        this.auditLog = new SimpleLinkedList<>();
        this.benefitCatalog = new SimpleLinkedList<>();
    }

    /**
     * Retorna la tabla hash de usuarios indexados por cédula.
     *
     * @return tabla hash de usuarios
     */
    public HashTable<String, User> getUsersById() {
        return usersById;
    }

    /**
     * Retorna la tabla hash de billeteras indexadas por código.
     *
     * @return tabla hash de billeteras
     */
    public HashTable<String, Wallet> getWalletsByCode() {
        return walletsByCode;
    }

    /**
     * Retorna la cola de prioridad de operaciones programadas.
     *
     * @return cola de prioridad de operaciones programadas
     */
    public PriorityQueue<ScheduledOperation> getScheduledOps() {
        return scheduledOps;
    }

    /**
     * Retorna el grafo de transferencias entre usuarios.
     *
     * @return grafo dirigido de transferencias (userId a userId)
     */
    public Graph<String> getTransferGraph() {
        return transferGraph;
    }

    /**
     * Retorna el árbol binario de búsqueda del ranking de puntos.
     *
     * @return BST de entradas de puntos de usuario
     */
    public BinarySearchTree<UserPointsEntry> getPointsRanking() {
        return pointsRanking;
    }

    /**
     * Retorna la cola de notificaciones pendientes.
     *
     * @return cola de notificaciones
     */
    public Queue<Notification> getPendingNotifications() {
        return pendingNotifications;
    }

    /**
     * Retorna el registro de auditoría del sistema.
     *
     * @return lista enlazada simple de eventos de auditoría
     */
    public SimpleLinkedList<AuditEvent> getAuditLog() {
        return auditLog;
    }

    /**
     * Retorna el catálogo de beneficios disponibles.
     *
     * @return lista enlazada simple de beneficios de recompensa
     */
    public SimpleLinkedList<RewardBenefit> getBenefitCatalog() {
        return benefitCatalog;
    }
}
