# Makefile - Bank Challenge
# Requiere: Docker + Docker Compose. Recomendado usar Git Bash / WSL en Windows.
# Variables
COMPOSE ?= docker compose
API ?= api
DB ?= db
BASE_URL ?= http://localhost:8080
BACKEND_DIR ?= backend/challenge
MVN_IMAGE ?= maven:3.9-eclipse-temurin-8

# Por defecto, mostrar ayuda
.DEFAULT_GOAL := help

# En Windows fuerza bash como shell cuando se usa Git Bash
ifeq ($(OS),Windows_NT)
  SHELL := bash
endif

## help: Muestra esta ayuda
help:
	@echo "Comandos disponibles:"
	@grep -E '^[a-zA-Z0-9_-]+:.*?## ' $(MAKEFILE_LIST) | \
		awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-18s\033[0m %s\n", $$1, $$2}'

## up: Levanta toda la solución (db, api, etc.) con build
up:
	$(COMPOSE) up -d --build

## down: Detiene y elimina contenedores y volúmenes
down:
	$(COMPOSE) down -v

## ps: Lista los servicios
ps:
	$(COMPOSE) ps

## logs: Muestra logs del servicio API
logs: logs-api

## logs-api: Logs de la API
logs-api:
	$(COMPOSE) logs -f $(API)

## logs-db: Logs de la base de datos
logs-db:
	$(COMPOSE) logs -f $(DB)

## restart-api: Reinicia solo la API
restart-api:
	$(COMPOSE) restart $(API)

## psql: Abre psql dentro del contenedor de DB (usa variables de entorno del contenedor)
psql:
	$(COMPOSE) exec $(DB) sh -lc 'psql -U $$POSTGRES_USER -d $$POSTGRES_DB'

## db-shell: Shell del contenedor de DB
db-shell:
	$(COMPOSE) exec $(DB) sh

## api-shell: Shell del contenedor de API
api-shell:
	$(COMPOSE) exec $(API) sh

## test: Ejecuta pruebas unitarias del backend
test:
	docker run --rm -v "$(CURDIR)/$(BACKEND_DIR):/app" -w /app $(MVN_IMAGE) mvn -q test

## test-frontend: Ejecuta pruebas unitarias del frontend
test-frontend:
	@echo "🧪 Ejecutando pruebas unitarias del frontend..."
	cd frontend-angular_new && npm test

## test-all: Ejecuta todas las pruebas unitarias (backend + frontend)
test-all:
	@echo "🧪 Ejecutando todas las pruebas unitarias..."
	@echo "📋 Backend tests:"
	docker run --rm -v "$(CURDIR)/$(BACKEND_DIR):/app" -w /app $(MVN_IMAGE) mvn -q test
	@echo "📋 Frontend tests:"
	cd frontend-angular_new && npm test

## package: Empaqueta el backend (salta tests)
package:
	docker run --rm -v "$(CURDIR)/$(BACKEND_DIR):/app" -w /app $(MVN_IMAGE) mvn -q -DskipTests -Dmaven.test.skip=true package

## seed: Carga los datos de ejemplo del enunciado (requiere API arriba en $(BASE_URL))
seed:
	# 1) Clientes
	curl -s -X POST "$(BASE_URL)/api/clientes" -H "Content-Type: application/json" -d '{"nombre":"Jose Lema","genero":"Masculino","edad":30,"identificacion":"1010101010","direccion":"Otavalo sn y principal","telefono":"098254785","clienteId":"jose123","contrasena":"1234","estado":true}' >/dev/null
	curl -s -X POST "$(BASE_URL)/api/clientes" -H "Content-Type: application/json" -d '{"nombre":"Marianela Montalvo","genero":"Femenino","edad":28,"identificacion":"2020202020","direccion":"Amazonas y NNUU","telefono":"097548965","clienteId":"maria456","contrasena":"5678","estado":true}' >/dev/null
	curl -s -X POST "$(BASE_URL)/api/clientes" -H "Content-Type: application/json" -d '{"nombre":"Juan Osorio","genero":"Masculino","edad":26,"identificacion":"3030303030","direccion":"13 junio y Equinoccial","telefono":"098874587","clienteId":"juan789","contrasena":"1245","estado":true}' >/dev/null
	# 2) Cuentas (asumiendo DB limpia: Jose=3, Marianela=4, Juan=5)
	curl -s -X POST "$(BASE_URL)/api/cuentas" -H "Content-Type: application/json" -d '{"numero":"478758","tipo":"Ahorro","saldoInicial":2000,"estado":true,"clienteId":3}' >/dev/null
	curl -s -X POST "$(BASE_URL)/api/cuentas" -H "Content-Type: application/json" -d '{"numero":"225487","tipo":"Corriente","saldoInicial":100,"estado":true,"clienteId":4}' >/dev/null
	curl -s -X POST "$(BASE_URL)/api/cuentas" -H "Content-Type: application/json" -d '{"numero":"495878","tipo":"Ahorro","saldoInicial":0,"estado":true,"clienteId":5}' >/dev/null
	curl -s -X POST "$(BASE_URL)/api/cuentas" -H "Content-Type: application/json" -d '{"numero":"496825","tipo":"Ahorro","saldoInicial":540,"estado":true,"clienteId":4}' >/dev/null
	curl -s -X POST "$(BASE_URL)/api/cuentas" -H "Content-Type: application/json" -d '{"numero":"585545","tipo":"Corriente","saldoInicial":1000,"estado":true,"clienteId":3}' >/dev/null
	# 3) Movimientos (usando IDs de cuentas creadas)
	curl -s -X POST "$(BASE_URL)/api/movimientos/retiro?cuentaId=3&monto=575&ref=retiro%20inicial" >/dev/null
	curl -s -X POST "$(BASE_URL)/api/movimientos/deposito?cuentaId=4&monto=600&ref=deposito%20prueba" >/dev/null
	curl -s -X POST "$(BASE_URL)/api/movimientos/deposito?cuentaId=5&monto=150&ref=deposito%20prueba" >/dev/null
	curl -s -X POST "$(BASE_URL)/api/movimientos/retiro?cuentaId=6&monto=540&ref=retiro%20total" >/dev/null
	@echo "Seed completado contra $(BASE_URL)"

## smoke: Pruebas rápidas de endpoints clave
smoke:
	curl -s -X GET "$(BASE_URL)/api/clientes" | head -c 300 ; echo
	curl -s -X GET "$(BASE_URL)/api/cuentas"  | head -c 300 ; echo
	curl -s -X GET "$(BASE_URL)/api/movimientos" | head -c 300 ; echo
	curl -s -X GET "$(BASE_URL)/api/reportes/json?clienteId=1&desde=2025-01-01&hasta=2025-12-31" | head -c 300 ; echo

## report-json: Genera reporte JSON (guarda ./report.json)
report-json:
	curl -s -X GET "$(BASE_URL)/api/reportes/json?clienteId=1&desde=2025-01-01&hasta=2025-12-31" -o report.json
	@echo "Reporte JSON guardado en report.json"

## report-pdf: Genera PDF directamente
report-pdf:
	curl -s -X GET "$(BASE_URL)/api/reportes/pdf?clienteId=1&desde=2025-01-01&hasta=2025-12-31" -o report.pdf
	@echo "Reporte PDF guardado en report.pdf"

## report-pdf-ps: Genera PDF con PowerShell
report-pdf-ps:
	powershell -NoProfile -Command "Invoke-RestMethod -Method GET '$(BASE_URL)/api/reportes/pdf?clienteId=1&desde=2025-01-01&hasta=2025-12-31' -OutFile 'report.pdf'"
	@echo "Reporte PDF guardado en report.pdf"

## clean: Limpia imágenes dangling y target del backend
clean:
	-$(COMPOSE) down -v
	-docker image prune -f
	-rm -rf "$(BACKEND_DIR)/target"
