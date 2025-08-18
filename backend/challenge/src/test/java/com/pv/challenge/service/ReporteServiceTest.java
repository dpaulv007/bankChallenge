package com.pv.challenge.service;

import com.pv.challenge.dto.ReporteLineaDtos;
import com.pv.challenge.dto.ReporteResponseDtos;
import com.pv.challenge.entity.Cliente;
import com.pv.challenge.entity.Cuenta;
import com.pv.challenge.entity.Movimiento;
import com.pv.challenge.entity.Persona;
import com.pv.challenge.exception.NotFoundException;
import com.pv.challenge.repo.ClienteRepository;
import com.pv.challenge.repo.CuentaRepository;
import com.pv.challenge.repo.MovimientoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para ReporteService (sin contexto Spring).
 * Importante: usamos Arrays.asList(...) en vez de List.of(...) por compatibilidad con Java 8.
 */
@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

  private ClienteRepository clienteRepo;
  private CuentaRepository cuentaRepo;
  private MovimientoRepository movRepo;
  private ReporteService service;

  @BeforeEach
  void setup() {
    clienteRepo = mock(ClienteRepository.class);
    cuentaRepo = mock(CuentaRepository.class);
    movRepo = mock(MovimientoRepository.class);
    service = new ReporteService(clienteRepo, cuentaRepo, movRepo);
  }
  private Persona persona(String nombre) {
    Persona p = new Persona();
    p.setNombre(nombre);
    return p;
  }

  private Cliente cliente(Long id, String nombre) {
    Cliente c = new Cliente();
    c.setId(id);
    c.setPersona(persona(nombre));
    return c;
  }

  private Cuenta cuenta(Long id, String numero, String tipo, boolean estado) {
    Cuenta ct = new Cuenta();
    ct.setId(id);
    ct.setNumero(numero);
    ct.setTipo(tipo);
    ct.setEstado(estado);
    return ct;
  }

  private Movimiento mov(Cuenta c, String tipo, String valor, String fechaIsoUtc) {
    Movimiento m = new Movimiento();
    m.setCuenta(c);
    m.setTipo(tipo);
    m.setValor(new BigDecimal(valor));
    m.setReferencia("ref");
    m.setSaldo(BigDecimal.ZERO); // no lo usa para cálculos de reporte
    m.setFecha(OffsetDateTime.parse(fechaIsoUtc).withOffsetSameInstant(ZoneOffset.UTC));
    return m;
  }

  // ---------- TESTS ----------

  @Test
  void generar_ok_con_saldo_inicial_de_movimientos_previos_y_totales() {
    // Cliente y cuenta
    Long clienteId = 1L;
    Cliente cli = cliente(clienteId, "Paul");
    Cuenta cta = cuenta(7L, "123", "Ahorro", true);

    when(clienteRepo.findById(clienteId)).thenReturn(Optional.of(cli));
    when(cuentaRepo.findByCliente_Id(clienteId)).thenReturn(Arrays.asList(cta));

    // Movimientos ANTERIORES al rango (definen saldo inicial): +100.00
    Movimiento prev1 = mov(cta, "DEPOSITO", "100.00", "2025-07-31T23:00:00Z");
    when(movRepo.findByCuenta_IdAndFechaBeforeOrderByFechaAsc(eq(7L), any(OffsetDateTime.class)))
        .thenReturn(Arrays.asList(prev1));

    // Movimientos EN RANGO: +200.00 y -50.00 => totales créditos=200, débitos=50
    Movimiento m1 = mov(cta, "DEPOSITO", "200.00", "2025-08-01T10:00:00Z");
    Movimiento m2 = mov(cta, "RETIRO", "50.00", "2025-08-02T12:00:00Z");
    when(movRepo.findByCuenta_IdAndFechaBetweenOrderByFechaAsc(eq(7L), any(OffsetDateTime.class), any(OffsetDateTime.class)))
        .thenReturn(Arrays.asList(m1, m2));

    // Ejecutar
    LocalDate desde = LocalDate.of(2025, 8, 1);
    LocalDate hasta = LocalDate.of(2025, 8, 31);
    ReporteResponseDtos r = service.generar(clienteId, desde, hasta, false);

    // Asserts generales
    assertNotNull(r);
    assertEquals(clienteId, r.getClienteId());
    assertEquals("Paul", r.getClienteNombre());
    assertEquals(desde, r.getDesde());
    assertEquals(hasta, r.getHasta());

    // Totales
    assertEquals(0, r.getTotalCreditos().compareTo(new BigDecimal("200.00")));
    assertEquals(0, r.getTotalDebitos().compareTo(new BigDecimal("50.00")));

    // Items y orden
    assertEquals(2, r.getItems().size());
    ReporteLineaDtos it0 = r.getItems().get(0);
    ReporteLineaDtos it1 = r.getItems().get(1);

    // Saldo inicial debe provenir de movimientos previos: 100.00
    assertEquals(0, it0.getSaldoInicial().compareTo(new BigDecimal("100.00")));
    assertEquals("123", it0.getNumeroCuenta());
    assertEquals("Ahorro", it0.getTipoCuenta());
    assertTrue(it0.isEstado());

    // Movimiento 1: +200 -> saldo disponible 300
    assertEquals(0, it0.getMovimiento().compareTo(new BigDecimal("200.00")));
    assertEquals(0, it0.getSaldoDisponible().compareTo(new BigDecimal("300.00")));

    // Movimiento 2: -50 -> saldo pasa a 250
    assertEquals(0, it1.getMovimiento().compareTo(new BigDecimal("-50.00")));
    assertEquals(0, it1.getSaldoInicial().compareTo(new BigDecimal("300.00")));
    assertEquals(0, it1.getSaldoDisponible().compareTo(new BigDecimal("250.00")));
  }

  @Test
  void generar_cliente_no_existe_lanza_notfound() {
    when(clienteRepo.findById(999L)).thenReturn(Optional.empty());
    assertThrows(NotFoundException.class,
        () -> service.generar(999L, LocalDate.now(), LocalDate.now(), false));
    verify(clienteRepo).findById(999L);
    verifyNoMoreInteractions(cuentaRepo, movRepo);
  }

  @Test
  void generar_sin_cuentas_devuelve_items_vacios_y_totales_cero() {
    Long clienteId = 2L;
    when(clienteRepo.findById(clienteId)).thenReturn(Optional.of(cliente(clienteId, "Ana")));
    when(cuentaRepo.findByCliente_Id(clienteId)).thenReturn(Collections.<Cuenta>emptyList());

    ReporteResponseDtos r = service.generar(clienteId, LocalDate.of(2025,8,1), LocalDate.of(2025,8,31), false);

    assertNotNull(r);
    assertTrue(r.getItems().isEmpty());
    assertEquals(0, r.getTotalCreditos().compareTo(BigDecimal.ZERO));
    assertEquals(0, r.getTotalDebitos().compareTo(BigDecimal.ZERO));
  }

  @Test
  void generar_con_pdf_retorna_base64_no_nulo() {
    Long clienteId = 3L;
    Cliente cli = cliente(clienteId, "Maria");
    Cuenta cta = cuenta(8L, "456", "Corriente", true);

    when(clienteRepo.findById(clienteId)).thenReturn(Optional.of(cli));
    when(cuentaRepo.findByCliente_Id(clienteId)).thenReturn(Arrays.asList(cta));

    // Sin movimientos previos
    when(movRepo.findByCuenta_IdAndFechaBeforeOrderByFechaAsc(eq(8L), any(OffsetDateTime.class)))
        .thenReturn(Collections.<Movimiento>emptyList());

    // Un movimiento en rango
    Movimiento m = mov(cta, "DEPOSITO", "10.00", "2025-08-05T09:00:00Z");
    when(movRepo.findByCuenta_IdAndFechaBetweenOrderByFechaAsc(eq(8L), any(OffsetDateTime.class), any(OffsetDateTime.class)))
        .thenReturn(Arrays.asList(m));

    ReporteResponseDtos r = service.generar(clienteId, LocalDate.of(2025,8,1), LocalDate.of(2025,8,31), true);

    assertNotNull(r.getPdfBase64(), "Debe generar un PDF en Base64");
    assertFalse(r.getPdfBase64().isEmpty());
  }

  @Test
  void generar_saldo_inicial_con_previos_mixtos() {
    Long clienteId = 4L;
    Cliente cli = cliente(clienteId, "Luis");
    Cuenta cta = cuenta(9L, "789", "Ahorro", true);

    when(clienteRepo.findById(clienteId)).thenReturn(Optional.of(cli));
    when(cuentaRepo.findByCliente_Id(clienteId)).thenReturn(Arrays.asList(cta));

    // Previos: +100, -30 => neto = 70
    Movimiento p1 = mov(cta, "DEPOSITO", "100.00", "2025-07-30T12:00:00Z");
    Movimiento p2 = mov(cta, "RETIRO",   "30.00",  "2025-07-31T12:00:00Z");
    when(movRepo.findByCuenta_IdAndFechaBeforeOrderByFechaAsc(eq(9L), any(OffsetDateTime.class)))
        .thenReturn(Arrays.asList(p1, p2));

    // En rango: +5
    Movimiento mr = mov(cta, "DEPOSITO", "5.00", "2025-08-02T10:00:00Z");
    when(movRepo.findByCuenta_IdAndFechaBetweenOrderByFechaAsc(eq(9L), any(OffsetDateTime.class), any(OffsetDateTime.class)))
        .thenReturn(Arrays.asList(mr));

    ReporteResponseDtos r = service.generar(clienteId, LocalDate.of(2025,8,1), LocalDate.of(2025,8,31), false);

    assertEquals(1, r.getItems().size());
    ReporteLineaDtos it0 = r.getItems().get(0);
    assertEquals(0, it0.getSaldoInicial().compareTo(new BigDecimal("70.00")));
    assertEquals(0, it0.getMovimiento().compareTo(new BigDecimal("5.00")));
    assertEquals(0, it0.getSaldoDisponible().compareTo(new BigDecimal("75.00")));
  }
}
