package com.pv.challenge.dto;

import com.pv.challenge.entity.Movimiento;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class MovimientoDtos {

    public static class MovimientoResponse {
        private Long id;
        private Long cuentaId;
        private String tipo;
        private BigDecimal valor;
        private BigDecimal saldo;
        private OffsetDateTime fecha;
        private String referencia;

        public MovimientoResponse(Movimiento m) {
            this.id = m.getId();
            this.cuentaId = m.getCuenta().getId();
            this.tipo = m.getTipo();
            this.valor = m.getValor();
            this.saldo = m.getSaldo();
            this.fecha = m.getFecha();
            this.referencia = m.getReferencia();
        }

        // Getters
        public Long getId() { return id; }
        public Long getCuentaId() { return cuentaId; }
        public String getTipo() { return tipo; }
        public BigDecimal getValor() { return valor; }
        public BigDecimal getSaldo() { return saldo; }
        public OffsetDateTime getFecha() { return fecha; }
        public String getReferencia() { return referencia; }
    }
}