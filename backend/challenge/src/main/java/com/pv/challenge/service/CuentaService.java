package com.pv.challenge.service;

import com.pv.challenge.entity.Cliente;
import com.pv.challenge.entity.Cuenta;
import com.pv.challenge.entity.Movimiento;
import com.pv.challenge.exception.BusinessException;
import com.pv.challenge.exception.NotFoundException;
import com.pv.challenge.repo.ClienteRepository;
import com.pv.challenge.repo.CuentaRepository;
import com.pv.challenge.repo.MovimientoRepository;
import com.pv.challenge.dto.CuentaDtos.SaveCuentaRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class CuentaService {

    private final CuentaRepository cuentaRepo;
    private final ClienteRepository clienteRepo;
    private final MovimientoRepository movRepo;

    public CuentaService(CuentaRepository cuentaRepo, ClienteRepository clienteRepo, MovimientoRepository movRepo) {
        this.cuentaRepo = cuentaRepo;
        this.clienteRepo = clienteRepo;
        this.movRepo = movRepo;
    }

    @Transactional
    public Cuenta crear(SaveCuentaRequest req) {
        cuentaRepo.findByNumero(req.numero).ifPresent(c -> { throw new BusinessException("Número de cuenta ya existe"); });
        Cliente cli = clienteRepo.findById(req.clienteId)
                .orElseThrow(() -> new NotFoundException("Cliente " + req.clienteId + " no existe"));

        Cuenta ct = new Cuenta();
        ct.setNumero(req.numero);
        ct.setTipo(req.tipo);
        ct.setEstado(req.estado != null ? req.estado : Boolean.TRUE);
        ct.setCliente(cli);
        ct.setSaldo(BigDecimal.ZERO);
        cuentaRepo.save(ct);
        
        BigDecimal ini = req.saldoInicial == null ? BigDecimal.ZERO : req.saldoInicial;
        if (ini.signum() > 0) {
            ct.setSaldo(ini);
            cuentaRepo.save(ct);

            Movimiento m = new Movimiento();
            m.setCuenta(ct);
            m.setTipo("DEPOSITO_INICIAL");
            m.setValor(ini);
            m.setSaldo(ini);
            m.setReferencia("apertura");
            m.setFecha(OffsetDateTime.now());
            movRepo.save(m);
        }
        return ct;
    }

    @Transactional
    public Cuenta actualizar(Long id, SaveCuentaRequest req) {
        Cuenta ct = cuentaRepo.findById(id).orElseThrow(() -> new NotFoundException("Cuenta " + id + " no existe"));
        if (!ct.getNumero().equals(req.numero)) {
            cuentaRepo.findByNumero(req.numero).ifPresent(x -> { throw new BusinessException("Número de cuenta ya existe"); });
            ct.setNumero(req.numero);
        }
        ct.setTipo(req.tipo);
        if (req.estado != null) ct.setEstado(req.estado);
        return cuentaRepo.save(ct);
    }

    @Transactional(readOnly = true)
    public List<Cuenta> listar() { return cuentaRepo.findAll(); }

    @Transactional(readOnly = true)
    public List<Cuenta> listarPorCliente(Long clienteId) { return cuentaRepo.findByCliente_Id(clienteId); }

    @Transactional
    public void eliminar(Long id) {
        if (!cuentaRepo.existsById(id)) throw new NotFoundException("Cuenta " + id + " no existe");
        cuentaRepo.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Cuenta obtener(Long id) {
        return cuentaRepo.findById(id).orElseThrow(() -> new NotFoundException("Cuenta " + id + " no existe"));
    }
}
