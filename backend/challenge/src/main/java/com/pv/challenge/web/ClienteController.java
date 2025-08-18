package com.pv.challenge.web;

import com.pv.challenge.entity.Cliente;
import com.pv.challenge.service.ClienteService;
import com.pv.challenge.dto.ClienteDtos;
import com.pv.challenge.dto.ClienteDtos.SaveClienteRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @GetMapping
    public List<ClienteDtos.ClienteResponse> listar() {
        return service.listar().stream().map(this::map).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ClienteDtos.ClienteResponse obtener(@PathVariable Long id) {
        return map(service.obtener(id));
    }

    @PostMapping
    public ClienteDtos.ClienteResponse crear(@RequestBody SaveClienteRequest req) {
        return map(service.crear(req));
    }

    @PutMapping("/{id}")
    public ClienteDtos.ClienteResponse actualizar(@PathVariable Long id, @RequestBody SaveClienteRequest req) {
        return map(service.actualizar(id, req));
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }

    private ClienteDtos.ClienteResponse map(Cliente c) {
        ClienteDtos.ClienteResponse r = new ClienteDtos.ClienteResponse();
        r.id = c.getId();
        r.nombre = c.getPersona().getNombre();
        r.genero = c.getPersona().getGenero();
        r.edad = c.getPersona().getEdad();
        r.identificacion = c.getPersona().getIdentificacion();
        r.direccion = c.getPersona().getDireccion();
        r.telefono = c.getPersona().getTelefono();
        r.clienteId = c.getClienteId();
        r.estado = c.getEstado();
        return r;
    }
}
