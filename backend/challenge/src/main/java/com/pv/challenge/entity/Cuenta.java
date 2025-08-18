package com.pv.challenge.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "cuentas")
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String numero;

    @Column(nullable = false, length = 15)
    private String tipo;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal saldo = BigDecimal.ZERO;

    @Column(nullable = false)
    private Boolean estado = Boolean.TRUE;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(name = "creado_en", nullable = false)
    private OffsetDateTime creadoEn = OffsetDateTime.now();

    public Cuenta() {}
    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }
    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public OffsetDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(OffsetDateTime creadoEn) { this.creadoEn = creadoEn; }
}
