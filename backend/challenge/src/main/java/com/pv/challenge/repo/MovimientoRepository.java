package com.pv.challenge.repo;

import com.pv.challenge.entity.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByCuenta_Id(Long cuentaId);
    
    List<Movimiento> findByCuenta_IdAndFechaBetweenOrderByFechaAsc(Long cuentaId,
                                                                   OffsetDateTime desde,
                                                                   OffsetDateTime hasta);

    List<Movimiento> findByCuenta_IdAndFechaBeforeOrderByFechaAsc(Long cuentaId,
                                                                  OffsetDateTime antesDe);
}
