import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';

import { CuentasComponent } from './cuentas.component';
import { ApiService } from 'src/app/core/services/api.service';

type ApiServiceMock = {
  listarClientes: jest.Mock;
  listarCuentas: jest.Mock;
  crearCuenta: jest.Mock;
  actualizarCuenta: jest.Mock;
  eliminarCuenta: jest.Mock;
};

describe('CuentasComponent', () => {
  let fixture: ComponentFixture<CuentasComponent>;
  let component: CuentasComponent;
  let api: ApiServiceMock;

  beforeEach(async () => {
    api = {
      listarClientes: jest.fn(),
      listarCuentas: jest.fn(),
      crearCuenta: jest.fn(),
      actualizarCuenta: jest.fn(),
      eliminarCuenta: jest.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [CuentasComponent, FormsModule],
      providers: [{ provide: ApiService, useValue: api }],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();

    fixture = TestBed.createComponent(CuentasComponent);
    component = fixture.componentInstance;
  });

  it('debe crearse y cargar clientes y cuentas en ngOnInit()', () => {
    const mockClientes = [{ id: 1, nombre: 'Carlos' }];
    const mockCuentas = [{ id: 99, numero: 'X123' }];

    api.listarClientes.mockReturnValue(of(mockClientes));
    api.listarCuentas.mockReturnValue(of(mockCuentas));

    fixture.detectChanges();

    expect(api.listarClientes).toHaveBeenCalled();
    expect(api.listarCuentas).toHaveBeenCalled();
    expect(component.clientes).toEqual(mockClientes);
    expect(component.rows).toEqual(mockCuentas);
  });

  it('load() debe llenar rows con cuentas', () => {
    const mock = [{ id: 1, numero: '12345' }];
    api.listarCuentas.mockReturnValue(of(mock));

    component.load();

    expect(component.rows).toEqual(mock);
  });

  it('submit() crea cuenta si editId es null', () => {
  api.crearCuenta.mockReturnValue(of({}));
  const loadSpy = jest.spyOn(component, 'load').mockImplementation(() => {});
  const resetSpy = jest.spyOn(component, 'reset');

  component.editId = null;
  component.form = {
    numero: '9876',
    tipo: 'Ahorro',
    saldoInicial: 0,
    estado: true,
    clienteId: 0,
  };

  component.submit();

  expect(api.crearCuenta).toHaveBeenCalledWith({
    numero: '9876',
    tipo: 'Ahorro',
    saldoInicial: 0,
    estado: true,
    clienteId: 0,
  });
  expect(resetSpy).toHaveBeenCalled();
  expect(loadSpy).toHaveBeenCalled();
});


  it('submit() actualiza cuenta si editId tiene valor', () => {
  api.actualizarCuenta.mockReturnValue(of({}));
  const loadSpy = jest.spyOn(component, 'load').mockImplementation(() => {});
  const resetSpy = jest.spyOn(component, 'reset');

  component.editId = 55;
  component.form = {
    numero: '8888',
    tipo: 'Ahorro',
    saldoInicial: 0,
    estado: true,
    clienteId: 0,
  };

  component.submit();

  expect(api.actualizarCuenta).toHaveBeenCalledWith(55, {
    numero: '8888',
    tipo: 'Ahorro',
    saldoInicial: 0,
    estado: true,
    clienteId: 0,
  });
  expect(resetSpy).toHaveBeenCalled();
  expect(loadSpy).toHaveBeenCalled();
});


  it('submit() setea err cuando ocurre error en crear', () => {
    api.crearCuenta.mockReturnValue(throwError(() => ({ error: { message: 'Error creando' } })));

    component.editId = null;
    component.submit();

    expect(component.err).toBe('Error creando');
  });

  it('submit() setea err cuando ocurre error en actualizar', () => {
    api.actualizarCuenta.mockReturnValue(throwError(() => ({ error: { message: 'Error actualizando' } })));

    component.editId = 10;
    component.submit();

    expect(component.err).toBe('Error actualizando');
  });

  it('edit() debe setear editId y valores del form', () => {
    const cuenta = {
      id: 10,
      numero: '9999',
      tipo: 'Corriente',
      saldoInicial: 100,
      estado: false,
      clienteId: 2,
    };

    component.edit(cuenta);

    expect(component.editId).toBe(10);
    expect(component.form).toEqual({
      numero: '9999',
      tipo: 'Corriente',
      saldoInicial: 100,
      estado: false,
      clienteId: 2,
    });
  });

  it('del() con confirm=true llama eliminarCuenta y luego load()', () => {
    jest.spyOn(window, 'confirm').mockReturnValue(true);
    api.eliminarCuenta.mockReturnValue(of(void 0));
    const loadSpy = jest.spyOn(component, 'load').mockImplementation(() => {});

    component.del(3);

    expect(api.eliminarCuenta).toHaveBeenCalledWith(3);
    expect(loadSpy).toHaveBeenCalled();
  });

  it('del() con confirm=false NO llama eliminarCuenta', () => {
    jest.spyOn(window, 'confirm').mockReturnValue(false);

    component.del(3);

    expect(api.eliminarCuenta).not.toHaveBeenCalled();
  });

  it('reset() reinicia editId y form a valores por defecto', () => {
    component.editId = 22;
    component.form = { numero: 'X' } as any;

    component.reset();

    expect(component.editId).toBeNull();
    expect(component.form).toEqual({
      numero: '',
      tipo: 'Ahorro',
      saldoInicial: 0,
      estado: true,
      clienteId: 0,
    });
  });
});
