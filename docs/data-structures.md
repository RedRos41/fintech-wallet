# Estructuras de Datos — Plataforma Fintech de Billeteras Digitales

## Tabla de Contenido

1. [SimpleLinkedList (Lista Enlazada Simple)](#1-simplelinkedlist--lista-enlazada-simple)
2. [DoublyLinkedList (Lista Doblemente Enlazada)](#2-doublylinkedlist--lista-doblemente-enlazada)
3. [Stack (Pila)](#3-stack--pila)
4. [Queue (Cola)](#4-queue--cola)
5. [PriorityQueue (Cola de Prioridad)](#5-priorityqueue--cola-de-prioridad)
6. [BinarySearchTree (Arbol Binario de Busqueda)](#6-binarysearchtree--arbol-binario-de-busqueda)
7. [HashTable (Tabla Hash)](#7-hashtable--tabla-hash)
8. [Graph (Grafo)](#8-graph--grafo)

---

## 1. SimpleLinkedList — Lista Enlazada Simple

### 1.1 Que es?

Una lista enlazada simple es una estructura de datos lineal donde cada elemento (nodo) contiene un dato y una referencia al siguiente nodo en la secuencia. El ultimo nodo apunta a `null`, indicando el final de la lista.

### 1.2 Como funciona?

Internamente, la `SimpleLinkedList<T>` mantiene tres campos:
- **`head`**: referencia al primer nodo de la lista.
- **`tail`**: referencia al ultimo nodo de la lista (permite insercion O(1) al final).
- **`size`**: contador de elementos.

Cada **`Node<T>`** contiene:
- `data`: el dato almacenado.
- `next`: puntero al siguiente nodo.

La insercion al inicio modifica el puntero `head`; la insercion al final modifica el puntero `next` de `tail` y actualiza `tail`. La eliminacion al inicio simplemente avanza `head` al siguiente nodo. La eliminacion al final requiere recorrer toda la lista para encontrar el penultimo nodo (O(n)), ya que no hay puntero `prev`.

### 1.3 Donde se usa en el proyecto?

- **Billeteras del usuario** (`User.wallets`): almacena las billeteras asociadas a cada usuario.
- **Notificaciones del usuario** (`User.notifications`): lista de notificaciones recibidas.
- **Beneficios canjeados** (`User.redeemedBenefits`): beneficios que el usuario ha canjeado.
- **Estructura interna de Stack, Queue, PriorityQueue**: las tres estructuras usan `SimpleLinkedList` como almacenamiento interno.
- **Cubetas de la HashTable**: cada cubeta de la tabla hash es una `SimpleLinkedList<HashEntry>`.
- **Registro de auditoria** (`DataStore.auditLog`): lista de eventos de auditoria del sistema.
- **Catalogo de beneficios** (`DataStore.benefitCatalog`): beneficios disponibles para canjear.
- **Resultados de recorridos del BST y del Graph**: los metodos `inOrder()`, `bfs()`, `dfs()`, etc., retornan resultados en `SimpleLinkedList`.

### 1.4 Por que esta estructura y no otra?

La lista enlazada simple es ideal cuando se necesita insercion/eliminacion frecuente al inicio o al final en O(1), sin necesidad de acceso aleatorio. En el contexto del proyecto:
- Las billeteras y notificaciones se agregan secuencialmente y se recorren completas para mostrar en la interfaz.
- No se requiere acceso por indice frecuente; el patron de uso principal es agregar al final e iterar.
- Consume menos memoria que una lista doblemente enlazada (un solo puntero por nodo).
- Es la base ideal para implementar Stack, Queue y PriorityQueue, ya que estas solo acceden a los extremos.

### 1.5 Complejidad

| Operacion | Complejidad |
|---|---|
| `addFirst(T)` | O(1) |
| `addLast(T)` | O(1) |
| `add(int, T)` | O(n) |
| `removeFirst()` | O(1) |
| `removeLast()` | O(n) |
| `remove(int)` | O(n) |
| `get(int)` | O(n) |
| `getFirst()` | O(1) |
| `getLast()` | O(1) |
| `contains(T)` | O(n) |
| `indexOf(T)` | O(n) |
| `size()` | O(1) |
| `isEmpty()` | O(1) |

---

## 2. DoublyLinkedList — Lista Doblemente Enlazada

### 2.1 Que es?

Una lista doblemente enlazada es una estructura de datos lineal donde cada nodo tiene referencias tanto al nodo siguiente como al anterior, permitiendo recorrido bidireccional.

### 2.2 Como funciona?

Internamente, la `DoublyLinkedList<T>` mantiene:
- **`head`**: referencia al primer nodo.
- **`tail`**: referencia al ultimo nodo.
- **`size`**: contador de elementos.

Cada **`DNode<T>`** contiene:
- `data`: el dato almacenado.
- `next`: puntero al siguiente nodo.
- `prev`: puntero al nodo anterior.

Gracias al puntero `prev`, tanto la eliminacion al final como la navegacion en reversa son O(1). Ademas, la busqueda por indice esta optimizada: si el indice esta en la primera mitad de la lista, se recorre desde `head`; si esta en la segunda mitad, se recorre desde `tail`. Esto reduce el recorrido promedio a n/2.

### 2.3 Donde se usa en el proyecto?

- **Historial de transacciones del usuario** (`User.transactionHistory`): permite recorrer las transacciones de la mas antigua a la mas reciente y viceversa.
- **Historial de transacciones de la billetera** (`Wallet.transactionHistory`): cada billetera mantiene su propio historial bidireccional.

### 2.4 Por que esta estructura y no otra?

El historial de transacciones requiere:
- **Recorrido bidireccional**: mostrar transacciones de la mas reciente a la mas antigua (para la interfaz) y de la mas antigua a la mas reciente (para analisis).
- **Insercion eficiente al final**: O(1) para agregar nuevas transacciones.
- **Eliminacion eficiente al final**: O(1) gracias al puntero `prev`, a diferencia de la lista simple que requiere O(n).
- El patron de uso es analogo a un historial de navegacion: se agregan al final y se puede recorrer en ambas direcciones.

### 2.5 Complejidad

| Operacion | Complejidad |
|---|---|
| `addFirst(T)` | O(1) |
| `addLast(T)` | O(1) |
| `add(int, T)` | O(n) |
| `removeFirst()` | O(1) |
| `removeLast()` | O(1) |
| `remove(int)` | O(n) |
| `get(int)` | O(n) |
| `getFirst()` | O(1) |
| `getLast()` | O(1) |
| `contains(T)` | O(n) |
| `indexOf(T)` | O(n) |
| `size()` | O(1) |
| `isEmpty()` | O(1) |
| `iteratorReverse()` | O(1) creacion, O(n) recorrido |

---

## 3. Stack — Pila

### 3.1 Que es?

Una pila (Stack) es una estructura de datos LIFO (Last In, First Out) donde el ultimo elemento insertado es el primero en ser retirado. Solo se puede acceder al elemento en el tope.

### 3.2 Como funciona?

La `Stack<T>` utiliza internamente una `SimpleLinkedList<T>`, realizando todas las operaciones en la cabeza de la lista:
- **`push(T)`**: delega a `list.addFirst(T)`, insertando al inicio en O(1).
- **`pop()`**: delega a `list.removeFirst()`, eliminando el primer elemento en O(1).
- **`peek()`**: delega a `list.getFirst()`, consultando sin eliminar en O(1).

Esta implementacion basada en composicion (en lugar de herencia) encapsula la lista interna y expone unicamente las operaciones validas de una pila.

### 3.3 Donde se usa en el proyecto?

- **Pila de deshacer del usuario** (`User.undoStack`): cada vez que se realiza una transaccion (deposito, retiro, transferencia), se apila en la pila de deshacer. El servicio `ReversalService` desapila la ultima transaccion para revertirla, devolviendo saldos y restando puntos.

### 3.4 Por que esta estructura y no otra?

La funcionalidad de "deshacer" (undo) es el caso de uso clasico de una pila:
- La operacion mas reciente es la primera que se puede revertir (LIFO).
- Solo se necesita acceso al tope: `push` para registrar y `pop` para revertir.
- Garantiza que las reversiones se realicen en orden cronologico inverso, manteniendo la consistencia de los saldos.
- Una cola (FIFO) no serviria porque revertir la operacion mas antigua primero podria generar inconsistencias de saldo.

### 3.5 Complejidad

| Operacion | Complejidad |
|---|---|
| `push(T)` | O(1) |
| `pop()` | O(1) |
| `peek()` | O(1) |
| `isEmpty()` | O(1) |
| `size()` | O(1) |
| `clear()` | O(1) |

---

## 4. Queue — Cola

### 4.1 Que es?

Una cola (Queue) es una estructura de datos FIFO (First In, First Out) donde el primer elemento insertado es el primero en ser retirado. Los elementos se agregan al final y se retiran del frente.

### 4.2 Como funciona?

La `Queue<T>` utiliza internamente una `SimpleLinkedList<T>`:
- **`enqueue(T)`**: delega a `list.addLast(T)`, agregando al final en O(1).
- **`dequeue()`**: delega a `list.removeFirst()`, retirando del frente en O(1).
- **`peek()`**: delega a `list.getFirst()`, consultando el frente sin eliminar en O(1).

La combinacion de `addLast` (con puntero `tail`) y `removeFirst` (con puntero `head`) garantiza que ambas operaciones sean O(1).

### 4.3 Donde se usa en el proyecto?

- **Cola de notificaciones pendientes** (`DataStore.pendingNotifications`): las notificaciones del sistema se encolan conforme se generan y se procesan en orden FIFO. Esto garantiza que las notificaciones mas antiguas se atiendan primero.

### 4.4 Por que esta estructura y no otra?

Las notificaciones deben procesarse en el orden en que se generan:
- Una notificacion de alerta de seguridad emitida a las 10:00 debe procesarse antes que una de subida de nivel emitida a las 10:01.
- FIFO garantiza justicia en el orden de atencion.
- No se requiere prioridad (para eso existe la `PriorityQueue`); todas las notificaciones tienen la misma urgencia de procesamiento.
- Una pila (LIFO) atenderia primero las mas recientes, lo cual seria injusto para las mas antiguas.

### 4.5 Complejidad

| Operacion | Complejidad |
|---|---|
| `enqueue(T)` | O(1) |
| `dequeue()` | O(1) |
| `peek()` | O(1) |
| `isEmpty()` | O(1) |
| `size()` | O(1) |
| `clear()` | O(1) |

---

## 5. PriorityQueue — Cola de Prioridad

### 5.1 Que es?

Una cola de prioridad es una estructura de datos donde cada elemento tiene una prioridad asociada. El elemento con mayor prioridad (menor valor segun `Comparable`) es el primero en ser retirado, independientemente del orden de insercion.

### 5.2 Como funciona?

La `PriorityQueue<T extends Comparable<T>>` utiliza internamente una `SimpleLinkedList<T>` con **insercion ordenada**:
- **`enqueue(T)`**: busca la posicion correcta recorriendo la lista y utiliza `list.add(index, T)` para insertar en orden. Tiene optimizaciones:
  - Si el elemento es mayor o igual al ultimo, se inserta al final en O(1).
  - Si es menor o igual al primero, se inserta al inicio en O(1).
  - En caso general, se busca la posicion linealmente en O(n).
- **`dequeue()`**: delega a `list.removeFirst()`, retirando el elemento de mayor prioridad en O(1).
- **`peek()`**: delega a `list.getFirst()`, consultando el de mayor prioridad en O(1).

### 5.3 Donde se usa en el proyecto?

- **Operaciones programadas** (`DataStore.scheduledOps`): las operaciones programadas (depositos, retiros, transferencias futuras) se encolan ordenadas por fecha programada. `ScheduledOperation` implementa `Comparable` comparando por `scheduledDate`, de modo que la operacion con fecha mas temprana tiene mayor prioridad. El `ScheduledOperationService.processAllDue()` desencola y ejecuta todas las operaciones cuya fecha ya se cumplio.

### 5.4 Por que esta estructura y no otra?

Las operaciones programadas deben ejecutarse en orden cronologico:
- La operacion programada para el 1 de enero debe ejecutarse antes que la programada para el 2 de enero, sin importar en que orden fueron programadas.
- Una cola FIFO no serviria porque un usuario podria programar primero una operacion para el dia 5 y luego una para el dia 3.
- Un BST seria excesivo: solo se necesita acceso al minimo (la operacion mas proxima), no busquedas arbitrarias.
- La insercion ordenada en lista garantiza que el `dequeue` siempre retorne la operacion mas urgente.

### 5.5 Complejidad

| Operacion | Complejidad |
|---|---|
| `enqueue(T)` | O(n) |
| `dequeue()` | O(1) |
| `peek()` | O(1) |
| `isEmpty()` | O(1) |
| `size()` | O(1) |
| `clear()` | O(1) |

---

## 6. BinarySearchTree — Arbol Binario de Busqueda

### 6.1 Que es?

Un arbol binario de busqueda (BST) es una estructura de datos jerarquica donde cada nodo tiene como maximo dos hijos. Para todo nodo, los valores del subarbol izquierdo son menores y los del subarbol derecho son mayores, lo que permite busquedas eficientes.

### 6.2 Como funciona?

La `BinarySearchTree<T extends Comparable<T>>` mantiene:
- **`root`**: referencia al nodo raiz del arbol.
- **`size`**: contador de elementos.

Cada **`BSTNode<T>`** contiene:
- `data`: el dato almacenado.
- `left`: puntero al hijo izquierdo (valores menores).
- `right`: puntero al hijo derecho (valores mayores).

Las operaciones principales son recursivas:
- **Insercion**: compara el nuevo valor con el nodo actual y desciende por la rama izquierda o derecha hasta encontrar una posicion vacia.
- **Busqueda**: desciende comparando hasta encontrar el valor o llegar a `null`.
- **Eliminacion**: tiene tres casos: nodo hoja (se elimina directamente), nodo con un hijo (se reemplaza por el hijo) y nodo con dos hijos (se reemplaza por el sucesor inorden).
- **Recorrido inorden**: izquierda-raiz-derecha, produce los elementos ordenados de menor a mayor.
- **Busqueda por rango**: recorre solo las ramas relevantes, podando las que estan fuera del rango.

### 6.3 Donde se usa en el proyecto?

- **Ranking de puntos** (`DataStore.pointsRanking`): almacena entradas `UserPointsEntry` (userId + puntos) ordenadas por puntos. El recorrido inorden produce el ranking ordenado de menor a mayor puntos. Permite consultas de rango (por ejemplo, usuarios con entre 500 y 1000 puntos). Los servicios `RewardService` y `ReversalService` actualizan el BST al agregar/restar puntos (eliminan la entrada anterior e insertan la nueva).

### 6.4 Por que esta estructura y no otra?

El ranking de puntos requiere:
- **Ordenamiento dinamico**: los puntos cambian frecuentemente con cada transaccion. Un arreglo ordenado requeriria O(n) para insertar/eliminar.
- **Recorrido ordenado eficiente**: el inorden del BST produce el ranking en O(n).
- **Busquedas por rango**: encontrar usuarios en un rango de puntos es O(log n + k) en promedio.
- Una lista ordenada tendria insercion O(n) y busqueda O(n). Una tabla hash no mantiene orden.
- El BST ofrece O(log n) promedio para insercion, eliminacion y busqueda, lo cual es optimo para datos que cambian frecuentemente y necesitan estar ordenados.

### 6.5 Complejidad

| Operacion | Promedio | Peor caso |
|---|---|---|
| `insert(T)` | O(log n) | O(n) |
| `contains(T)` | O(log n) | O(n) |
| `delete(T)` | O(log n) | O(n) |
| `min()` | O(log n) | O(n) |
| `max()` | O(log n) | O(n) |
| `inOrder()` | O(n) | O(n) |
| `preOrder()` | O(n) | O(n) |
| `postOrder()` | O(n) | O(n) |
| `rangeSearch(T, T)` | O(log n + k) | O(n) |
| `height()` | O(n) | O(n) |
| `size()` | O(1) | O(1) |

> **Nota**: El peor caso O(n) ocurre cuando el arbol degenera en una lista (inserciones en orden). En la practica del proyecto, las entradas de puntos tienen orden variado, manteniendo el arbol relativamente balanceado.

---

## 7. HashTable — Tabla Hash

### 7.1 Que es?

Una tabla hash es una estructura de datos que asocia claves con valores, proporcionando acceso, insercion y eliminacion en tiempo constante amortizado O(1). Utiliza una funcion hash para convertir la clave en un indice del arreglo interno.

### 7.2 Como funciona?

La `HashTable<K, V>` utiliza **encadenamiento separado** (separate chaining):
- **`buckets`**: arreglo de `SimpleLinkedList<HashEntry<K, V>>` (cubetas). Capacidad inicial de 16.
- **`size`**: contador de entradas almacenadas.
- **Factor de carga**: 0.75. Cuando `(size + 1) / buckets.length > 0.75`, se duplica la capacidad y se reubican todas las entradas.

Cada **`HashEntry<K, V>`** contiene:
- `key`: la clave.
- `value`: el valor asociado.

**Proceso de operaciones**:
1. Se calcula el indice: `Math.abs(key.hashCode()) % buckets.length`.
2. Se accede a la cubeta correspondiente (una `SimpleLinkedList`).
3. Se recorre la cubeta buscando una entrada con la misma clave (`Objects.equals`).
4. Para `put`: si la clave existe, se actualiza el valor; si no, se agrega al final de la cubeta.
5. Para `get`: se retorna el valor si se encuentra; si no, se lanza excepcion.
6. Para `remove`: se busca el indice de la entrada en la cubeta y se elimina.

### 7.3 Donde se usa en el proyecto?

- **Usuarios por cedula** (`DataStore.usersById`): permite buscar cualquier usuario por su cedula en O(1). Es la estructura mas consultada del sistema.
- **Billeteras por codigo** (`DataStore.walletsByCode`): permite buscar cualquier billetera por su codigo UUID en O(1).
- **Lista de adyacencia del grafo** (`Graph.adjacencyList`): el grafo de transferencias usa una tabla hash para mapear cada vertice (userId) a su lista de aristas.
- **Deteccion de fraude**: `FraudDetectionService` usa tablas hash temporales para contar destinos unicos de transferencias.
- **Analiticas**: `AnalyticsService` usa tablas hash para contar frecuencias, categorias y evitar conteos duplicados.

### 7.4 Por que esta estructura y no otra?

El acceso rapido por clave es fundamental en un sistema financiero:
- Buscar un usuario por cedula o una billetera por codigo debe ser instantaneo, no O(n).
- Las operaciones transaccionales (deposito, retiro, transferencia) consultan la tabla hash multiples veces por operacion.
- Una lista enlazada requeriria O(n) para buscar por clave. Un BST requeriria O(log n) y que las claves (UUIDs, cedulas) sean comparables.
- El encadenamiento separado con `SimpleLinkedList` maneja colisiones de forma eficiente y reutiliza la estructura de datos ya implementada.
- La redimension automatica mantiene el factor de carga bajo, evitando degradacion de rendimiento.

### 7.5 Complejidad

| Operacion | Amortizado | Peor caso |
|---|---|---|
| `put(K, V)` | O(1) | O(n) |
| `get(K)` | O(1) | O(n) |
| `remove(K)` | O(1) | O(n) |
| `containsKey(K)` | O(1) | O(n) |
| `keys()` | O(n + m) | O(n + m) |
| `values()` | O(n + m) | O(n + m) |
| `size()` | O(1) | O(1) |
| `isEmpty()` | O(1) | O(1) |

> **Nota**: m = numero de cubetas, n = numero de entradas. El peor caso O(n) ocurre cuando todas las claves colisionan en la misma cubeta (extremadamente improbable con buenas funciones hash).

---

## 8. Graph — Grafo

### 8.1 Que es?

Un grafo dirigido ponderado es una estructura de datos compuesta por vertices (nodos) y aristas (conexiones dirigidas) con un peso numerico asociado. Permite modelar relaciones entre entidades.

### 8.2 Como funciona?

La `Graph<T>` utiliza **listas de adyacencia** implementadas con las propias estructuras del proyecto:
- **`adjacencyList`**: una `HashTable<T, SimpleLinkedList<Edge<T>>>` que mapea cada vertice a su lista de aristas salientes.
- **`edgeCount`**: contador de aristas totales.

Cada **`Edge<T>`** contiene:
- `destination`: vertice destino.
- `weight`: peso de la arista (monto de la transferencia).
- `label`: etiqueta opcional.

**Algoritmos implementados**:
- **BFS (Breadth-First Search)**: recorrido en anchura usando una `Queue` propia. Visita vertices nivel por nivel.
- **DFS (Depth-First Search)**: recorrido en profundidad usando recursion. Visita vertice y explora cada rama completamente antes de retroceder.
- **Deteccion de ciclos**: utiliza tres estados por vertice (NO_VISITADO, EN_PROCESO, COMPLETADO). Si durante DFS se encuentra un vertice EN_PROCESO, existe un ciclo.

### 8.3 Donde se usa en el proyecto?

- **Grafo de transferencias** (`DataStore.transferGraph`): modela las relaciones de transferencia entre usuarios. Cada vertice es un `userId` (cedula). Cada arista dirigida representa que el usuario origen ha transferido dinero al usuario destino, con el monto como peso.
- **Analisis de relaciones** (`AnalyticsService.getTransferRelationships()`): permite visualizar la red de transferencias.
- **Deteccion de ciclos** (`AnalyticsService.detectCyclesInTransfers()`): identifica patrones circulares de transferencias (A transfiere a B, B transfiere a C, C transfiere a A), lo cual puede indicar lavado de dinero o fraude.
- **Recorridos BFS/DFS**: permiten analizar la propagacion de flujos de dinero desde un usuario dado.

### 8.4 Por que esta estructura y no otra?

Las relaciones de transferencia forman naturalmente un grafo dirigido:
- La relacion "A transfiere a B" no implica que "B transfiere a A" (direccionalidad).
- El monto transferido es un atributo de la relacion, no de los nodos (pesos en aristas).
- Ninguna otra estructura modela relaciones muchos-a-muchos de forma natural.
- Las listas de adyacencia (vs. matriz de adyacencia) son mas eficientes en memoria para grafos dispersos, que es el caso tipico: cada usuario transfiere a pocos otros usuarios, no a todos.
- La deteccion de ciclos es una funcionalidad critica para la seguridad financiera, y es un algoritmo clasico de grafos (DFS con estados).
- El uso de `HashTable` para las listas de adyacencia permite agregar/buscar vertices en O(1), y el uso de `Queue` propia para BFS demuestra la integracion coherente de todas las estructuras.

### 8.5 Complejidad

| Operacion | Complejidad |
|---|---|
| `addVertex(T)` | O(1) amortizado |
| `removeVertex(T)` | O(V + E) |
| `addEdge(T, T, double)` | O(k) |
| `removeEdge(T, T)` | O(k) |
| `getNeighbors(T)` | O(k) |
| `hasVertex(T)` | O(1) amortizado |
| `hasEdge(T, T)` | O(k) |
| `getEdgeWeight(T, T)` | O(k) |
| `bfs(T)` | O(V + E) |
| `dfs(T)` | O(V + E) |
| `hasCycles()` | O(V + E) |
| `vertexCount()` | O(1) |
| `edgeCount()` | O(1) |

> **Nota**: V = numero de vertices, E = numero de aristas, k = grado del vertice (numero de aristas salientes).

---

## Resumen Comparativo

| Estructura | Uso principal en el proyecto | Operacion clave | Complejidad clave |
|---|---|---|---|
| SimpleLinkedList | Billeteras, notificaciones, cubetas hash, base de Stack/Queue | `addLast` / `removeFirst` | O(1) |
| DoublyLinkedList | Historial de transacciones (usuario y billetera) | `addLast` / `removeLast` / recorrido inverso | O(1) |
| Stack | Pila de deshacer (reversion de operaciones) | `push` / `pop` | O(1) |
| Queue | Cola de notificaciones pendientes | `enqueue` / `dequeue` | O(1) |
| PriorityQueue | Operaciones programadas por fecha | `enqueue` (ordenado) / `dequeue` | O(n) / O(1) |
| BinarySearchTree | Ranking de puntos de usuarios | `insert` / `delete` / `inOrder` | O(log n) promedio |
| HashTable | Usuarios por cedula, billeteras por codigo | `put` / `get` / `containsKey` | O(1) amortizado |
| Graph | Red de transferencias entre usuarios | `addEdge` / `bfs` / `hasCycles` | O(V + E) |
