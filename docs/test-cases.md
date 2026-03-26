# Casos de Prueba — Plataforma Fintech de Billeteras Digitales

## Casos de Prueba Funcionales

| # | Escenario | Entrada | Resultado esperado | Estado |
|---|---|---|---|---|
| 1 | Registrar un usuario nuevo exitosamente | `id="1001"`, `name="Carlos Lopez"`, `email="carlos@mail.com"`, `phone="3001234567"` | Se crea el usuario con nivel BRONZE, 0 puntos, se agrega vertice al grafo de transferencias y entrada al BST de ranking | Aprobado |
| 2 | Registrar un usuario con cedula duplicada | `id="1001"` (ya existente) | Se lanza `IllegalArgumentException` con mensaje "Ya existe un usuario registrado con la cedula: 1001" | Aprobado |
| 3 | Actualizar datos de un usuario existente | `id="1001"`, `name="Carlos A. Lopez"`, `email="carlosa@mail.com"`, `phone="3009876543"` | Se actualizan nombre, correo y telefono del usuario. El id y la fecha de registro no cambian | Aprobado |
| 4 | Eliminar un usuario sin saldo en billeteras | `id="1001"` (usuario sin saldo en ninguna billetera) | Se elimina el usuario de la tabla hash, se remueve vertice del grafo y entrada del BST. Las billeteras se eliminan del registro global | Aprobado |
| 5 | Eliminar un usuario con saldo positivo en una billetera | `id="1001"` (billetera con saldo $50,000) | Se lanza `IllegalArgumentException` indicando que la billetera tiene saldo y no se puede eliminar al usuario | Aprobado |
| 6 | Depositar dinero en una billetera activa | `walletCode="abc-123"`, `amount=500000.0` | Se incrementa el saldo de la billetera en $500,000. Se crea transaccion COMPLETED, se registra en historial de billetera y usuario, se apila en pila de deshacer, se generan 5000 puntos (deposito: 1 punto por cada $100) | Aprobado |
| 7 | Retirar dinero con saldo suficiente | `walletCode="abc-123"` (saldo $500,000), `amount=200000.0` | Se reduce el saldo a $300,000. Se crea transaccion WITHDRAWAL COMPLETED, se registra en historiales y se generan puntos | Aprobado |
| 8 | Retirar dinero con saldo insuficiente | `walletCode="abc-123"` (saldo $100,000), `amount=500000.0` | Se lanza `IllegalArgumentException` con mensaje "Saldo insuficiente en la billetera..." | Aprobado |
| 9 | Transferir dinero entre billeteras de distintos usuarios | `sourceWalletCode="abc-123"` (saldo $500,000, usuario nivel BRONZE 5% comision), `targetWalletCode="def-456"`, `amount=100000.0` | Se descuenta $100,000 del origen. Se acredita $95,000 al destino ($100,000 - 5% comision). Comision registrada: $5,000. Se generan 30 puntos (transferencia: 3 por cada $100). Se agrega arista al grafo de transferencias | Aprobado |
| 10 | Transferencia rechazada por limite diario excedido | Usuario nivel BRONZE (limite 5 transacciones/dia), ya realizo 5 transacciones hoy, intenta una sexta | Se crea transaccion con estado REJECTED. Se envia notificacion OPERATION_REJECTED al usuario. No se modifica ningun saldo | Aprobado |
| 11 | Revertir la ultima operacion (deposito) | `userId="1001"` (ultima operacion: deposito de $500,000) | Se desapila la transaccion de la pila de deshacer. Se resta $500,000 del saldo de la billetera. Se restan los puntos generados. Se actualiza el BST de ranking. Se marca la transaccion como REVERSED. Se envia notificacion SECURITY_ALERT | Aprobado |
| 12 | Revertir operacion sin transacciones en pila | `userId="1001"` (pila de deshacer vacia) | Se lanza `IllegalArgumentException` con mensaje "El usuario 1001 no tiene operaciones para deshacer" | Aprobado |
| 13 | Programar una operacion futura | `ScheduledOperation` con `scheduledDate=2026-04-01 10:00`, `type=TRANSFER`, `amount=50000.0` | Se encola la operacion en la PriorityQueue ordenada por fecha. La operacion queda como no ejecutada | Aprobado |
| 14 | Procesar operaciones programadas vencidas | `now=2026-04-02 00:00`. Hay 2 operaciones con fecha <= 2026-04-02 y 1 con fecha 2026-04-05 | Se ejecutan las 2 operaciones vencidas (se desencolan y ejecutan como transacciones normales). Se marcan como ejecutadas. La operacion del 2026-04-05 permanece en la cola. Retorna 2 (cantidad procesada) | Aprobado |
| 15 | Calcular puntos por transaccion de deposito | Transaccion tipo DEPOSIT, monto $150,000 | Se generan 1,500 puntos (1 punto por cada $100: 150000/100 = 1500) | Aprobado |
| 16 | Calcular puntos por transaccion de transferencia | Transaccion tipo TRANSFER, monto $200,000 | Se generan 6,000 puntos (3 puntos por cada $100: 200000/100 * 3 = 6000) | Aprobado |
| 17 | Subida de nivel automatica (BRONZE a SILVER) | Usuario con 450 puntos (BRONZE) realiza transaccion que genera 100 puntos | Puntos totales: 550. Nivel cambia de BRONZE a SILVER (rango 501-1000). Se actualiza BST (elimina entrada con 450, inserta con 550). Se envia notificacion LEVEL_UP | Aprobado |
| 18 | Canjear un beneficio con puntos suficientes | `userId="1001"` (800 puntos), `benefitId="ben-01"` (costo 500 puntos) | Puntos se reducen a 300. Se actualiza BST de ranking. Nivel puede cambiar si baja de rango. Beneficio se marca como canjeado con fecha actual. Se agrega a la lista de beneficios canjeados del usuario. Se envia notificacion BENEFIT_REDEEMED | Aprobado |
| 19 | Canjear un beneficio con puntos insuficientes | `userId="1001"` (200 puntos), `benefitId="ben-01"` (costo 500 puntos) | Se lanza `IllegalArgumentException` con mensaje "Puntos insuficientes. Se requieren 500 pero el usuario tiene 200" | Aprobado |
| 20 | Comision diferenciada por nivel (GOLD vs BRONZE) | Transferencia de $100,000 por usuario nivel GOLD (1% comision) | Comision cobrada: $1,000. Destino recibe $99,000. Comparado con BRONZE (5%): comision seria $5,000, destino recibiria $95,000 | Aprobado |
| 21 | Deteccion de fraude: alta frecuencia | Usuario realiza 6 transacciones en el ultimo minuto | Se genera evento de auditoria con nivel de riesgo HIGH y descripcion "Alta frecuencia de transacciones" | Aprobado |
| 22 | Deteccion de fraude: monto anomalo | Usuario con promedio de transacciones de $50,000 realiza transaccion de $200,000 (> 3x promedio) | Se genera evento de auditoria con nivel MEDIUM y descripcion "Monto anomalo detectado" | Aprobado |
| 23 | Deteccion de fraude: destino repetido | Usuario realiza 4 transferencias al mismo destino en 5 minutos | Se genera evento de auditoria con nivel HIGH y descripcion "Destino repetido sospechoso" | Aprobado |
| 24 | Deteccion de fraude: fragmentacion de transferencias | Usuario transfiere a 4 billeteras distintas en 10 minutos | Se genera evento de auditoria con nivel MEDIUM y descripcion "Fragmentacion de transferencias" | Aprobado |
| 25 | Analiticas: deteccion de ciclos en grafo de transferencias | Grafo con aristas: A->B, B->C, C->A | `detectCyclesInTransfers()` retorna `true`. Indica posible patron circular de transferencias | Aprobado |
| 26 | Analiticas: consultar frecuencia por tipo de transaccion | Sistema con 10 depositos, 5 retiros, 8 transferencias completadas | `getTransactionFrequencyByType()` retorna tabla hash con DEPOSIT=10, WITHDRAWAL=5, TRANSFER=8, SCHEDULED_PAYMENT=0 | Aprobado |
| 27 | Enviar notificacion a usuario existente | `userId="1001"`, `type=LOW_BALANCE`, `message="Saldo bajo en billetera principal"` | Se crea notificacion con estado no leida. Se agrega a la lista de notificaciones del usuario. Se encola en la cola de notificaciones pendientes del sistema | Aprobado |
| 28 | Reversion de transferencia con devolucion de comision | `userId="1001"`, ultima operacion: transferencia de $100,000 con comision de $5,000 | Se devuelve $100,000 al origen. Se resta $95,000 del destino (monto - comision). Se restan los puntos generados. Transaccion marcada como REVERSED | Aprobado |
| 29 | Crear billetera para usuario inexistente | `ownerId="9999"` (no existe), `name="Mi billetera"`, `type=SAVINGS` | Se lanza `IllegalArgumentException` con mensaje "No existe un usuario con la cedula: 9999" | Aprobado |
| 30 | Desactivar billetera con saldo cero | `code="abc-123"` (saldo $0) | La billetera se marca como inactiva (`active=false`). No se puede usar en futuras transacciones | Aprobado |

---

## Resumen de Cobertura

| Area funcional | Casos cubiertos | Numeros |
|---|---|---|
| CRUD de usuarios | 5 | 1, 2, 3, 4, 5 |
| Operaciones financieras (deposito/retiro/transferencia) | 5 | 6, 7, 8, 9, 10 |
| Reversion de operaciones | 3 | 11, 12, 28 |
| Operaciones programadas | 2 | 13, 14 |
| Puntos y niveles | 4 | 15, 16, 17, 20 |
| Canje de beneficios | 2 | 18, 19 |
| Deteccion de fraude | 4 | 21, 22, 23, 24 |
| Analiticas | 2 | 25, 26 |
| Notificaciones | 1 | 27 |
| Billeteras | 2 | 29, 30 |
