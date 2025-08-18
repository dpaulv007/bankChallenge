package com.pv.challenge.service;

import com.pv.challenge.dto.CuentaDtos.SaveCuentaRequest;
import com.pv.challenge.entity.Cliente;
import com.pv.challenge.entity.Cuenta;
import com.pv.challenge.entity.Movimiento;
import com.pv.challenge.exception.BusinessException;
import com.pv.challenge.exception.NotFoundException;
import com.pv.challenge.repo.ClienteRepository;
import com.pv.challenge.repo.CuentaRepository;
import com.pv.challenge.repo.MovimientoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CuentaServiceTest {

  private CuentaRepository cuentaRepo;
  private ClienteRepository clienteRepo;
  private MovimientoRepository movRepo;

  private CuentaService service;

  @BeforeEach
  void setup() {
    cuentaRepo = mock(CuentaRepository.class);
    clienteRepo = mock(ClienteRepository.class);
    movRepo = mock(MovimientoRepository.class);
    service = new CuentaService(cuentaRepo, clienteRepo, movRepo);
  }

  private SaveCuentaRequest reqBase() {
    SaveCuentaRequest r = new SaveCuentaRequest();
    r.numero = "001-ABC";
    r.tipo = "Ahorro";
    r.estado = true;
    r.clienteId = 10L;
    r.saldoInicial = new BigDecimal("100.00");
    return r;
  }

  private Cliente cliente(long id) {
    Cliente c = new Cliente();
    c.setId(id);
    c.setEstado(true);
    return c;
  }

  private Cuenta cuentaExistente(long id, String numero, String tipo, boolean estado) {
    Cuenta ct = new Cuenta();
    ct.setId(id);
    ct.setNumero(numero);
    ct.setTipo(tipo);
    ct.setEstado(estado);
    ct.setSaldo(BigDecimal.ZERO);
    return ct;
  }

  // ---------- CREAR ----------

  @Test
  void crear_ok_con_deposito_inicial_crea_movimiento_y_actualiza_saldo() {
    SaveCuentaRequest req = reqBase(); // saldoInicial = 100.00
    when(cuentaRepo.findByNumero(req.numero)).thenReturn(Optional.empty());
    when(clienteRepo.findById(req.clienteId)).thenReturn(Optional.of(cliente(10L)));

    // que save(cta) devuelva el mismo objeto
    when(cuentaRepo.save(any(Cuenta.class))).thenAnswer(inv -> inv.getArgument(0));
    when(movRepo.save(any(Movimiento.class))).thenAnswer(inv -> inv.getArgument(0));

    Cuenta creada = service.crear(req);

    assertNotNull(creada);
    assertEquals(req.numero, creada.getNumero());
    assertEquals(req.tipo, creada.getTipo());
    assertEquals(true, creada.getEstado());
    // saldo debe quedar igual al depósito inicial
    assertEquals(0, creada.getSaldo().compareTo(new BigDecimal("100.00")));

    // Se registró movimiento de depósito inicial
    ArgumentCaptor<Movimiento> capMov = ArgumentCaptor.forClass(Movimiento.class);
    verify(movRepo).save(capMov.capture());
    Movimiento m = capMov.getValue();
    assertEquals("DEPOSITO_INICIAL", m.getTipo());
    assertEquals(0, m.getValor().compareTo(new BigDecimal("100.00")));
    assertEquals(0, m.getSaldo().compareTo(new BigDecimal("100.00")));
    assertEquals("apertura", m.getReferencia());
    assertNotNull(m.getFecha());
    assertSame(creada, m.getCuenta());

    verify(cuentaRepo, times(2)).save(any(Cuenta.class)); // una al crear, otra al setear saldo inicial
  }

  @Test
  void crear_ok_sin_deposito_inicial_no_crea_movimiento() {
    SaveCuentaRequest req = reqBase();
    req.saldoInicial = null; 

    when(cuentaRepo.findByNumero(req.numero)).thenReturn(Optional.empty());
    when(clienteRepo.findById(req.clienteId)).thenReturn(Optional.of(cliente(10L)));
    when(cuentaRepo.save(any(Cuenta.class))).thenAnswer(inv -> inv.getArgument(0));

    Cuenta creada = service.crear(req);

    assertNotNull(creada);
    assertEquals(0, creada.getSaldo().compareTo(BigDecimal.ZERO));
    verify(movRepo, never()).save(any(Movimiento.class));
    verify(cuentaRepo, times(1)).save(any(Cuenta.class)); // solo la creación
  }

  @Test
  void crear_falla_numero_duplicado() {
    SaveCuentaRequest req = reqBase();
    when(cuentaRepo.findByNumero(req.numero)).thenReturn(Optional.of(new Cuenta()));

    BusinessException ex = assertThrows(BusinessException.class, () -> service.crear(req));
    assertTrue(ex.getMessage().toLowerCase().contains("número"));

    verify(clienteRepo, never()).findById(anyLong());
    verify(cuentaRepo, never()).save(any(Cuenta.class));
    verify(movRepo, never()).save(any(Movimiento.class));
  }

  @Test
  void crear_falla_cliente_no_existe() {
    SaveCuentaRequest req = reqBase();
    when(cuentaRepo.findByNumero(req.numero)).thenReturn(Optional.empty());
    when(clienteRepo.findById(req.clienteId)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> service.crear(req));
    verify(cuentaRepo, never()).save(any(Cuenta.class));
    verify(movRepo, never()).save(any(Movimiento.class));
  }

  // ---------- ACTUALIZAR ----------

  @Test
  void actualizar_ok_cambiando_numero_tipo_y_estado() {
    SaveCuentaRequest req = new SaveCuentaRequest();
    req.numero = "002-XYZ";
    req.tipo = "Corriente";
    req.estado = false;

    Cuenta existente = cuentaExistente(50L, "001-ABC", "Ahorro", true);
    when(cuentaRepo.findById(50L)).thenReturn(Optional.of(existente));
    when(cuentaRepo.findByNumero("002-XYZ")).thenReturn(Optional.empty());
    when(cuentaRepo.save(any(Cuenta.class))).thenAnswer(inv -> inv.getArgument(0));

    Cuenta actualizada = service.actualizar(50L, req);

    assertEquals("002-XYZ", actualizada.getNumero());
    assertEquals("Corriente", actualizada.getTipo());
    assertEquals(false, actualizada.getEstado());

    verify(cuentaRepo).findById(50L);
    verify(cuentaRepo).findByNumero("002-XYZ");
    verify(cuentaRepo).save(existente);
  }

  @Test
  void actualizar_cambiar_numero_con_conflicto_lanza_excepcion() {
    SaveCuentaRequest req = new SaveCuentaRequest();
    req.numero = "DUP-001";
    req.tipo = "Ahorro";
    req.estado = true;

    Cuenta existente = cuentaExistente(60L, "OLD-000", "Corriente", true);
    when(cuentaRepo.findById(60L)).thenReturn(Optional.of(existente));
    when(cuentaRepo.findByNumero("DUP-001")).thenReturn(Optional.of(new Cuenta()));

    BusinessException ex = assertThrows(BusinessException.class, () -> service.actualizar(60L, req));
    assertTrue(ex.getMessage().toLowerCase().contains("número"));

    verify(cuentaRepo, never()).save(any(Cuenta.class));
  }

  @Test
  void actualizar_no_existe_lanza_notfound() {
    SaveCuentaRequest req = new SaveCuentaRequest();
    req.numero = "X";
    req.tipo = "Ahorro";
    req.estado = true;

    when(cuentaRepo.findById(999L)).thenReturn(Optional.empty());
    assertThrows(NotFoundException.class, () -> service.actualizar(999L, req));
  }

  // ---------- LISTAR ----------

  @Test
  void listar_devuelve_todos() {
    when(cuentaRepo.findAll()).thenReturn(Arrays.asList(new Cuenta(), new Cuenta()));
    assertEquals(2, service.listar().size());
    verify(cuentaRepo).findAll();
  }

  @Test
  void listar_vacio() {
    when(cuentaRepo.findAll()).thenReturn(Collections.emptyList());
    assertTrue(service.listar().isEmpty());
  }

  // ---------- LISTAR POR CLIENTE ----------

  @Test
  void listarPorCliente_devuelve() {
    when(cuentaRepo.findByCliente_Id(10L)).thenReturn(Arrays.asList(new Cuenta()));
    assertEquals(1, service.listarPorCliente(10L).size());
    verify(cuentaRepo).findByCliente_Id(10L);
  }

  // ---------- ELIMINAR ----------

  @Test
  void eliminar_ok_existente() {
    when(cuentaRepo.existsById(5L)).thenReturn(true);
    service.eliminar(5L);
    verify(cuentaRepo).existsById(5L);
    verify(cuentaRepo).deleteById(5L);
  }

  @Test
  void eliminar_no_existente_lanza_notfound() {
    when(cuentaRepo.existsById(6L)).thenReturn(false);
    assertThrows(NotFoundException.class, () -> service.eliminar(6L));
    verify(cuentaRepo).existsById(6L);
    verify(cuentaRepo, never()).deleteById(anyLong());
  }

  // ---------- OBTENER ----------

  @Test
  void obtener_ok() {
    Cuenta ct = cuentaExistente(44L, "N-1", "Ahorro", true);
    when(cuentaRepo.findById(44L)).thenReturn(Optional.of(ct));

    Cuenta out = service.obtener(44L);
    assertEquals(44L, out.getId());
    verify(cuentaRepo).findById(44L);
  }

  @Test
  void obtener_no_existente_lanza_notfound() {
    when(cuentaRepo.findById(77L)).thenReturn(Optional.empty());
    assertThrows(NotFoundException.class, () -> service.obtener(77L));
  }
}
