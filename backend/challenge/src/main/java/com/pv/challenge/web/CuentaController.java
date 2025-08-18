package com.pv.challenge.web;

import com.pv.challenge.entity.Cuenta;
import com.pv.challenge.service.CuentaService;
import com.pv.challenge.dto.CuentaDtos.SaveCuentaRequest;
import com.pv.challenge.dto.CuentaDtos.CuentaResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

    private final CuentaService service;

    public CuentaController(CuentaService service) {
        this.service = service;
    }

    @GetMapping
    public List<CuentaResponse> listar() { 
        return service.listar().stream().map(this::map).collect(Collectors.toList()); 
    }

    @GetMapping("/cliente/{clienteId}")
    public List<CuentaResponse> listarPorCliente(@PathVariable Long clienteId) { 
        return service.listarPorCliente(clienteId).stream().map(this::map).collect(Collectors.toList()); 
    }

    @GetMapping("/{id}")
    public CuentaResponse obtener(@PathVariable Long id) { 
        return map(service.obtener(id)); 
    }

    @PostMapping
    public CuentaResponse crear(@RequestBody SaveCuentaRequest req) { 
        return map(service.crear(req)); 
    }

    @PutMapping("/{id}")
    public CuentaResponse actualizar(@PathVariable Long id, @RequestBody SaveCuentaRequest req) { 
        return map(service.actualizar(id, req)); 
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) { 
        service.eliminar(id); 
    }

    private CuentaResponse map(Cuenta c) {
        CuentaResponse r = new CuentaResponse();
        r.id = c.getId();
        r.numero = c.getNumero();
        r.tipo = c.getTipo();
        r.saldo = c.getSaldo();
        r.estado = c.getEstado();
        r.clienteId = c.getCliente().getId();
        r.clienteNombre = c.getCliente().getPersona().getNombre();
        return r;
    }
}
