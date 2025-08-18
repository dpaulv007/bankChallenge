package com.pv.challenge.entity;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    private Long id; 

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id")
    private Persona persona;
    
    @Column(name = "clienteid", nullable = false, unique = true, length = 50)
    private String clienteId;

    @Column(nullable = false, length = 120)
    private String contrasena;

    @Column(nullable = false)
    private Boolean estado = Boolean.TRUE;

    @Column(name = "creado_en", nullable = false)
    private OffsetDateTime creadoEn = OffsetDateTime.now();

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Persona getPersona() { return persona; }
    public void setPersona(Persona persona) { this.persona = persona; }
    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }
    public OffsetDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(OffsetDateTime creadoEn) { this.creadoEn = creadoEn; }
}
