-- Seguridad: todo en una transacción para crear "atómico".
BEGIN;

-- --------
-- Limpieza
-- --------
DROP VIEW  IF EXISTS vw_estado_cuenta;
DROP TABLE IF EXISTS movimientos;
DROP TABLE IF EXISTS cuentas;
DROP TABLE IF EXISTS clientes;
DROP TABLE IF EXISTS personas;

-- -------------
-- Tabla PERSONAS
-- -------------
CREATE TABLE personas (
  id               BIGSERIAL PRIMARY KEY,
  nombre           VARCHAR(150) NOT NULL,
  genero           VARCHAR(10)  NOT NULL,              -- 'Masculino' | 'Femenino' | 'Otro'
  edad             INT          NOT NULL CHECK (edad >= 0),
  identificacion   VARCHAR(50)  NOT NULL UNIQUE,
  direccion        VARCHAR(200) NOT NULL,
  telefono         VARCHAR(32)  NOT NULL,
  creado_en        TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_personas_identificacion ON personas(identificacion);

-- ------------
-- Tabla CLIENTES
-- ------------
-- PK compartido = FK a personas(id). Así garantizamos 1–1 real.
CREATE TABLE clientes (
  id           BIGINT PRIMARY KEY
               REFERENCES personas(id)
               ON DELETE CASCADE,
  clienteid    VARCHAR(50)  NOT NULL UNIQUE,  -- código/usuario del cliente
  contrasena   VARCHAR(120) NOT NULL,         -- hash/clave (no texto plano)
  estado       BOOLEAN      NOT NULL DEFAULT TRUE,
  creado_en    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_clientes_estado ON clientes(estado);

-- -----------
-- Tabla CUENTAS
-- -----------
CREATE TABLE cuentas (
  id            BIGSERIAL PRIMARY KEY,
  numero        VARCHAR(30)  NOT NULL UNIQUE,
  tipo          VARCHAR(15)  NOT NULL CHECK (tipo IN ('Ahorro','Corriente')),
  saldo         NUMERIC(19,2) NOT NULL DEFAULT 0 CHECK (saldo >= 0),
  estado        BOOLEAN       NOT NULL DEFAULT TRUE,
  cliente_id    BIGINT        NOT NULL
                REFERENCES clientes(id)
                ON DELETE CASCADE,            -- borrar cliente → borra cuentas
  creado_en     TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_cuentas_cliente ON cuentas(cliente_id);
CREATE INDEX idx_cuentas_estado  ON cuentas(estado);

-- ---------------
-- Tabla MOVIMIENTOS
-- ---------------
-- Guardamos el saldo resultante para cada movimiento (auditoría / reporte).
CREATE TABLE movimientos (
  id           BIGSERIAL PRIMARY KEY,
  cuenta_id    BIGINT       NOT NULL
               REFERENCES cuentas(id)
               ON DELETE CASCADE,             -- borrar cuenta → borra movs
  tipo         VARCHAR(20)  NOT NULL CHECK (tipo IN ('DEPOSITO','RETIRO','DEPOSITO_INICIAL')),
  valor        NUMERIC(19,2) NOT NULL CHECK (valor > 0),
  saldo        NUMERIC(19,2) NOT NULL CHECK (saldo >= 0),
  referencia   VARCHAR(150),
  fecha        TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_movs_cuenta   ON movimientos(cuenta_id);
CREATE INDEX idx_movs_fecha    ON movimientos(fecha);
CREATE INDEX idx_movs_tipo     ON movimientos(tipo);

-- ---------------
-- DATOS DE PRUEBA
-- ---------------
-- Insertar personas
INSERT INTO personas (nombre, genero, edad, identificacion, direccion, telefono) VALUES
('Juan Pérez', 'Masculino', 30, '1234567890', 'Av. Principal 123', '0987654321'),
('María García', 'Femenino', 25, '0987654321', 'Calle Secundaria 456', '0912345678');

-- Insertar clientes
INSERT INTO clientes (id, clienteid, contrasena, estado) VALUES
(1, 'juan123', '$2a$10$example', true),
(2, 'maria456', '$2a$10$example', true);

-- Insertar cuentas
INSERT INTO cuentas (numero, tipo, saldo, cliente_id) VALUES
('001-001-001', 'Ahorro', 1000.00, 1),
('001-001-002', 'Corriente', 500.00, 2);

COMMIT;