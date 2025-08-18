package com.pv.challenge.service;

import com.pv.challenge.entity.Cuenta;
import com.pv.challenge.entity.Movimiento;
import com.pv.challenge.exception.BusinessException;
import com.pv.challenge.exception.NotFoundException;
import com.pv.challenge.repo.CuentaRepository;
import com.pv.challenge.repo.MovimientoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovimientoServiceTest {

  private CuentaRepository cuentaRepo;
  private MovimientoRepository movRepo;
  private MovimientoService service;

  @BeforeEach
  void setup() {
    cuentaRepo = mock(CuentaRepository.class);
    movRepo = mock(MovimientoRepository.class);
    service = new MovimientoService(cuentaRepo, movRepo);
  }
  
  private Cuenta cuenta(long id, String numero, BigDecimal saldo) {
    Cuenta c = new Cuenta();
    c.setId(id);
    c.setNumero(numero);
    c.setSaldo(saldo);
    c.setEstado(true);
    return c;
  }

  // ---------- DEPOSITAR ----------

  @Test
  void depositar_ok_actualiza_saldo_y_persiste_movimiento() {
    Long cuentaId = 10L;
    BigDecimal monto = new BigDecimal("25.00");
    Cuenta c = cuenta(cuentaId, "001", new BigDecimal("50.00"));

    when(cuentaRepo.findById(cuentaId)).thenReturn(Optional.of(c));
    when(cuentaRepo.save(any(Cuenta.class))).thenAnswer(inv -> inv.getArgument(0));
    when(movRepo.save(any(Movimiento.class))).thenAnswer(inv -> inv.getArgument(0));

    Movimiento m = service.depositar(cuentaId, monto, "ref-dep");

    // Validar saldo actualizado
    assertEquals(0, c.getSaldo().compareTo(new BigDecimal("75.00")));
    // Movimiento retornado
    assertNotNull(m);
    assertEquals("DEPOSITO", m.getTipo());
    assertEquals(0, m.getValor().compareTo(monto));
    assertEquals(0, m.getSaldo().compareTo(new BigDecimal("75.00")));
    assertEquals("ref-dep", m.getReferencia());
    assertNotNull(m.getFecha());
    assertSame(c, m.getCuenta());

    // Capturar registro persistido en repo de movimientos
    ArgumentCaptor<Movimiento> cap = ArgumentCaptor.forClass(Movimiento.class);
    verify(movRepo).save(cap.capture());
    Movimiento guardado = cap.getValue();
    assertEquals("DEPOSITO", guardado.getTipo());
    assertEquals(0, guardado.getValor().compareTo(monto));

    verify(cuentaRepo).findById(cuentaId);
    verify(cuentaRepo).save(c);
  }

  @Test
  void depositar_monto_null_lanza_business() {
    assertThrows(BusinessException.class, () -> service.depositar(1L, null, "x"));
    verifyNoInteractions(cuentaRepo, movRepo);
  }

  @Test
  void depositar_monto_cero_o_negativo_lanza_business() {
    assertThrows(BusinessException.class, () -> service.depositar(1L, BigDecimal.ZERO, "x"));
    assertThrows(BusinessException.class, () -> service.depositar(1L, new BigDecimal("-1"), "x"));
    verifyNoInteractions(cuentaRepo, movRepo);
  }

  @Test
  void depositar_cuenta_no_existe_lanza_notfound() {
    when(cuentaRepo.findById(99L)).thenReturn(Optional.empty());
    assertThrows(NotFoundException.class, () -> service.depositar(99L, new BigDecimal("10"), "x"));
    verify(cuentaRepo).findById(99L);
    verify(movRepo, never()).save(any());
  }

  // ---------- RETIRAR ----------

  @Test
  void retirar_ok_actualiza_saldo_y_persiste_movimiento() {
    Long cuentaId = 20L;
    BigDecimal monto = new BigDecimal("40.00");
    Cuenta c = cuenta(cuentaId, "002", new BigDecimal("100.00"));

    when(cuentaRepo.findById(cuentaId)).thenReturn(Optional.of(c));
    when(cuentaRepo.save(any(Cuenta.class))).thenAnswer(inv -> inv.getArgument(0));
    when(movRepo.save(any(Movimiento.class))).thenAnswer(inv -> inv.getArgument(0));

    Movimiento m = service.retirar(cuentaId, monto, "ref-ret");

    assertEquals(0, c.getSaldo().compareTo(new BigDecimal("60.00")));
    assertNotNull(m);
    assertEquals("RETIRO", m.getTipo());
    assertEquals(0, m.getValor().compareTo(monto));
    assertEquals(0, m.getSaldo().compareTo(new BigDecimal("60.00")));
    assertEquals("ref-ret", m.getReferencia());
    assertNotNull(m.getFecha());
    assertSame(c, m.getCuenta());

    verify(cuentaRepo).findById(cuentaId);
    verify(cuentaRepo).save(c);
    verify(movRepo).save(any(Movimiento.class));
  }

  @Test
  void retirar_saldo_insuficiente_lanza_business() {
    Long cuentaId = 21L;
    Cuenta c = cuenta(cuentaId, "003", new BigDecimal("10.00"));
    when(cuentaRepo.findById(cuentaId)).thenReturn(Optional.of(c));

    assertThrows(BusinessException.class, () -> service.retirar(cuentaId, new BigDecimal("50.00"), "x"));
    // No debe guardar ni cuenta ni movimiento
    verify(cuentaRepo, never()).save(any(Cuenta.class));
    verify(movRepo, never()).save(any(Movimiento.class));
  }

  @Test
  void retirar_con_saldo_null_lanza_business() {
    Long cuentaId = 22L;
    Cuenta c = cuenta(cuentaId, "004", null);
    when(cuentaRepo.findById(cuentaId)).thenReturn(Optional.of(c));

    assertThrows(BusinessException.class, () -> service.retirar(cuentaId, new BigDecimal("1.00"), "x"));
    verify(cuentaRepo, never()).save(any(Cuenta.class));
    verify(movRepo, never()).save(any(Movimiento.class));
  }

  @Test
  void retirar_cuenta_no_existe_lanza_notfound() {
    when(cuentaRepo.findById(88L)).thenReturn(Optional.empty());
    assertThrows(NotFoundException.class, () -> service.retirar(88L, new BigDecimal("10.00"), "x"));
    verify(cuentaRepo).findById(88L);
    verify(movRepo, never()).save(any());
  }

  // ---------- TRANSFERIR ----------

  @Test
  void transferir_ok_debita_origen_y_acredita_destino_con_movimientos() {
    Long idOrigen = 30L, idDestino = 31L;
    BigDecimal monto = new BigDecimal("30.00");

    Cuenta origen = cuenta(idOrigen, "OR-1", new BigDecimal("100.00"));
    Cuenta destino = cuenta(idDestino, "DE-1", new BigDecimal("10.00"));

    when(cuentaRepo.findById(idOrigen)).thenReturn(Optional.of(origen));
    when(cuentaRepo.findById(idDestino)).thenReturn(Optional.of(destino));
    when(cuentaRepo.save(any(Cuenta.class))).thenAnswer(inv -> inv.getArgument(0));
    when(movRepo.save(any(Movimiento.class))).thenAnswer(inv -> inv.getArgument(0));

    service.transferir(idOrigen, idDestino, monto, "pago");

    // Saldos finales
    assertEquals(0, origen.getSaldo().compareTo(new BigDecimal("70.00")));
    assertEquals(0, destino.getSaldo().compareTo(new BigDecimal("40.00")));

    // Debe haber guardado cuenta dos veces (retiro y depósito) y 2 movimientos
    verify(cuentaRepo, times(2)).save(any(Cuenta.class));
    verify(movRepo, times(2)).save(any(Movimiento.class));

    // Verificar tipos y referencias de movimientos
    ArgumentCaptor<Movimiento> capMov = ArgumentCaptor.forClass(Movimiento.class);
    verify(movRepo, times(2)).save(capMov.capture());

    boolean retiroOk = capMov.getAllValues().stream()
        .anyMatch(mm -> "RETIRO".equals(mm.getTipo())
            && 0 == mm.getValor().compareTo(monto)
            && mm.getReferencia() != null
            && mm.getReferencia().contains("pago")
            && mm.getReferencia().contains("debito"));

    boolean depositoOk = capMov.getAllValues().stream()
        .anyMatch(mm -> "DEPOSITO".equals(mm.getTipo())
            && 0 == mm.getValor().compareTo(monto)
            && mm.getReferencia() != null
            && mm.getReferencia().contains("pago")
            && mm.getReferencia().contains("credito"));

    assertTrue(retiroOk, "Debe registrar movimiento de RETIRO con referencia '- debito'");
    assertTrue(depositoOk, "Debe registrar movimiento de DEPOSITO con referencia '- credito'");
  }

  @Test
  void transferir_misma_cuenta_lanza_business() {
    assertThrows(BusinessException.class,
        () -> service.transferir(1L, 1L, new BigDecimal("10.00"), "x"));
    verifyNoInteractions(cuentaRepo, movRepo);
  }
}
