package com.pv.challenge.service;

import com.pv.challenge.dto.ClienteDtos.SaveClienteRequest;
import com.pv.challenge.entity.Cliente;
import com.pv.challenge.entity.Persona;
import com.pv.challenge.exception.BusinessException;
import com.pv.challenge.exception.NotFoundException;
import com.pv.challenge.repo.ClienteRepository;
import com.pv.challenge.repo.PersonaRepository;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {

  private final PersonaRepository personaRepo;
  private final ClienteRepository clienteRepo;

  public ClienteService(
    PersonaRepository personaRepo,
    ClienteRepository clienteRepo
  ) {
    this.personaRepo = personaRepo;
    this.clienteRepo = clienteRepo;
  }

  @Transactional
  public Cliente crear(SaveClienteRequest req) {
    personaRepo
      .findByIdentificacion(req.identificacion)
      .ifPresent(p -> {
        throw new BusinessException("La identificación ya existe");
      });
    clienteRepo
      .findByClienteId(req.clienteId)
      .ifPresent(c -> {
        throw new BusinessException("El clienteId ya existe");
      });

    Persona p = new Persona();
    p.setNombre(req.nombre);
    p.setGenero(req.genero);
    p.setEdad(req.edad);
    p.setIdentificacion(req.identificacion);
    p.setDireccion(req.direccion);
    p.setTelefono(req.telefono);
    personaRepo.save(p);

    Cliente c = new Cliente();
    c.setId(p.getId());
    c.setPersona(p);
    c.setClienteId(req.clienteId);
    c.setContrasena(req.contrasena);
    c.setEstado(req.estado != null ? req.estado : Boolean.TRUE);
    return clienteRepo.save(c);
  }

  @Transactional
  public Cliente actualizar(Long id, SaveClienteRequest req) {
    Cliente c = clienteRepo
      .findById(id)
      .orElseThrow(() -> new NotFoundException("Cliente " + id + " no existe"));
    Persona p = c.getPersona();

    boolean cambioIdent = !p.getIdentificacion().equals(req.identificacion);
    boolean cambioClienteId = !c.getClienteId().equals(req.clienteId);
    
    if (cambioIdent) {
      personaRepo
        .findByIdentificacion(req.identificacion)
        .ifPresent(other -> {
          throw new BusinessException("La identificación ya existe");
        });
    }
    if (cambioClienteId) {
      clienteRepo
        .findByClienteId(req.clienteId)
        .ifPresent(other -> {
          throw new BusinessException("El clienteId ya existe");
        });
    }
    
    if (cambioIdent) p.setIdentificacion(req.identificacion);
    p.setNombre(req.nombre);
    p.setGenero(req.genero);
    p.setEdad(req.edad);
    p.setDireccion(req.direccion);
    p.setTelefono(req.telefono);
    personaRepo.save(p);
    
    if (cambioClienteId) c.setClienteId(req.clienteId);
    c.setContrasena(req.contrasena);
    if (req.estado != null) c.setEstado(req.estado);

    return clienteRepo.save(c);
  }

  @Transactional
  public void eliminar(Long id) {
    if (!clienteRepo.existsById(id)) throw new NotFoundException(
      "Cliente " + id + " no existe"
    );
    clienteRepo.deleteById(id);
  }

  @Transactional(readOnly = true)
    public List<Cliente> listar() {
        return clienteRepo.findAll();
    }

  public Cliente obtener(Long id) {
    return clienteRepo
      .findById(id)
      .orElseThrow(() -> new NotFoundException("Cliente " + id + " no existe"));
  }
}
