package com.pv.challenge.web;

import com.pv.challenge.dto.CuentaDtos.SaveCuentaRequest;
import com.pv.challenge.dto.CuentaDtos.CuentaResponse;
import com.pv.challenge.entity.Cliente;
import com.pv.challenge.entity.Cuenta;
import com.pv.challenge.entity.Persona;
import com.pv.challenge.exception.BusinessException;
import com.pv.challenge.exception.NotFoundException;
import com.pv.challenge.service.CuentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CuentaControllerTest {

    @Mock
    private CuentaService cuentaService;

    @InjectMocks
    private CuentaController cuentaController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Cuenta crearCuentaConCliente(Long id, String numero, String tipo, BigDecimal saldo, String nombreCliente) {
        Persona persona = new Persona();
        persona.setNombre(nombreCliente);
        
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setPersona(persona);
        
        Cuenta cuenta = new Cuenta();
        cuenta.setId(id);
        cuenta.setNumero(numero);
        cuenta.setTipo(tipo);
        cuenta.setSaldo(saldo);
        cuenta.setEstado(true);
        cuenta.setCliente(cliente);
        
        return cuenta;
    }

    @Test
    void listar_retornaListaCuentas() {
        Cuenta cuenta1 = crearCuentaConCliente(1L, "001", "Ahorro", new BigDecimal("1000"), "Juan Pérez");
        Cuenta cuenta2 = crearCuentaConCliente(2L, "002", "Corriente", new BigDecimal("500"), "María García");
        
        when(cuentaService.listar()).thenReturn(Arrays.asList(cuenta1, cuenta2));

        List<CuentaResponse> resultado = cuentaController.listar();

        assertEquals(2, resultado.size());
        assertEquals("001", resultado.get(0).numero);
        assertEquals("Juan Pérez", resultado.get(0).clienteNombre);
        verify(cuentaService).listar();
    }

    @Test
    void listarPorCliente_retornaCuentasDelCliente() {
        Long clienteId = 1L;
        Cuenta cuenta = crearCuentaConCliente(1L, "001", "Ahorro", new BigDecimal("1000"), "Juan Pérez");
        
        when(cuentaService.listarPorCliente(clienteId)).thenReturn(Arrays.asList(cuenta));

        List<CuentaResponse> resultado = cuentaController.listarPorCliente(clienteId);

        assertEquals(1, resultado.size());
        assertEquals("001", resultado.get(0).numero);
        verify(cuentaService).listarPorCliente(clienteId);
    }

    @Test
    void obtener_cuentaExiste_retornaCuenta() {
        Long cuentaId = 1L;
        Cuenta cuenta = crearCuentaConCliente(cuentaId, "001", "Ahorro", new BigDecimal("1000"), "Juan Pérez");
        
        when(cuentaService.obtener(cuentaId)).thenReturn(cuenta);

        CuentaResponse resultado = cuentaController.obtener(cuentaId);

        assertEquals(cuentaId, resultado.id);
        assertEquals("001", resultado.numero);
        assertEquals("Juan Pérez", resultado.clienteNombre);
        verify(cuentaService).obtener(cuentaId);
    }

    @Test
    void obtener_cuentaNoExiste_lanzaNotFoundException() {
        Long cuentaId = 999L;
        
        when(cuentaService.obtener(cuentaId)).thenThrow(new NotFoundException("Cuenta no encontrada"));

        assertThrows(NotFoundException.class, () -> cuentaController.obtener(cuentaId));
        verify(cuentaService).obtener(cuentaId);
    }

    @Test
    void crear_datosValidos_retornaCuentaCreada() {
        SaveCuentaRequest request = new SaveCuentaRequest();
        request.numero = "003";
        request.tipo = "Ahorro";
        request.clienteId = 1L;
        
        Cuenta cuentaCreada = crearCuentaConCliente(1L, "003", "Ahorro", BigDecimal.ZERO, "Juan Pérez");
        
        when(cuentaService.crear(request)).thenReturn(cuentaCreada);

        CuentaResponse resultado = cuentaController.crear(request);

        assertEquals("003", resultado.numero);
        assertEquals("Ahorro", resultado.tipo);
        verify(cuentaService).crear(request);
    }

    @Test
    void crear_numeroExistente_lanzaBusinessException() {
        SaveCuentaRequest request = new SaveCuentaRequest();
        request.numero = "001";
        
        when(cuentaService.crear(request)).thenThrow(new BusinessException("Número de cuenta ya existe"));

        assertThrows(BusinessException.class, () -> cuentaController.crear(request));
        verify(cuentaService).crear(request);
    }

    @Test
    void actualizar_datosValidos_retornaCuentaActualizada() {
        Long cuentaId = 1L;
        SaveCuentaRequest request = new SaveCuentaRequest();
        request.numero = "001-UPD";
        request.tipo = "Corriente";
        
        Cuenta cuentaActualizada = crearCuentaConCliente(cuentaId, "001-UPD", "Corriente", new BigDecimal("1000"), "Juan Pérez");
        
        when(cuentaService.actualizar(cuentaId, request)).thenReturn(cuentaActualizada);

        CuentaResponse resultado = cuentaController.actualizar(cuentaId, request);

        assertEquals("001-UPD", resultado.numero);
        assertEquals("Corriente", resultado.tipo);
        verify(cuentaService).actualizar(cuentaId, request);
    }

    @Test
    void eliminar_cuentaExiste_eliminaCorrectamente() {
        Long cuentaId = 1L;
        
        doNothing().when(cuentaService).eliminar(cuentaId);

        assertDoesNotThrow(() -> cuentaController.eliminar(cuentaId));
        verify(cuentaService).eliminar(cuentaId);
    }

    @Test
    void eliminar_cuentaNoExiste_lanzaNotFoundException() {
        Long cuentaId = 999L;
        
        doThrow(new NotFoundException("Cuenta no encontrada")).when(cuentaService).eliminar(cuentaId);

        assertThrows(NotFoundException.class, () -> cuentaController.eliminar(cuentaId));
        verify(cuentaService).eliminar(cuentaId);
    }
}