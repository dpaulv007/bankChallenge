package com.pv.challenge.web;

import com.pv.challenge.dto.ClienteDtos.SaveClienteRequest;
import com.pv.challenge.dto.ClienteDtos.ClienteResponse;
import com.pv.challenge.entity.Cliente;
import com.pv.challenge.entity.Persona;
import com.pv.challenge.exception.BusinessException;
import com.pv.challenge.exception.NotFoundException;
import com.pv.challenge.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ClienteControllerTest {

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private ClienteController clienteController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Cliente crearClienteConPersona(Long id, String nombre, String clienteId) {
        Persona persona = new Persona();
        persona.setNombre(nombre);
        persona.setGenero("Masculino");
        persona.setEdad(30);
        persona.setIdentificacion("1234567890");
        persona.setDireccion("Test Address");
        persona.setTelefono("0987654321");
        
        Cliente cliente = new Cliente();
        cliente.setId(id);
        cliente.setPersona(persona);
        cliente.setClienteId(clienteId);
        cliente.setEstado(true);
        
        return cliente;
    }

    @Test
    void listar_retornaListaClientes() {
        Cliente cliente1 = crearClienteConPersona(1L, "Juan Pérez", "juan123");
        Cliente cliente2 = crearClienteConPersona(2L, "María García", "maria456");
        
        when(clienteService.listar()).thenReturn(Arrays.asList(cliente1, cliente2));

        List<ClienteResponse> resultado = clienteController.listar();

        assertEquals(2, resultado.size());
        assertEquals("Juan Pérez", resultado.get(0).nombre);
        assertEquals("juan123", resultado.get(0).clienteId);
        verify(clienteService).listar();
    }

    @Test
    void obtener_clienteExiste_retornaCliente() {
        Long clienteId = 1L;
        Cliente cliente = crearClienteConPersona(clienteId, "Juan Pérez", "juan123");
        
        when(clienteService.obtener(clienteId)).thenReturn(cliente);

        ClienteResponse resultado = clienteController.obtener(clienteId);

        assertEquals(clienteId, resultado.id);
        assertEquals("Juan Pérez", resultado.nombre);
        assertEquals("juan123", resultado.clienteId);
        verify(clienteService).obtener(clienteId);
    }

    @Test
    void obtener_clienteNoExiste_lanzaNotFoundException() {
        Long clienteId = 999L;
        
        when(clienteService.obtener(clienteId)).thenThrow(new NotFoundException("Cliente no encontrado"));

        assertThrows(NotFoundException.class, () -> clienteController.obtener(clienteId));
        verify(clienteService).obtener(clienteId);
    }

    @Test
    void crear_datosValidos_retornaClienteCreado() {
        SaveClienteRequest request = new SaveClienteRequest();
        request.nombre = "Test User";
        request.clienteId = "test123";
        
        Cliente clienteCreado = crearClienteConPersona(1L, "Test User", "test123");
        
        when(clienteService.crear(request)).thenReturn(clienteCreado);

        ClienteResponse resultado = clienteController.crear(request);

        assertEquals(1L, resultado.id);
        assertEquals("Test User", resultado.nombre);
        assertEquals("test123", resultado.clienteId);
        verify(clienteService).crear(request);
    }

    @Test
    void crear_datosInvalidos_lanzaBusinessException() {
        SaveClienteRequest request = new SaveClienteRequest();
        request.identificacion = "duplicada";
        
        when(clienteService.crear(request)).thenThrow(new BusinessException("Identificación ya existe"));

        assertThrows(BusinessException.class, () -> clienteController.crear(request));
        verify(clienteService).crear(request);
    }

    @Test
    void actualizar_datosValidos_retornaClienteActualizado() {
        Long clienteId = 1L;
        SaveClienteRequest request = new SaveClienteRequest();
        request.nombre = "Updated User";
        
        Cliente clienteActualizado = crearClienteConPersona(clienteId, "Updated User", "test123");
        
        when(clienteService.actualizar(clienteId, request)).thenReturn(clienteActualizado);

        ClienteResponse resultado = clienteController.actualizar(clienteId, request);

        assertEquals(clienteId, resultado.id);
        assertEquals("Updated User", resultado.nombre);
        verify(clienteService).actualizar(clienteId, request);
    }

    @Test
    void eliminar_clienteExiste_eliminaCorrectamente() {
        Long clienteId = 1L;
        
        doNothing().when(clienteService).eliminar(clienteId);

        assertDoesNotThrow(() -> clienteController.eliminar(clienteId));
        verify(clienteService).eliminar(clienteId);
    }

    @Test
    void eliminar_clienteNoExiste_lanzaNotFoundException() {
        Long clienteId = 999L;
        
        doThrow(new NotFoundException("Cliente no encontrado")).when(clienteService).eliminar(clienteId);

        assertThrows(NotFoundException.class, () -> clienteController.eliminar(clienteId));
        verify(clienteService).eliminar(clienteId);
    }
}