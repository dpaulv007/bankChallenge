package com.pv.challenge.service;

import com.pv.challenge.entity.Cuenta;
import com.pv.challenge.entity.Movimiento;
import com.pv.challenge.exception.BusinessException;
import com.pv.challenge.exception.NotFoundException;
import com.pv.challenge.repo.CuentaRepository;
import com.pv.challenge.repo.MovimientoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
public class MovimientoService {
  private final CuentaRepository cuentaRepo;
  private final MovimientoRepository movRepo;

  public MovimientoService(CuentaRepository cuentaRepo, MovimientoRepository movRepo) {
    this.cuentaRepo = cuentaRepo;
    this.movRepo = movRepo;
  }

  private void assertMontoPositivo(BigDecimal monto) {
    if (monto == null || monto.signum() <= 0) {
      throw new BusinessException("El monto debe ser mayor a 0.");
    }
  }

  @Transactional
  public Movimiento depositar(Long cuentaId, BigDecimal monto, String ref) {
    assertMontoPositivo(monto);
    Cuenta c = cuentaRepo.findById(cuentaId)
        .orElseThrow(() -> new NotFoundException("Cuenta " + cuentaId + " no existe"));

    BigDecimal nuevoSaldo = c.getSaldo().add(monto);
    c.setSaldo(nuevoSaldo);
    cuentaRepo.save(c);

    Movimiento m = new Movimiento();
    m.setCuenta(c);
    m.setTipo("DEPOSITO");
    m.setValor(monto);
    m.setSaldo(nuevoSaldo);
    m.setReferencia(ref);
    m.setFecha(OffsetDateTime.now());
    return movRepo.save(m);
  }

  @Transactional
  public Movimiento retirar(Long cuentaId, BigDecimal monto, String ref) {
    assertMontoPositivo(monto);
    Cuenta c = cuentaRepo.findById(cuentaId)
        .orElseThrow(() -> new NotFoundException("Cuenta " + cuentaId + " no existe"));
    if (c.getSaldo() == null || c.getSaldo().compareTo(monto) < 0) {
      throw new BusinessException("Saldo no disponible.");
    }
    BigDecimal nuevoSaldo = c.getSaldo().subtract(monto);
    c.setSaldo(nuevoSaldo);
    cuentaRepo.save(c);

    Movimiento m = new Movimiento();
    m.setCuenta(c);
    m.setTipo("RETIRO");
    m.setValor(monto);
    m.setSaldo(nuevoSaldo);
    m.setReferencia(ref);
    m.setFecha(OffsetDateTime.now());
    return movRepo.save(m);
  }

  @Transactional
  public void transferir(Long idOrigen, Long idDestino, BigDecimal monto, String ref) {
    if (idOrigen.equals(idDestino)) throw new BusinessException("La cuenta destino debe ser distinta a la de origen");
    retirar(idOrigen, monto, (ref == null ? "" : ref) + " - debito");
    depositar(idDestino, monto, (ref == null ? "" : ref) + " - credito");
  }
}
