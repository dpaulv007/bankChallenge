package com.pv.challenge.web;

import com.pv.challenge.dto.MovimientoDtos;
import com.pv.challenge.entity.Movimiento;
import com.pv.challenge.repo.MovimientoRepository;
import com.pv.challenge.service.MovimientoService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/movimientos")
public class MovimientoController {

    private final MovimientoService service;
    private final MovimientoRepository movRepo;

    public MovimientoController(MovimientoService service, MovimientoRepository movRepo) {
        this.service = service;
        this.movRepo = movRepo;
    }

    @GetMapping
    public List<MovimientoDtos.MovimientoResponse> listar(@RequestParam(required = false) Long cuentaId) {
        List<Movimiento> movimientos = cuentaId == null ? movRepo.findAll() : movRepo.findByCuenta_Id(cuentaId);
        return movimientos.stream().map(MovimientoDtos.MovimientoResponse::new).collect(Collectors.toList());
    }

    @PostMapping("/deposito")
    public MovimientoDtos.MovimientoResponse depositar(@RequestParam Long cuentaId,
                                @RequestParam BigDecimal monto,
                                @RequestParam(required = false) String ref) {
        return new MovimientoDtos.MovimientoResponse(service.depositar(cuentaId, monto, ref));
    }

    @PostMapping("/retiro")
    public MovimientoDtos.MovimientoResponse retirar(@RequestParam Long cuentaId,
                              @RequestParam BigDecimal monto,
                              @RequestParam(required = false) String ref) {
        return new MovimientoDtos.MovimientoResponse(service.retirar(cuentaId, monto, ref));
    }

    @PostMapping("/transferencia")
    public void transferir(@RequestParam Long origenId,
                           @RequestParam Long destinoId,
                           @RequestParam BigDecimal monto,
                           @RequestParam(required = false) String ref) {
        service.transferir(origenId, destinoId, monto, ref);
    }
}
