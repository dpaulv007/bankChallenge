package com.pv.challenge.dto;

public class ClienteDtos {

    // Crear/Actualizar cliente junto con persona
    public static class SaveClienteRequest {
        // Persona
        public String nombre;
        public String genero;
        public Integer edad;
        public String identificacion;
        public String direccion;
        public String telefono;
        // Cliente
        public String clienteId;
        public String contrasena;
        public Boolean estado = Boolean.TRUE;
    }

    public static class ClienteResponse {
        public Long id;
        // Persona
        public String nombre;
        public String genero;
        public Integer edad;
        public String identificacion;
        public String direccion;
        public String telefono;
        // Cliente
        public String clienteId;
        public Boolean estado;
    }
}
