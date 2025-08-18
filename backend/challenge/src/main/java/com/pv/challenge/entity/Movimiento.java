package com.pv.challenge.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "movimientos")
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_id")
    private Cuenta cuenta;

    @Column(nullable = false, length = 20)
    private String tipo; 

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal saldo; 

    @Column(length = 150)
    private String referencia;

    @Column(nullable = false)
    private OffsetDateTime fecha = OffsetDateTime.now();

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Cuenta getCuenta() { return cuenta; }
    public void setCuenta(Cuenta cuenta) { this.cuenta = cuenta; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
    public OffsetDateTime getFecha() { return fecha; }
    public void setFecha(OffsetDateTime fecha) { this.fecha = fecha; }
}
