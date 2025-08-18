package com.pv.challenge.entity;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "personas")
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, length = 10)
    private String genero; 

    @Column(nullable = false)
    private Integer edad;

    @Column(nullable = false, unique = true, length = 50)
    private String identificacion;

    @Column(nullable = false, length = 200)
    private String direccion;

    @Column(nullable = false, length = 32)
    private String telefono;

    @Column(name = "creado_en", nullable = false)
    private OffsetDateTime creadoEn = OffsetDateTime.now();

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }
    public String getIdentificacion() { return identificacion; }
    public void setIdentificacion(String identificacion) { this.identificacion = identificacion; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public OffsetDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(OffsetDateTime creadoEn) { this.creadoEn = creadoEn; }
}
