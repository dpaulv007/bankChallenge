package com.pv.challenge.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReporteLineaDtos {
  private LocalDateTime fecha;
  private String cliente;
  private String numeroCuenta;
  private String tipoCuenta;
  private BigDecimal saldoInicial;
  private boolean estado;
  private BigDecimal movimiento;
  private BigDecimal saldoDisponible;

  public LocalDateTime getFecha() { return fecha; }
  public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

  public String getCliente() { return cliente; }
  public void setCliente(String cliente) { this.cliente = cliente; }

  public String getNumeroCuenta() { return numeroCuenta; }
  public void setNumeroCuenta(String numeroCuenta) { this.numeroCuenta = numeroCuenta; }

  public String getTipoCuenta() { return tipoCuenta; }
  public void setTipoCuenta(String tipoCuenta) { this.tipoCuenta = tipoCuenta; }

  public BigDecimal getSaldoInicial() { return saldoInicial; }
  public void setSaldoInicial(BigDecimal saldoInicial) { this.saldoInicial = saldoInicial; }

  public boolean isEstado() { return estado; }
  public void setEstado(boolean estado) { this.estado = estado; }

  public BigDecimal getMovimiento() { return movimiento; }
  public void setMovimiento(BigDecimal movimiento) { this.movimiento = movimiento; }

  public BigDecimal getSaldoDisponible() { return saldoDisponible; }
  public void setSaldoDisponible(BigDecimal saldoDisponible) { this.saldoDisponible = saldoDisponible; }
}
