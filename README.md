# Plataforma Fintech de Billeteras Digitales

**Universidad del Quindio** — Ingenieria de Sistemas y Computacion
**Asignatura**: Estructuras de Datos — Periodo 2026-1

### Equipo de Desarrollo

| Integrantes |
|---|
| Derek |
| Brahian |
| Sara |

---

## Descripcion del Proyecto

Plataforma de billeteras digitales tipo fintech que permite a los usuarios gestionar multiples billeteras virtuales, realizar transacciones financieras (depositos, retiros y transferencias), programar operaciones futuras, acumular puntos y niveles, canjear beneficios y monitorear actividades sospechosas mediante un sistema de deteccion de fraude.

El proyecto fue desarrollado como parte del curso de Estructuras de Datos, aplicando de manera practica 8 estructuras de datos implementadas desde cero (sin utilizar `java.util.Collection`), integradas en un sistema web funcional con Spring Boot y Thymeleaf.

---

## Tecnologias

| Componente | Tecnologia |
|---|---|
| Lenguaje | Java 17 |
| Framework | Spring Boot 3.2.5 |
| Motor de plantillas | Thymeleaf |
| Frontend | Bootstrap 5, HTML5, CSS3 |
| Build tool | Maven |
| Testing | JUnit 5 |

---

## Instalacion y Ejecucion

### Prerrequisitos

- Java 17 o superior instalado
- Maven 3.8+ instalado (o usar el wrapper `mvnw` incluido)

### Pasos

1. Clonar el repositorio:

```bash
git clone <url-del-repositorio>
cd fintech-wallet
```

2. Compilar el proyecto:

```bash
mvn clean compile
```

3. Ejecutar las pruebas:

```bash
mvn test
```

4. Iniciar la aplicacion:

```bash
mvn spring-boot:run
```

5. Abrir en el navegador:

```
http://localhost:8080
```

> **Nota**: Los datos de prueba se cargan automaticamente al iniciar la aplicacion mediante el `DataInitializer`.

---

## Almacenamiento en Memoria

> Los datos se almacenan en memoria. Al reiniciar la aplicacion, se cargan los datos de prueba predefinidos.

Esto es una decision de diseno intencional: el proyecto se enfoca en demostrar el uso correcto y eficiente de las estructuras de datos, no en la persistencia. Toda la informacion se gestiona a traves del `DataStore`, un servicio Spring singleton que contiene todas las estructuras de datos del sistema.

---

## Estructura del Proyecto

```
fintech-wallet/
├── pom.xml
├── README.md
├── docs/
│   ├── class-diagram.md          # Diagrama de clases en Mermaid
│   ├── data-structures.md        # Documentacion detallada de estructuras de datos
│   ├── problem-description.md    # Descripcion del problema y alcance
│   └── test-cases.md             # Casos de prueba documentados
├── src/
│   ├── main/
│   │   ├── java/com/uniquindio/fintech/
│   │   │   ├── FintechWalletApplication.java
│   │   │   ├── config/
│   │   │   │   └── DataInitializer.java
│   │   │   ├── controller/
│   │   │   │   ├── AnalyticsController.java
│   │   │   │   ├── DashboardController.java
│   │   │   │   ├── NotificationController.java
│   │   │   │   ├── RewardController.java
│   │   │   │   ├── ScheduledController.java
│   │   │   │   ├── TransactionController.java
│   │   │   │   ├── UserController.java
│   │   │   │   └── WalletController.java
│   │   │   ├── datastructures/
│   │   │   │   ├── graph/
│   │   │   │   │   └── Graph.java
│   │   │   │   ├── hash/
│   │   │   │   │   └── HashTable.java
│   │   │   │   ├── list/
│   │   │   │   │   ├── DoublyLinkedList.java
│   │   │   │   │   └── SimpleLinkedList.java
│   │   │   │   ├── queue/
│   │   │   │   │   ├── PriorityQueue.java
│   │   │   │   │   └── Queue.java
│   │   │   │   ├── stack/
│   │   │   │   │   └── Stack.java
│   │   │   │   └── tree/
│   │   │   │       └── BinarySearchTree.java
│   │   │   ├── model/
│   │   │   │   ├── AuditEvent.java
│   │   │   │   ├── Notification.java
│   │   │   │   ├── RewardBenefit.java
│   │   │   │   ├── ScheduledOperation.java
│   │   │   │   ├── Transaction.java
│   │   │   │   ├── User.java
│   │   │   │   ├── UserPointsEntry.java
│   │   │   │   ├── Wallet.java
│   │   │   │   └── enums/
│   │   │   │       ├── NotificationType.java
│   │   │   │       ├── RiskLevel.java
│   │   │   │       ├── TransactionStatus.java
│   │   │   │       ├── TransactionType.java
│   │   │   │       ├── UserLevel.java
│   │   │   │       └── WalletType.java
│   │   │   └── service/
│   │   │       ├── AnalyticsService.java
│   │   │       ├── DataStore.java
│   │   │       ├── FraudDetectionService.java
│   │   │       ├── NotificationService.java
│   │   │       ├── ReversalService.java
│   │   │       ├── RewardService.java
│   │   │       ├── ScheduledOperationService.java
│   │   │       ├── TransactionService.java
│   │   │       ├── UserService.java
│   │   │       └── WalletService.java
│   │   └── resources/
│   │       └── templates/          # Vistas Thymeleaf
│   └── test/
│       └── java/com/uniquindio/fintech/
│           ├── datastructures/     # Tests de las 8 estructuras de datos
│           └── service/            # Tests de servicios
```

---

## Estructuras de Datos Implementadas

El proyecto implementa **8 estructuras de datos genericas** desde cero, cada una con un proposito especifico dentro del sistema:

| # | Estructura | Uso en el proyecto | Documentacion |
|---|---|---|---|
| 1 | **SimpleLinkedList** | Billeteras, notificaciones, cubetas hash, base de Stack/Queue | [Ver detalle](docs/data-structures.md#1-simplelinkedlist--lista-enlazada-simple) |
| 2 | **DoublyLinkedList** | Historial de transacciones (recorrido bidireccional) | [Ver detalle](docs/data-structures.md#2-doublylinkedlist--lista-doblemente-enlazada) |
| 3 | **Stack** | Pila de deshacer operaciones (undo) | [Ver detalle](docs/data-structures.md#3-stack--pila) |
| 4 | **Queue** | Cola de notificaciones pendientes (FIFO) | [Ver detalle](docs/data-structures.md#4-queue--cola) |
| 5 | **PriorityQueue** | Operaciones programadas ordenadas por fecha | [Ver detalle](docs/data-structures.md#5-priorityqueue--cola-de-prioridad) |
| 6 | **BinarySearchTree** | Ranking de puntos de usuarios | [Ver detalle](docs/data-structures.md#6-binarysearchtree--arbol-binario-de-busqueda) |
| 7 | **HashTable** | Busqueda O(1) de usuarios por cedula y billeteras por codigo | [Ver detalle](docs/data-structures.md#7-hashtable--tabla-hash) |
| 8 | **Graph** | Red de transferencias, deteccion de ciclos, BFS/DFS | [Ver detalle](docs/data-structures.md#8-graph--grafo) |

Para la documentacion completa de cada estructura (mecanismo interno, justificacion tecnica y tabla de complejidad), consultar [docs/data-structures.md](docs/data-structures.md).

---

## Modulos del Sistema

| Modulo | Descripcion |
|---|---|
| **Gestion de Usuarios** | CRUD completo con validaciones, niveles automaticos |
| **Billeteras** | Creacion de multiples billeteras por usuario (5 tipos) |
| **Transacciones** | Depositos, retiros y transferencias con comisiones y limites |
| **Reversion** | Deshacer la ultima operacion (devolucion de saldos y puntos) |
| **Operaciones Programadas** | Programar transacciones futuras con ejecucion automatica |
| **Recompensas** | Puntos por transaccion, 4 niveles, canje de beneficios |
| **Deteccion de Fraude** | 4 reglas heuristicas, registro de auditoria |
| **Notificaciones** | 6 tipos de alertas para el usuario |
| **Analiticas** | Estadisticas, rankings, deteccion de ciclos en transferencias |

---

## Documentacion Adicional

| Documento | Descripcion |
|---|---|
| [Diagrama de Clases](docs/class-diagram.md) | Diagrama completo en formato Mermaid con estructuras, modelos, servicios y relaciones |
| [Estructuras de Datos](docs/data-structures.md) | Documentacion detallada de las 8 estructuras implementadas |
| [Descripcion del Problema](docs/problem-description.md) | Contexto, necesidades y alcance del sistema |
| [Casos de Prueba](docs/test-cases.md) | 30 casos de prueba funcionales documentados |

---

## Capturas de Pantalla

### Dashboard Principal
> Vista general del sistema con resumen de usuarios, billeteras y transacciones.

### Gestion de Usuarios
> Listado de usuarios con opciones de crear, editar y eliminar.

### Billeteras del Usuario
> Vista de las billeteras de un usuario con saldos y tipo.

### Realizar Transaccion
> Formulario para depositar, retirar o transferir fondos.

### Historial de Transacciones
> Listado de transacciones con estado, monto y puntos generados.

### Operaciones Programadas
> Cola de operaciones futuras pendientes de ejecucion.

### Sistema de Recompensas
> Ranking de puntos, nivel del usuario y catalogo de beneficios.

### Analiticas y Estadisticas
> Graficas de billeteras mas usadas, usuarios mas activos y red de transferencias.

### Deteccion de Fraude
> Registro de eventos de auditoria con niveles de riesgo.
