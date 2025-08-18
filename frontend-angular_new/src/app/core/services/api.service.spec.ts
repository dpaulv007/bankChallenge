import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ApiService } from './api.service';

describe('ApiService', () => {
  let service: ApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ApiService]
    });
    service = TestBed.inject(ApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('debe crearse', () => {
    expect(service).toBeTruthy();
  });

  describe('Clientes', () => {
    it('listarClientes() debe hacer GET /api/clientes', () => {
      const mockClientes = [{ id: 1, nombre: 'Test' }];

      service.listarClientes().subscribe(clientes => {
        expect(clientes).toEqual(mockClientes);
      });

      const req = httpMock.expectOne('/api/clientes');
      expect(req.request.method).toBe('GET');
      req.flush(mockClientes);
    });

    it('crearCliente() debe hacer POST /api/clientes', () => {
      const cliente = { nombre: 'Nuevo Cliente' };
      const mockResponse = { id: 1, ...cliente };

      service.crearCliente(cliente).subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne('/api/clientes');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(cliente);
      req.flush(mockResponse);
    });

    it('actualizarCliente() debe hacer PUT /api/clientes/{id}', () => {
      const id = 1;
      const cliente = { nombre: 'Cliente Actualizado' };
      const mockResponse = { id, ...cliente };

      service.actualizarCliente(id, cliente).subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne(`/api/clientes/${id}`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(cliente);
      req.flush(mockResponse);
    });

    it('eliminarCliente() debe hacer DELETE /api/clientes/{id}', () => {
      const id = 1;

      service.eliminarCliente(id).subscribe();

      const req = httpMock.expectOne(`/api/clientes/${id}`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });
  });

  describe('Cuentas', () => {
    it('listarCuentas() debe hacer GET /api/cuentas', () => {
      const mockCuentas = [{ id: 1, numero: '123' }];

      service.listarCuentas().subscribe(cuentas => {
        expect(cuentas).toEqual(mockCuentas);
      });

      const req = httpMock.expectOne('/api/cuentas');
      expect(req.request.method).toBe('GET');
      req.flush(mockCuentas);
    });

    it('crearCuenta() debe hacer POST /api/cuentas', () => {
      const cuenta = { numero: '456', tipo: 'Ahorro' };
      const mockResponse = { id: 1, ...cuenta };

      service.crearCuenta(cuenta).subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne('/api/cuentas');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(cuenta);
      req.flush(mockResponse);
    });
  });

  describe('Movimientos', () => {
    it('depositar() debe hacer POST /api/movimientos/deposito con params', () => {
      const cuentaId = 1;
      const monto = 100;
      const ref = 'deposito test';

      service.depositar(cuentaId, monto, ref).subscribe();

      const req = httpMock.expectOne(request => 
        request.url === '/api/movimientos/deposito' &&
        request.params.get('cuentaId') === '1' &&
        request.params.get('monto') === '100' &&
        request.params.get('ref') === 'deposito test'
      );
      expect(req.request.method).toBe('POST');
      req.flush({});
    });

    it('retirar() debe hacer POST /api/movimientos/retiro con params', () => {
      const cuentaId = 2;
      const monto = 50;
      const ref = 'retiro test';

      service.retirar(cuentaId, monto, ref).subscribe();

      const req = httpMock.expectOne(request => 
        request.url === '/api/movimientos/retiro' &&
        request.params.get('cuentaId') === '2' &&
        request.params.get('monto') === '50' &&
        request.params.get('ref') === 'retiro test'
      );
      expect(req.request.method).toBe('POST');
      req.flush({});
    });

    it('transferir() debe hacer POST /api/movimientos/transferencia con params', () => {
      const origenId = 1;
      const destinoId = 2;
      const monto = 75;
      const ref = 'transferencia test';

      service.transferir(origenId, destinoId, monto, ref).subscribe();

      const req = httpMock.expectOne(request => 
        request.url === '/api/movimientos/transferencia' &&
        request.params.get('origenId') === '1' &&
        request.params.get('destinoId') === '2' &&
        request.params.get('monto') === '75' &&
        request.params.get('ref') === 'transferencia test'
      );
      expect(req.request.method).toBe('POST');
      req.flush(null);
    });

    it('listarMovimientos() debe hacer GET /api/movimientos', () => {
      const mockMovimientos = [{ id: 1, monto: 100 }];

      service.listarMovimientos().subscribe(movimientos => {
        expect(movimientos).toEqual(mockMovimientos);
      });

      const req = httpMock.expectOne('/api/movimientos');
      expect(req.request.method).toBe('GET');
      req.flush(mockMovimientos);
    });
  });

  describe('Reportes', () => {
    it('reporteJSON() debe hacer GET /api/reportes/json con params', () => {
      const clienteId = 1;
      const desde = '2025-01-01';
      const hasta = '2025-01-31';
      const mockReporte = { cliente: 'Test', movimientos: [] };

      service.reporteJSON(clienteId, desde, hasta).subscribe(reporte => {
        expect(reporte).toEqual(mockReporte);
      });

      const req = httpMock.expectOne(request => 
        request.url === '/api/reportes/json' &&
        request.params.get('clienteId') === '1' &&
        request.params.get('desde') === '2025-01-01' &&
        request.params.get('hasta') === '2025-01-31'
      );
      expect(req.request.method).toBe('GET');
      req.flush(mockReporte);
    });

    it('reportePDF() debe hacer GET /api/reportes/pdf con responseType blob', () => {
      const clienteId = 2;
      const desde = '2025-01-01';
      const hasta = '2025-01-31';
      const mockBlob = new Blob(['pdf content'], { type: 'application/pdf' });

      service.reportePDF(clienteId, desde, hasta).subscribe(blob => {
        expect(blob).toEqual(mockBlob);
      });

      const req = httpMock.expectOne(request => 
        request.url === '/api/reportes/pdf' &&
        request.params.get('clienteId') === '2' &&
        request.params.get('desde') === '2025-01-01' &&
        request.params.get('hasta') === '2025-01-31'
      );
      expect(req.request.method).toBe('GET');
      req.flush(mockBlob);
    });
  });
});