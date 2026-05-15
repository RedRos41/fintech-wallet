# Informe Final Tecnico — Plataforma Fintech de Billeteras Digitales

## Informacion Academica

- **Universidad**: Universidad del Quindio
- **Programa**: Ingenieria de Sistemas y Computacion
- **Asignatura**: Estructuras de Datos
- **Periodo**: 2026-1
- **Equipo**: Derek, Brahian, Sara

---

## Tabla de Contenido

1. [Resumen Ejecutivo](#1-resumen-ejecutivo)
2. [Problema y Objetivo](#2-problema-y-objetivo)
3. [Arquitectura del Sistema](#3-arquitectura-del-sistema)
4. [Estructuras de Datos Implementadas](#4-estructuras-de-datos-implementadas)
5. [Modulos Funcionales](#5-modulos-funcionales)
6. [Algoritmos Relevantes](#6-algoritmos-relevantes)
7. [Decisiones de Diseno](#7-decisiones-de-diseno)
8. [Validacion del Sistema](#8-validacion-del-sistema)
9. [Cumplimiento de Requisitos del PDF](#9-cumplimiento-de-requisitos-del-pdf)
10. [Ejecucion del Proyecto](#10-ejecucion-del-proyecto)
11. [Conclusiones y Trabajo Futuro](#11-conclusiones-y-trabajo-futuro)
12. [Referencias y Documentacion Complementaria](#12-referencias-y-documentacion-complementaria)

---

## 1. Resumen Ejecutivo

La Plataforma Fintech de Billeteras Digitales es una aplicacion web desarrollada con Spring Boot que simula el funcionamiento de una billetera digital moderna. El proyecto fue construido como entregable final del curso de Estructuras de Datos del periodo 2026-1, aplicando de manera practica las **8 estructuras de datos genericas** implementadas desde cero, sin recurrir a `java.util.Collection`.

El sistema cubre los **9 modulos funcionales** exigidos por el enunciado del proyecto: gestion de usuarios y billeteras, operaciones financieras, operaciones programadas, sistema de recompensas, niveles de usuario, reversion, alertas, analitica y deteccion de comportamiento financiero inusual. Adicionalmente integra una pagina de **comparacion de rendimiento** entre estructuras de datos para soportar el analisis de complejidad.

Cifras del proyecto:

| Indicador | Valor |
|---|---|
| Archivos Java fuente | 45 |
| Plantillas Thymeleaf | 17 |
| Servicios Spring | 10 |
| Controladores REST/Web | 9 |
| Modelos de dominio | 9 |
| Estructuras de datos genericas | 8 |
| Pruebas JUnit | 92 (todas en verde) |
| Reglas heuristicas de fraude | 7 |

---

## 2. Problema y Objetivo

### 2.1 Contexto

Los sistemas financieros digitales como Nequi, Daviplata o PayPal manejan grandes volumenes de transacciones, requieren respuestas en tiempo real, deben resolver consultas complejas (rankings, reportes, deteccion de patrones) y necesitan operaciones reversibles. Cada uno de estos requerimientos mapea directamente a una estructura de datos clasica.

### 2.2 Objetivo

Construir un sistema academico que **demuestre por que cada estructura de datos es la adecuada para resolver una necesidad concreta** del dominio fintech, justificando su eleccion en terminos de complejidad y patron de acceso.

### 2.3 Alcance

- Almacenamiento en memoria (sin persistencia en disco), enfocado en estructuras y no en infraestructura.
- Datos de prueba precargados al iniciar la aplicacion.
- Interfaz web con Thymeleaf y Bootstrap 5 para validar el comportamiento manualmente.
- Pruebas unitarias para cada estructura y para los servicios criticos.

---

## 3. Arquitectura del Sistema

### 3.1 Stack tecnologico

| Componente | Tecnologia |
|---|---|
| Lenguaje | Java 17 |
| Framework | Spring Boot 3.2.5 |
| Plantillas | Thymeleaf + layout-dialect |
| Frontend | Bootstrap 5.3, Bootstrap Icons |
| Build | Maven (con wrapper `mvnw`) |
| Pruebas | JUnit 5, Spring Boot Test |

### 3.2 Capas de la aplicacion

```
+----------------------------+
|  Vistas Thymeleaf (HTML)   |  <- Bootstrap 5
+----------------------------+
|  Controladores Spring MVC  |  <- 9 controllers
+----------------------------+
|  Servicios de dominio      |  <- 10 services @Service
+----------------------------+
|  DataStore (singleton)     |  <- centraliza todas las estructuras
+----------------------------+
|  Estructuras de datos      |  <- 8 implementaciones genericas
+----------------------------+
```

### 3.3 Patrones aplicados

- **Inyeccion de dependencias** en todos los servicios y controladores (constructor-based).
- **DataStore singleton** que actua como repositorio en memoria, evitando dependencias cruzadas entre servicios y simplificando el testing.
- **Modelo anemico de dominio** con servicios que orquestan la logica, manteniendo las clases del paquete `model` enfocadas en datos.
- **Strategy implicito** en los enums (`UserLevel`, `TransactionType`) que llevan ventajas y politicas asociadas.

---

## 4. Estructuras de Datos Implementadas

Cada una de las 8 estructuras esta documentada en detalle en [data-structures.md](data-structures.md). Aqui se resume el rol de cada una y su justificacion.

| # | Estructura | Uso principal | Justificacion |
|---|---|---|---|
| 1 | `SimpleLinkedList<T>` | Billeteras del usuario, notificaciones, beneficios canjeados, base de Stack/Queue/PriorityQueue, cubetas de HashTable | Insercion O(1) en extremos, recorrido secuencial, bajo costo de memoria |
| 2 | `DoublyLinkedList<T>` | Historial de transacciones por usuario y por billetera | Permite recorrido bidireccional necesario para mostrar transacciones de mas reciente a mas antigua y viceversa |
| 3 | `Stack<T>` | Pila de deshacer (`undoStack`) por usuario | LIFO natural para reversion: la ultima operacion realizada es la primera candidata a deshacer |
| 4 | `Queue<T>` | Cola de notificaciones pendientes de despacho | FIFO garantiza que las notificaciones se entreguen en el orden generado |
| 5 | `PriorityQueue<T>` | Operaciones programadas ordenadas por fecha y top transacciones por monto | Extraccion O(1) del elemento mas urgente; insercion ordenada O(n) |
| 6 | `BinarySearchTree<T>` | Ranking de usuarios por puntos (`UserPointsEntry`) | Recorrido in-order produce el ranking ordenado en O(n); busqueda en O(log n) promedio |
| 7 | `HashTable<K,V>` | Indice de usuarios por cedula y de billeteras por codigo | Acceso O(1) amortizado, esencial para validar identidad y resolver codigos en cada transaccion |
| 8 | `Graph<V>` | Red dirigida de transferencias entre usuarios | Permite analisis de rutas, deteccion de ciclos (BFS/DFS), patrones de interaccion |

Las complejidades especificas de cada operacion estan tabuladas en `data-structures.md`.

---

## 5. Modulos Funcionales

### 5.1 Gestion de usuarios y billeteras

- CRUD completo en `UserService` y `WalletService`.
- Cada usuario tiene cedula unica (clave hash), nombre, correo, lista de billeteras (`SimpleLinkedList<Wallet>`), historial (`DoublyLinkedList<Transaction>`), pila de deshacer (`Stack<Transaction>`), notificaciones (`SimpleLinkedList<Notification>`) y beneficios canjeados.
- Tipos de billetera soportados (enum `WalletType`): `SAVINGS`, `DAILY`, `SHOPPING`, `TRANSPORT`, `INVESTMENT`.
- Validaciones: cedula unica, formato de correo, saldo no negativo, billeteras activas, codigo unico por billetera.

### 5.2 Operaciones financieras

`TransactionService` orquesta los 4 tipos de operacion:

| Operacion | Estructuras involucradas |
|---|---|
| Recarga (deposit) | actualiza `Wallet.balance`, agrega a `DoublyLinkedList` historiales, apila en `Stack` undo, suma en `BinarySearchTree` |
| Retiro (withdrawal) | igual + valida saldo + emite notificacion `LOW_BALANCE` si aplica |
| Transferencia interna | dos billeteras del mismo usuario; sin arista en grafo |
| Transferencia externa | actualiza ambas billeteras, agrega arista al `Graph<String>`, dispara analisis de fraude |

Cada transaccion guarda los 9 campos exigidos por el PDF: `id`, `date`, `type`, `amount`, `sourceWalletCode`, `targetWalletCode`, `status`, `pointsGenerated`, `commissionCharged` (extra de implementacion).

### 5.3 Operaciones programadas

`ScheduledOperationService` mantiene una `PriorityQueue<ScheduledOperation>` ordenada por `scheduledDate`. El metodo `processAllDue()` recorre la cola extrayendo operaciones cuya fecha sea menor o igual al instante actual y las delega a `TransactionService`. Cuando la operacion se ejecuta correctamente, otorga un bono extra de puntos (politica del PDF).

### 5.4 Sistema de recompensas y niveles

Politica exacta del PDF (`RewardService.calculatePointsForTransaction`):

| Tipo | Puntos |
|---|---|
| Recarga | 1 punto por cada 100 unidades |
| Retiro | 2 puntos por cada 100 unidades |
| Transferencia | 3 puntos por cada 100 unidades |
| Programada exitosa | base + 5 puntos de bono |

Niveles (`UserLevel`):

| Nivel | Rango | Comision | Limite tx/dia |
|---|---|---|---|
| Bronce | 0–500 | 5 % | 5 |
| Plata | 501–1.000 | 3 % | 15 |
| Oro | 1.001–5.000 | 1 % | 30 |
| Platino | > 5.000 | 0 % | 100 |

El ranking se mantiene en un `BinarySearchTree<UserPointsEntry>` para producir reportes ordenados en O(n) via `inOrder()`.

### 5.5 Reversion

`ReversalService` extrae la ultima transaccion del `Stack<Transaction>` del usuario, recompone los saldos, ajusta los puntos descontandolos del ranking y reevalua el nivel del usuario. La transaccion original cambia su estado a `REVERSED` para no contarse en analiticas.

### 5.6 Alertas y notificaciones

`NotificationType` cubre los 5 casos del PDF y agrega `SECURITY_ALERT`:

- `LOW_BALANCE`, `SCHEDULED_UPCOMING`, `OPERATION_REJECTED`, `LEVEL_UP`, `BENEFIT_REDEEMED`, `SECURITY_ALERT`.

Las notificaciones se encolan en `Queue<Notification>` (despacho FIFO) y se conservan en la lista personal de cada usuario.

### 5.7 Analitica

`AnalyticsService` provee las 6 consultas del PDF mas las nuevas de la seccion 8:

| Consulta | Estructura clave |
|---|---|
| Top billeteras por uso | `HashTable` + ordenamiento por seleccion |
| Top usuarios por transferencias | `HashTable` + ordenamiento por seleccion |
| Categorias mas activas | `HashTable` agrupada por `WalletType` |
| Frecuencia por tipo de transaccion | `HashTable` agrupada por `TransactionType` |
| Monto total por rango de fechas | recorrido lineal sobre historiales |
| Relaciones del grafo + deteccion de ciclos | `Graph` con DFS |
| **Top transacciones por monto** | `PriorityQueue<TransactionAmountEntry>` |
| **Usuario mas activo en periodo** | recorrido lineal con conteo |

### 5.8 Deteccion de comportamiento financiero inusual

`FraudDetectionService` aplica **7 reglas heuristicas** que cubren los 6 patrones exigidos por el PDF:

| # | Regla | Patron PDF | Riesgo |
|---|---|---|---|
| 1 | `checkHighFrequency` — mas de 5 tx en 1 minuto | Multiples transferencias consecutivas | ALTO |
| 2 | `checkAmountAnomaly` — monto > 3x el promedio | Valores inusualmente altos | MEDIO |
| 3 | `checkRepeatedDestination` — > 3 tx al mismo destino en 5 min | Repetitivos al mismo destino | ALTO |
| 4 | `checkFragmentation` — > 3 destinos distintos en 10 min | Fragmentacion (ampliacion) | MEDIO |
| 5 | `checkSourceFragmentation` — 3+ billeteras propias como origen en 10 min | Fragmentacion con varias billeteras propias | ALTO |
| 6 | `checkFrequencyChange` — ultima hora supera 5x el ritmo historico | Cambios bruscos en frecuencia | MEDIO |
| 7 | `checkUnusualHour` — hora actual representa < 5 % del historial | Horarios no habituales | MEDIO |

Cada deteccion crea un `AuditEvent` que se inserta en `SimpleLinkedList<AuditEvent>` (registro de auditoria) con marca temporal, id de usuario, id de transaccion, nivel de riesgo y descripcion.

---

## 6. Algoritmos Relevantes

### 6.1 Top-K transacciones por monto

Se construye una `PriorityQueue<TransactionAmountEntry>` insertando todas las transacciones completadas. La clase `TransactionAmountEntry implements Comparable` ordena por `amount` descendente; en caso de empate, por id de transaccion (orden total). Despues se extraen las primeras K con `dequeue()`. Complejidad: O(n) inserciones a O(n) cada una (peor caso) + K extracciones O(1).

### 6.2 Deteccion de ciclos en el grafo

Implementada en `Graph<V>.hasCycles()` con DFS y dos tablas hash de marcado:
- `visited`: vertices ya procesados.
- `recursionStack`: vertices en la rama actual de la recursion.

Si durante el DFS se encuentra un vertice que ya esta en `recursionStack`, hay un ciclo. Complejidad O(V + E).

### 6.3 Insercion ordenada en PriorityQueue

`PriorityQueue<T>.enqueue(T)` recorre la lista interna hasta encontrar la posicion correcta. Optimiza dos casos comunes:
- Insercion al final (nuevo elemento >= ultimo): O(1).
- Insercion al inicio (nuevo elemento <= primero): O(1).
- Caso general: O(n).

### 6.4 Ranking por puntos con BST

`BinarySearchTree<UserPointsEntry>.inOrder()` produce la lista ordenada por puntos ascendentes en O(n). Para producir el ranking descendente (top usuarios) se invierte la lista en O(n). En cada actualizacion de puntos se elimina la entrada vieja y se inserta la nueva, manteniendo la invariante del BST.

### 6.5 Comparacion de rendimiento entre estructuras

`StructureBenchmarkService` ejecuta 3 escenarios sobre datos generados en memoria:

| Escenario | Estructura A | Estructura B | Que se mide |
|---|---|---|---|
| Busqueda por clave (1.000 lookups) | `HashTable` | `SimpleLinkedList` | Acceso O(1) vs O(n) |
| Obtencion en orden ascendente | `BinarySearchTree.inOrder()` | `SimpleLinkedList` con insertion sort | Recorrido in-order vs ordenamiento manual |
| Top elemento de mayor prioridad | `PriorityQueue.peek()` | `SimpleLinkedList` con busqueda lineal | Ventaja de tener el extremo precomputado |

Los resultados se exponen en la pagina `/benchmark` con tiempo en nanosegundos y factor de mejora (`speedup`).

---

## 7. Decisiones de Diseno

### 7.1 No uso de `java.util.Collection`

Decision deliberada: las 8 estructuras estan implementadas desde cero. Se permite `java.util.Map`/`java.util.List` solo en la capa de presentacion (controladores Spring que necesitan tipos compatibles con Thymeleaf). Cada estructura propia ofrece un metodo `toJavaList()` para puentear con la capa de vista.

### 7.2 Estructura unica para multiples roles (`SimpleLinkedList`)

`SimpleLinkedList` se reutiliza como almacenamiento interno de `Stack`, `Queue`, `PriorityQueue` y como cubetas de la `HashTable`. Esto reduce la superficie de codigo y concentra las pruebas unitarias en una sola clase fundamental.

### 7.3 Dos listas para historial de transacciones

Se mantienen dos copias del historial: una en el usuario (`DoublyLinkedList<Transaction>`) y otra en cada billetera (`DoublyLinkedList<Transaction>`). Esto permite consultas O(n) en cualquiera de los dos contextos sin filtrar el historial completo del sistema. La duplicacion es consciente y aceptable porque las transacciones son inmutables (no hay riesgo de inconsistencia entre copias).

### 7.4 Wrapper `UserPointsEntry` y `TransactionAmountEntry`

Las clases `UserPointsEntry` y `TransactionAmountEntry` son wrappers que implementan `Comparable` para poder usarse en estructuras genericas que exigen `T extends Comparable<T>`. Aislan el criterio de ordenamiento del modelo de dominio, manteniendo `User` y `Transaction` libres de dependencias estructurales.

### 7.5 Deteccion de fraude posterior, no bloqueante

El analisis de fraude se ejecuta despues de que la transaccion ya fue procesada. Esto reproduce el comportamiento de las plataformas reales, que prefieren no bloquear al usuario por una sospecha y registran el evento para revision posterior. El manejo de la transaccion sospechosa queda a discrecion del operador del sistema (revision manual, contacto con el usuario, etc.).

### 7.6 Heuristicas con umbrales fijos

Los umbrales de las reglas de fraude son fijos (5 tx/min, 3x el promedio, etc.). En un sistema productivo serian dinamicos por usuario o aprendidos. Para fines academicos los umbrales fijos son suficientes para demostrar el patron y son faciles de probar manualmente.

---

## 8. Validacion del Sistema

### 8.1 Pruebas automatizadas

Se ejecutan con `mvnw test`. Resultados actuales:

```
Tests run: 92, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Cobertura de pruebas:

| Modulo | Archivo de prueba | Tests |
|---|---|---|
| SimpleLinkedList | `SimpleLinkedListTest` | 12 |
| DoublyLinkedList | `DoublyLinkedListTest` | 10 |
| Stack | `StackTest` | 7 |
| Queue | `QueueTest` | 7 |
| PriorityQueue | `PriorityQueueTest` | 7 |
| BinarySearchTree | `BinarySearchTreeTest` | 13 |
| HashTable | `HashTableTest` | 10 |
| Graph | `GraphTest` | 14 |
| TransactionService | `TransactionServiceTest` | 5 |
| RewardService | `RewardServiceTest` | 4 |
| ReversalService | `ReversalServiceTest` | 3 |
| **Total** | | **92** |

### 8.2 Casos de prueba manuales

Documentados en [test-cases.md](test-cases.md): 30 casos funcionales que cubren el flujo completo del usuario.

### 8.3 Datos precargados

`DataInitializer` carga datos de prueba al arranque para facilitar la exploracion: usuarios de ejemplo, billeteras con saldos iniciales, beneficios del catalogo, transacciones historicas. Esto permite navegar la aplicacion sin necesidad de capturar datos manualmente antes de validar funcionalidades.

---

## 9. Cumplimiento de Requisitos del PDF

### 9.1 Seccion 4 — Funcionalidades

| Subseccion | Estado |
|---|---|
| 4.1 Gestion de usuarios y billeteras (5 tipos) | Cumplido |
| 4.2 Operaciones financieras + 8 campos por transaccion | Cumplido |
| 4.3 Operaciones programadas con prioridad por fecha | Cumplido |
| 4.4 Sistema de recompensas con politica exacta | Cumplido |
| 4.5 4 niveles de usuario con ventajas | Cumplido |
| 4.6 Reversion de operaciones | Cumplido |
| 4.7 Notificaciones (5 tipos PDF + extra de seguridad) | Cumplido |
| 4.8 Analitica (6 consultas) | Cumplido |
| 4.9 Deteccion de fraude (6 patrones PDF cubiertos por 7 reglas) | Cumplido |

### 9.2 Seccion 5 — Estructuras de datos

Las 8 estructuras se implementan desde cero y se usan en servicios concretos, no solo declarativamente. Ver tabla en la seccion 4 de este informe.

### 9.3 Seccion 6 — Requisitos funcionales

Los 12 requisitos numerados se mapean uno a uno a metodos de servicio:

| # | Requisito | Implementacion |
|---|---|---|
| 1 | CRUD usuarios | `UserService` |
| 2 | Multiples billeteras por usuario | `WalletService.createWallet` |
| 3 | Recargas, retiros, transferencias | `TransactionService` |
| 4 | Consultar historial | `Wallet.transactionHistory`, `User.transactionHistory` |
| 5 | Programar operaciones futuras | `ScheduledOperationService.scheduleOperation` |
| 6 | Procesar programadas | `ScheduledOperationService.processAllDue` |
| 7 | Acumular y descontar puntos | `RewardService.addPoints`, `ReversalService` |
| 8 | Asignar nivel | `UserLevel.fromPoints` invocado en cada update de puntos |
| 9 | Revertir operaciones | `ReversalService.revertLastTransaction` |
| 10 | Generar alertas | `NotificationService.sendNotification` |
| 11 | Reportes | `AnalyticsService` + pagina `/analytics` |
| 12 | Analizar relaciones | `Graph<String>` + `AnalyticsService.detectCyclesInTransfers` |

### 9.4 Seccion 8 — Requisitos adicionales

| Requisito adicional | Implementacion |
|---|---|
| Recalcular puntos si tx revertida | `ReversalService.revertLastTransaction` |
| Usuario con mayor actividad en periodo | `AnalyticsService.getMostActiveUserInPeriod(from, to)` |
| Ciclos / rutas frecuentes en grafo | `Graph.hasCycles`, `AnalyticsService.detectCyclesInTransfers` |
| Top transacciones por monto con estructura ordenada | `AnalyticsService.getTopTransactionsByAmount` con `PriorityQueue` |
| Simular ejecucion automatica de programadas | `ScheduledOperationService.processAllDue` |
| Comparar rendimiento de distintas estructuras | `StructureBenchmarkService` + pagina `/benchmark` |

### 9.5 Seccion 9 — Entregables

| Entregable | Ubicacion |
|---|---|
| Codigo fuente completo | `src/main/java/com/uniquindio/fintech/` |
| Diagrama de clases | [class-diagram.md](class-diagram.md) |
| Descripcion del problema | [problem-description.md](problem-description.md) |
| Estructuras de datos + justificacion | [data-structures.md](data-structures.md) |
| Casos de prueba | [test-cases.md](test-cases.md) + 92 pruebas JUnit |
| Informe final tecnico | Este documento |

---

## 10. Ejecucion del Proyecto

### 10.1 Prerrequisitos

- Java 17 o superior.
- Maven 3.8+ (o usar el wrapper `mvnw` incluido).

### 10.2 Pasos

```bash
# Compilar
.\mvnw.cmd clean compile

# Ejecutar pruebas
.\mvnw.cmd test

# Iniciar la aplicacion
.\mvnw.cmd spring-boot:run

# Abrir en el navegador
http://localhost:8080
```

### 10.3 Mapa de paginas

| Ruta | Modulo |
|---|---|
| `/` | Dashboard |
| `/users` | Gestion de usuarios |
| `/wallets` | Gestion de billeteras |
| `/transactions` | Historial y formularios de transaccion |
| `/scheduled` | Operaciones programadas |
| `/rewards` | Catalogo y canjes |
| `/notifications` | Bandeja de notificaciones |
| `/analytics` | Analitica completa, top transacciones, ranking, ciclos |
| `/benchmark` | Comparacion de rendimiento entre estructuras |

---

## 11. Conclusiones y Trabajo Futuro

### 11.1 Conclusiones

- La eleccion de cada estructura de datos esta justificada por el patron de acceso del modulo que la utiliza, no por imposicion del enunciado. Cada decision (HashTable para indices, BST para ranking, Graph para relaciones) responde a un requisito concreto.
- La separacion en capas (estructura > DataStore > servicio > controlador > vista) permite reemplazar cualquier capa sin afectar las demas. Por ejemplo, mover de almacenamiento en memoria a una base de datos solo requeriria cambiar `DataStore`.
- La cobertura de pruebas (92 tests) da confianza para refactorizar las estructuras o agregar nuevas reglas de negocio sin romper funcionalidad existente.
- La pagina de benchmark permite contrastar la teoria de complejidad asintotica con la realidad empirica del codigo escrito por el equipo, cerrando el ciclo de aprendizaje del curso.

### 11.2 Trabajo futuro

- **Persistencia**: integrar JPA / Hibernate sobre PostgreSQL manteniendo las estructuras propias en memoria como cache de primer nivel.
- **Autenticacion**: anadir Spring Security para login, roles y proteccion de endpoints.
- **Reglas de fraude dinamicas**: aprender umbrales por usuario en lugar de usar valores fijos.
- **Notificaciones en tiempo real**: WebSockets para empujar alertas al frontend sin refresco manual.
- **Programacion real con cron**: reemplazar `processAllDue` manual por un `@Scheduled` de Spring que itere la `PriorityQueue` cada minuto.
- **Auto-balanceo del BST**: migrar el `BinarySearchTree` a un AVL o Red-Black para mantener O(log n) garantizado en el peor caso.

---

## 12. Referencias y Documentacion Complementaria

- [README.md](../README.md) — Guia rapida de instalacion y descripcion del proyecto.
- [problem-description.md](problem-description.md) — Contexto, necesidades y alcance.
- [data-structures.md](data-structures.md) — Detalle exhaustivo de las 8 estructuras.
- [class-diagram.md](class-diagram.md) — Diagrama Mermaid de clases, modelos y servicios.
- [test-cases.md](test-cases.md) — 30 casos de prueba funcionales.
- Enunciado oficial: `Proyecto Final estructuras de datos 2026-1-noche.pdf`.
