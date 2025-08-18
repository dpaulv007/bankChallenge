import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MovimientosComponent } from './movimientos.component';
import { ApiService } from 'src/app/core/services/api.service';
import { of, throwError } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';

type ApiMock = {
  listarMovimientos: jest.Mock;
  listarCuentas: jest.Mock;
  depositar: jest.Mock;
  retirar: jest.Mock;
  transferir: jest.Mock;
};

describe('MovimientosComponent', () => {
  let fixture: ComponentFixture<MovimientosComponent>;
  let component: MovimientosComponent;
  let api: ApiMock;

  beforeEach(async () => {
    api = {
      listarMovimientos: jest.fn(),
      listarCuentas: jest.fn(),
      depositar: jest.fn(),
      retirar: jest.fn(),
      transferir: jest.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [MovimientosComponent],
      providers: [{ provide: ApiService, useValue: api }],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();

    fixture = TestBed.createComponent(MovimientosComponent);
    component = fixture.componentInstance;
  });

  it('debe crearse y cargar cuentas y movimientos en ngOnInit()', () => {
    const mockMovimientos = [{ id: 1, monto: 100 }];
    const mockCuentas = [{ id: 1, numero: '123', tipo: 'Ahorro' }];
    api.listarMovimientos.mockReturnValue(of(mockMovimientos));
    api.listarCuentas.mockReturnValue(of(mockCuentas));

    fixture.detectChanges();

    expect(api.listarMovimientos).toHaveBeenCalled();
    expect(api.listarCuentas).toHaveBeenCalled();
    expect(component.rows).toEqual(mockMovimientos);
    expect(component.cuentas).toEqual(mockCuentas);
  });

  it('load() debe llenar rows con movimientos', () => {
    const mock = [{ id: 2, monto: 200 }];
    api.listarMovimientos.mockReturnValue(of(mock));

    component.load();

    expect(component.rows).toEqual(mock);
  });

  it('depositar() con datos válidos realiza depósito y resetea', () => {
    api.depositar.mockReturnValue(of({}));
    const loadSpy = jest.spyOn(component, 'load').mockImplementation(() => {});
    component.cuentaId = 1;
    component.monto = 50;
    component.ref = 'ref123';

    component.depositar();

    expect(api.depositar).toHaveBeenCalledWith(1, 50, 'ref123');
    expect(component.monto).toBe(0);
    expect(component.ref).toBe('');
    expect(loadSpy).toHaveBeenCalled();
  });

  it('depositar() ignora si datos inválidos', () => {
    component.cuentaId = null;
    component.monto = 0;

    component.depositar();

    expect(api.depositar).not.toHaveBeenCalled();
  });

  it('depositar() setea err si hay error', () => {
    api.depositar.mockReturnValue(
      throwError(() => ({ error: { message: 'Error depósito' } }))
    );
    component.cuentaId = 1;
    component.monto = 100;

    component.depositar();

    expect(component.err).toBe('Error depósito');
  });

  it('retirar() con datos válidos realiza retiro y resetea', () => {
    api.retirar.mockReturnValue(of({}));
    const loadSpy = jest.spyOn(component, 'load').mockImplementation(() => {});
    component.cuentaId = 2;
    component.monto = 80;
    component.ref = 'retiroX';

    component.retirar();

    expect(api.retirar).toHaveBeenCalledWith(2, 80, 'retiroX');
    expect(component.monto).toBe(0);
    expect(component.ref).toBe('');
    expect(loadSpy).toHaveBeenCalled();
  });

  it('retirar() setea err si hay error', () => {
    api.retirar.mockReturnValue(
      throwError(() => ({ error: { message: 'Error retiro' } }))
    );
    component.cuentaId = 3;
    component.monto = 120;

    component.retirar();

    expect(component.err).toBe('Error retiro');
  });

  it('retirar() ignora si datos inválidos', () => {
    component.cuentaId = 0;
    component.monto = -1;

    component.retirar();

    expect(api.retirar).not.toHaveBeenCalled();
  });

  it('transferir() con datos válidos ejecuta transferencia y limpia form', () => {
    api.transferir.mockReturnValue(of({}));
    const loadSpy = jest.spyOn(component, 'load').mockImplementation(() => {});

    component.transfer = {
      origenId: 1,
      destinoId: 2,
      monto: 30,
      ref: 'tref',
    };

    component.transferir();

    expect(api.transferir).toHaveBeenCalledWith(1, 2, 30, 'tref');
    expect(component.transfer).toEqual({
      origenId: null,
      destinoId: null,
      monto: 0,
      ref: '',
    });
    expect(loadSpy).toHaveBeenCalled();
  });

  it('transferir() ignora si datos inválidos', () => {
    component.transfer = {
      origenId: null,
      destinoId: 2,
      monto: 0,
      ref: '',
    };

    component.transferir();

    expect(api.transferir).not.toHaveBeenCalled();
  });

  it('transferir() setea err si hay error', () => {
    api.transferir.mockReturnValue(
      throwError(() => ({ error: { message: 'Falló transferencia' } }))
    );

    component.transfer = {
      origenId: 1,
      destinoId: 2,
      monto: 100,
      ref: 'err',
    };

    component.transferir();

    expect(component.err).toBe('Falló transferencia');
  });
});
