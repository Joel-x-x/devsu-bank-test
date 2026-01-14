# Migraciones de Base de Datos - Flyway

Este directorio contiene las migraciones de base de datos para el sistema bancario.

## Orden de Ejecución

Las migraciones se ejecutan en el siguiente orden:

1. **V1__create_person_table.sql** - Crea la tabla `person` (tabla base para herencia)
2. **V2__create_customer_table.sql** - Crea la tabla `customer` (hereda campos de person)
3. **V3__create_account_table.sql** - Crea la tabla `account` 
4. **V4__create_movement_table.sql** - Crea la tabla `movement`

## Estructura de Tablas

### Person (Tabla Base)
- Campos de auditoría: id, created_at, updated_at, deleted_at, created_by, updated_by, deleted_by, is_deleted
- Campos de persona: name, genre, birth_date, identification, address, phone

### Customer (Hereda de Person)
- Todos los campos de Person
- Campos específicos: password, status, customer_code

### Account
- Campos de auditoría (hereda de Auditable)
- Campos específicos: account_number, account_type, initial_balance, daily_limit

### Movement
- Campos de auditoría (hereda de Auditable)
- Campos específicos: movement_date, movement_type, amount, balance, available_balance

## Tipos de Datos

- **CHAR(36)**: UUID para IDs
- **DATETIME(6)**: Timestamps con precisión de microsegundos
- **DECIMAL(19,4)**: Valores monetarios con 4 decimales
- **BIT(1)**: Valores booleanos
- **VARCHAR**: Strings de longitud variable

## Relaciones entre Tablas

- **Customer → Person**: Customer hereda todos los campos de Person (herencia de tabla única)
- **Account → Customer**: Relación ManyToOne (un cliente puede tener múltiples cuentas)
  - FK: `customer_id` REFERENCES `customer(id)`
- **Movement → Account**: Relación ManyToOne (una cuenta puede tener múltiples movimientos)
  - FK: `account_id` REFERENCES `account(id)`

## Notas

- Todas las tablas usan InnoDB como motor de almacenamiento
- El charset es utf8mb4 con collation utf8mb4_unicode_ci
- Se crean índices en columnas frecuentemente consultadas
- Los campos nullable están explícitamente definidos

