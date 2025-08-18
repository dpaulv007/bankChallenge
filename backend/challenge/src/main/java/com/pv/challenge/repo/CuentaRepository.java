package com.pv.challenge.repo;

import com.pv.challenge.entity.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    Optional<Cuenta> findByNumero(String numero);
    List<Cuenta> findByCliente_Id(Long clienteId);
}
