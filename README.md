# 🏦 Bank Challenge - Sistema Bancario

Sistema bancario completo desarrollado con **Spring Boot**, **Angular** y **PostgreSQL**, containerizado con **Docker**.

## 🚀 Inicio Rápido

### Prerrequisitos
- [Docker](https://www.docker.com/get-started) y Docker Compose
- [Git](https://git-scm.com/)

### Levantar el proyecto
```bash
# Clonar repositorio
git clone <repository-url>
cd bank-challenge

# Opción 1: Con Makefile (recomendado)
make up
make seed    # Carga datos de prueba

# Opción 2: Con Docker Compose
docker-compose up -d --build
```

### Acceder a la aplicación
- **Frontend**: http://localhost:4200
- **Backend API**: http://localhost:8080
- **Base de datos**: localhost:5432

## 🏗️ Arquitectura

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │    Backend      │    │   Database      │
│   Angular 16    │───▶│  Spring Boot    │───▶│  PostgreSQL     │
│   Port: 4200    │    │   Port: 8080    │    │   Port: 5432    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Tecnologías
- **Frontend**: Angular 16, TypeScript, CSS
- **Backend**: Spring Boot 2.7, Java 8, JPA/Hibernate
- **Base de datos**: PostgreSQL 16
- **Containerización**: Docker, Docker Compose
- **Proxy**: Nginx (en producción)

## 📁 Estructura del Proyecto

```
bank-challenge/
├── backend/challenge/          # API Spring Boot
│   ├── src/main/java/         # Código fuente Java
│   ├── src/main/resources/    # Configuraciones
│   └── pom.xml               # Dependencias Maven
├── frontend-angular_new/      # Aplicación Angular
│   ├── src/app/              # Componentes Angular
│   ├── src/styles.css        # Estilos globales
│   └── package.json          # Dependencias npm
├── db/init/                  # Scripts de inicialización DB
├── postman/                  # Colecciones Postman
├── docker-compose.yml        # Orquestación de servicios
├── Makefile                  # Automatización de tareas
└── README.md                 # Este archivo
```

## 🔧 Comandos Disponibles

### Con Makefile
```bash
make up          # Levantar todos los servicios
make down        # Detener y limpiar todo
make seed        # Cargar datos de prueba
make logs        # Ver logs del backend
make smoke       # Probar endpoints principales
make test        # Ejecutar pruebas unitarias del backend
make test-frontend # Ejecutar pruebas unitarias del frontend
make test-all    # Ejecutar todas las pruebas unitarias
make clean       # Limpiar imágenes y archivos temporales
```

### Con Docker Compose
```bash
docker-compose up -d --build    # Levantar servicios
docker-compose down -v          # Detener y limpiar
docker-compose logs api         # Ver logs del backend
docker-compose ps               # Estado de servicios
```

## 🌐 Endpoints de la API

### Clientes
- `GET /api/clientes` - Listar clientes
- `POST /api/clientes` - Crear cliente
- `GET /api/clientes/{id}` - Obtener cliente
- `PUT /api/clientes/{id}` - Actualizar cliente
- `DELETE /api/clientes/{id}` - Eliminar cliente

### Cuentas
- `GET /api/cuentas` - Listar cuentas
- `POST /api/cuentas` - Crear cuenta
- `GET /api/cuentas/{id}` - Obtener cuenta
- `GET /api/cuentas/cliente/{id}` - Cuentas por cliente
- `PUT /api/cuentas/{id}` - Actualizar cuenta
- `DELETE /api/cuentas/{id}` - Eliminar cuenta

### Movimientos
- `GET /api/movimientos` - Listar movimientos
- `POST /api/movimientos/deposito` - Realizar depósito
- `POST /api/movimientos/retiro` - Realizar retiro
- `POST /api/movimientos/transferencia` - Realizar transferencia

### Reportes
- `GET /api/reportes/json` - Reporte en JSON
- `GET /api/reportes/pdf` - Reporte en PDF

## 📊 Datos de Prueba

El comando `make seed` carga:
- **3 clientes** (Jose Lema, Marianela Montalvo, Juan Osorio)
- **5 cuentas** distribuidas entre los clientes
- **4 movimientos** de ejemplo

### Ejemplo de cliente:
```json
{
  "nombre": "Jose Lema",
  "genero": "Masculino",
  "edad": 30,
  "identificacion": "1010101010",
  "direccion": "Otavalo sn y principal",
  "telefono": "098254785",
  "clienteId": "jose123",
  "contrasena": "1234",
  "estado": true
}
```

## 🧪 Pruebas con Postman

1. Importar colección: `postman/postman_collection_bank_challenge_fixed.json`
2. Importar environment: `postman/postman_environment_bank_challenge_fixed.json`
3. Seleccionar environment "Bank Challenge Local - Fixed"
4. Ejecutar requests en orden: Clientes → Cuentas → Movimientos → Reportes

## 🐛 Solución de Problemas

### El frontend no carga
```bash
# Verificar que todos los servicios estén corriendo
docker-compose ps

# Revisar logs
make logs
```

### Error de conexión a la base de datos
```bash
# Reiniciar servicios
make down
make up
```

### Puertos ocupados
```bash
# Verificar puertos en uso
netstat -an | findstr "4200\|8080\|5432"

# Cambiar puertos en docker-compose.yml si es necesario
```

### Limpiar todo y empezar de nuevo
```bash
make clean
make up
make seed
```

## 📝 Variables de Entorno

Configuradas en `.env`:
```env
POSTGRES_USER=devuser
POSTGRES_PASSWORD=devpass
POSTGRES_DB=banking
POSTGRES_PORT=5432
```

## 🔒 Seguridad

- Contraseñas hasheadas en base de datos
- Validaciones de entrada en backend
- Manejo de errores centralizado
- Headers de seguridad en nginx

## 🧪 Testing

### Backend (Java/Spring Boot)
- **52+ tests unitarios** cubriendo servicios y controladores
- Tests con JUnit 5 y Mockito
- Cobertura completa de lógica de negocio
- Validación de DTOs y manejo de errores

### Frontend (Angular/TypeScript)
- **51 tests unitarios** cubriendo componentes y servicios
- Tests con Jest y Angular Testing Utilities
- Mocking de servicios HTTP
- Validación de interacciones de usuario

### Comandos de Testing
```bash
make test          # Tests del backend
make test-frontend # Tests del frontend
make test-all      # Todos los tests
```

## 📈 Características Implementadas

- ✅ CRUD completo de clientes, cuentas y movimientos
- ✅ Transacciones bancarias (depósito, retiro, transferencia)
- ✅ Reportes en JSON y PDF
- ✅ Interfaz web responsive
- ✅ API REST documentada
- ✅ Base de datos relacional
- ✅ Containerización completa
- ✅ Automatización con Makefile
- ✅ **Testing completo (103+ tests unitarios)**

## 🚀 Despliegue en Producción

Para producción, considerar:
- Variables de entorno seguras
- HTTPS con certificados SSL
- Backup automático de base de datos
- Monitoreo y logs centralizados
- Escalamiento horizontal