package com.pv.challenge.service;

import com.pv.challenge.dto.ClienteDtos.SaveClienteRequest;
import com.pv.challenge.entity.Cliente;
import com.pv.challenge.entity.Persona;
import com.pv.challenge.exception.BusinessException;
import com.pv.challenge.repo.ClienteRepository;
import com.pv.challenge.repo.PersonaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ClienteServiceTest {

    @Mock
    private PersonaRepository personaRepo;

    @Mock
    private ClienteRepository clienteRepo;

    @InjectMocks
    private ClienteService clienteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crear_clienteValido_retornaCliente() {
        SaveClienteRequest request = new SaveClienteRequest();
        request.nombre = "Test User";
        request.genero = "Masculino";
        request.edad = 30;
        request.identificacion = "1234567890";
        request.direccion = "Test Address";
        request.telefono = "0987654321";
        request.clienteId = "test123";
        request.contrasena = "password";
        request.estado = true;

        when(personaRepo.findByIdentificacion(request.identificacion)).thenReturn(Optional.empty());
        when(clienteRepo.findByClienteId(request.clienteId)).thenReturn(Optional.empty());

        Persona persona = new Persona();
        persona.setId(1L);
        when(personaRepo.save(any(Persona.class))).thenReturn(persona);

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        when(clienteRepo.save(any(Cliente.class))).thenReturn(cliente);

        Cliente resultado = clienteService.crear(request);

        assertNotNull(resultado);
        verify(personaRepo).save(any(Persona.class));
        verify(clienteRepo).save(any(Cliente.class));
    }

    @Test
    void crear_identificacionDuplicada_lanzaExcepcion() {
        SaveClienteRequest request = new SaveClienteRequest();
        request.identificacion = "1234567890";

        when(personaRepo.findByIdentificacion(request.identificacion)).thenReturn(Optional.of(new Persona()));

        assertThrows(BusinessException.class, () -> clienteService.crear(request));
    }
}