# Descripcion del Problema — Plataforma Fintech de Billeteras Digitales

## Informacion Academica

- **Universidad**: Universidad del Quindio
- **Programa**: Ingenieria de Sistemas y Computacion
- **Asignatura**: Estructuras de Datos
- **Periodo**: 2026-1
- **Equipo**: Derek, Brahian, Sara

---

## 1. Contexto

En la actualidad, los sistemas financieros digitales han transformado la forma en que las personas gestionan su dinero. Las billeteras digitales se han convertido en una herramienta fundamental para realizar transacciones rapidas, seguras y sin necesidad de intermediarios bancarios tradicionales. Plataformas como Nequi, Daviplata y PayPal han demostrado que la gestion financiera digital es una necesidad real y creciente.

En el ambito academico, el desarrollo de un sistema de billeteras digitales representa una oportunidad excepcional para aplicar de manera practica los conceptos teoricos de las estructuras de datos. Este tipo de sistema requiere almacenamiento eficiente, busquedas rapidas, procesamiento ordenado y modelado de relaciones complejas, lo cual mapea directamente a las estructuras de datos fundamentales estudiadas en el curso.

---

## 2. Necesidades Identificadas

El sistema busca resolver las siguientes necesidades:

### 2.1 Gestion de Usuarios
- Registro, consulta, actualizacion y eliminacion de usuarios del sistema.
- Cada usuario se identifica por su cedula y posee datos personales, billeteras, historial de transacciones, puntos y nivel.

### 2.2 Gestion de Billeteras Virtuales
- Creacion de multiples billeteras por usuario, categorizadas por tipo (ahorro, diaria, compras, transporte, inversion).
- Consulta de saldo, historial de transacciones y estado de activacion.
- Desactivacion de billeteras sin saldo.

### 2.3 Operaciones Financieras
- **Depositos**: agregar fondos a una billetera.
- **Retiros**: retirar fondos de una billetera con validacion de saldo suficiente.
- **Transferencias**: mover fondos entre billeteras de distintos usuarios, aplicando comisiones segun el nivel del remitente.
- **Limite diario**: restriccion del numero maximo de transacciones diarias segun el nivel del usuario.

### 2.4 Reversion de Operaciones
- Capacidad de deshacer la ultima operacion realizada por un usuario.
- Devolucion de saldos, reembolso de comisiones y ajuste de puntos.
- Registro de la transaccion como revertida en el historial.

### 2.5 Operaciones Programadas
- Programacion de depositos, retiros y transferencias para una fecha futura.
- Ejecucion automatica cuando se alcanza la fecha programada.
- Procesamiento en orden cronologico (la operacion mas proxima primero).

### 2.6 Sistema de Recompensas y Niveles
- Acumulacion de puntos por cada transaccion realizada, con tasas diferenciadas por tipo de operacion.
- Cuatro niveles de usuario basados en puntos acumulados: Bronce (0-500), Plata (501-1000), Oro (1001-5000) y Platino (5001+).
- Cada nivel determina la tasa de comision y el limite diario de transacciones.
- Catalogo de beneficios canjeables con puntos acumulados.

### 2.7 Deteccion de Fraude
- Analisis automatico de patrones sospechosos en las transacciones:
  - Alta frecuencia de transacciones (mas de 5 en un minuto).
  - Montos anomalos (superiores a 3 veces el promedio del usuario).
  - Transacciones repetidas al mismo destino (mas de 3 en 5 minutos).
  - Fragmentacion de transferencias (mas de 3 destinos distintos en 10 minutos).
- Registro de eventos de auditoria con niveles de riesgo (Bajo, Medio, Alto, Critico).

### 2.8 Notificaciones
- Sistema de notificaciones para informar al usuario sobre eventos relevantes: saldo bajo, operaciones rechazadas, subida de nivel, beneficios canjeados y alertas de seguridad.
- Procesamiento en orden FIFO (primero en llegar, primero en atenderse).

### 2.9 Analiticas y Estadisticas
- Billeteras mas usadas por cantidad de transacciones.
- Usuarios mas activos por numero de transferencias.
- Categorias de billetera mas frecuentes.
- Montos totales en rangos de fechas.
- Frecuencia de transacciones por tipo.
- Visualizacion de la red de transferencias entre usuarios.
- Deteccion de ciclos en las relaciones de transferencia.

---

## 3. Alcance del Sistema

### 3.1 Funcionalidades Incluidas

| Modulo | Funcionalidades |
|---|---|
| Usuarios | CRUD completo, niveles automaticos, puntos |
| Billeteras | Creacion, consulta, desactivacion, multiples tipos |
| Transacciones | Deposito, retiro, transferencia, comisiones, limites diarios |
| Reversion | Deshacer ultima operacion, devolucion de saldos y puntos |
| Operaciones programadas | Programacion, ejecucion automatica, consulta de pendientes |
| Recompensas | Calculo de puntos, niveles, canje de beneficios |
| Fraude | 4 reglas heuristicas, registro de auditoria |
| Notificaciones | 6 tipos de notificacion, cola de procesamiento |
| Analiticas | 7 consultas estadisticas, deteccion de ciclos |

### 3.2 Restricciones Tecnicas

- **Almacenamiento en memoria**: todos los datos se almacenan en estructuras de datos propias (no se utiliza base de datos). Al reiniciar la aplicacion, se cargan datos de prueba predefinidos.
- **Estructuras de datos propias**: no se utilizan colecciones de `java.util` (ArrayList, HashMap, LinkedList, etc.) para el almacenamiento principal. Todas las estructuras fueron implementadas desde cero.
- **Sin persistencia**: el sistema no persiste datos entre ejecuciones. Esto es una decision de diseno para enfocar el proyecto en las estructuras de datos.

### 3.3 Tecnologias Utilizadas

| Componente | Tecnologia |
|---|---|
| Lenguaje | Java 17 |
| Framework backend | Spring Boot 3.2.5 |
| Motor de plantillas | Thymeleaf |
| Frontend | Bootstrap 5, HTML5, CSS3 |
| Build tool | Maven |
| Testing | JUnit 5 (Spring Boot Test) |

### 3.4 Arquitectura

El sistema sigue una arquitectura de capas:

1. **Capa de presentacion**: controladores Spring MVC que manejan las peticiones HTTP y renderizan vistas Thymeleaf.
2. **Capa de servicios**: logica de negocio encapsulada en 9 servicios Spring (`@Service`) con inyeccion de dependencias.
3. **Capa de datos**: `DataStore` centraliza todas las estructuras de datos del sistema como un singleton Spring.
4. **Capa de estructuras de datos**: 8 estructuras de datos genericas implementadas desde cero, sin dependencias de `java.util.Collection`.

---

## 4. Justificacion Academica

Este proyecto integra los siguientes conceptos del curso de Estructuras de Datos:

| Concepto | Aplicacion en el proyecto |
|---|---|
| Listas enlazadas simples | Billeteras, notificaciones, cubetas hash, base de Stack/Queue/PriorityQueue |
| Listas doblemente enlazadas | Historial de transacciones bidireccional |
| Pilas (LIFO) | Funcionalidad de deshacer (undo) operaciones |
| Colas (FIFO) | Procesamiento de notificaciones en orden |
| Colas de prioridad | Operaciones programadas ordenadas por fecha |
| Arboles binarios de busqueda | Ranking dinamico de puntos de usuarios |
| Tablas hash | Busqueda eficiente de usuarios y billeteras por clave |
| Grafos dirigidos ponderados | Red de transferencias, deteccion de ciclos, BFS/DFS |
| Generics en Java | Todas las estructuras son parametrizadas con tipos genericos |
| Complejidad algoritmica | Analisis O() documentado para cada operacion |
| Patron Iterator | Todas las estructuras lineales implementan `Iterable<T>` |

El proyecto demuestra que las estructuras de datos no son conceptos aislados, sino herramientas fundamentales para resolver problemas reales de ingenieria de software.
