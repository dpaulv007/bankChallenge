package com.pv.challenge.dto;

import java.math.BigDecimal;

public class CuentaDtos {

    public static class SaveCuentaRequest {
        public String numero;
        public String tipo;
        public BigDecimal saldoInicial = BigDecimal.ZERO;
        public Boolean estado = Boolean.TRUE;
        public Long clienteId;
    }

    public static class CuentaResponse {
        public Long id;
        public String numero;
        public String tipo;
        public BigDecimal saldo;
        public Boolean estado;
        public Long clienteId;
        public String clienteNombre;
    }
}
