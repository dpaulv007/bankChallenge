import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of, throwError } from 'rxjs';

import { ClientesComponent } from './clientes.component';
import { ApiService } from 'src/app/core/services/api.service';

type ApiServiceMock = {
  listarClientes: jest.Mock;
  crearCliente: jest.Mock;
  actualizarCliente: jest.Mock;
  eliminarCliente: jest.Mock;
};

describe('ClientesComponent', () => {
  let fixture: ComponentFixture<ClientesComponent>;
  let component: ClientesComponent;

  let api: ApiServiceMock;

  beforeEach(async () => {
    api = {
      listarClientes: jest.fn(),
      crearCliente: jest.fn(),
      actualizarCliente: jest.fn(),
      eliminarCliente: jest.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [ClientesComponent, FormsModule, HttpClientTestingModule],
      providers: [{ provide: ApiService, useValue: api }],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();

    fixture = TestBed.createComponent(ClientesComponent);
    component = fixture.componentInstance;
  });

  it('debe crearse y cargar clientes en ngOnInit()', () => {
    const mock = [{ id: 1, nombre: 'Ana' }];
    api.listarClientes.mockReturnValue(of(mock));

    fixture.detectChanges();

    expect(api.listarClientes).toHaveBeenCalledTimes(1);
    expect(component.rows).toEqual(mock);
    expect(component.err).toBe('');
  });

  it('load() debe setear err cuando la API falla', () => {
    api.listarClientes.mockReturnValue(throwError(() => ({ error: { message: 'Fallo listado' } })));

    component.load();

    expect(component.err).toBe('Fallo listado');
  });

  it('submit() crea cuando editId es null, luego reset() y load()', () => {
    api.crearCliente.mockReturnValue(of({}));
    const resetSpy = jest.spyOn(component, 'reset');
    const loadSpy = jest.spyOn(component, 'load').mockImplementation(() => {});

    component.editId = null;
    component.form = {
      nombre: 'Juan',
      genero: 'Masculino',
      edad: 25,
      identificacion: 'ABC',
      direccion: 'Calle 1',
      telefono: '099',
      contrasena: '123',
      estado: true,
    };
    const expected = { ...component.form };

    component.submit();

    expect(api.crearCliente).toHaveBeenCalledWith(expected);
    expect(resetSpy).toHaveBeenCalled();
    expect(loadSpy).toHaveBeenCalled();
    expect(component.err).toBe('');
  });

  it('submit() actualiza cuando editId tiene valor, luego reset() y load()', () => {
    api.actualizarCliente.mockReturnValue(of({}));
    const resetSpy = jest.spyOn(component, 'reset');
    const loadSpy = jest.spyOn(component, 'load').mockImplementation(() => {});

    component.editId = 77;
    component.form = {
      nombre: 'Maria',
      genero: 'Femenino',
      edad: 30,
      identificacion: 'XYZ',
      direccion: 'Av 2',
      telefono: '088',
      contrasena: '321',
      estado: false,
    };
    const expected = { ...component.form };

    component.submit();

    expect(api.actualizarCliente).toHaveBeenCalledWith(77, expected);
    expect(resetSpy).toHaveBeenCalled();
    expect(loadSpy).toHaveBeenCalled();
    expect(component.err).toBe('');
  });

  it('submit() setea err cuando falla crear', () => {
    api.crearCliente.mockReturnValue(
      throwError(() => ({ error: { message: 'No se pudo crear' } }))
    );

    component.editId = null;
    component.submit();

    expect(component.err).toBe('No se pudo crear');
  });

  it('submit() setea err cuando falla actualizar', () => {
    api.actualizarCliente.mockReturnValue(
      throwError(() => ({ error: { message: 'No se pudo actualizar' } }))
    );

    component.editId = 5;
    component.submit();

    expect(component.err).toBe('No se pudo actualizar');
  });

  it('edit() debe popular el formulario y setear editId', () => {
    const row = {
      id: 15,
      nombreCompleto: 'Usuario X',
      genero: 'Masculino',
      edad: 40,
      identificacionCliente: 'ID-555',
      direccion: 'Calle Z',
      telefono: '123456',
      contrasena: 'xx',
      estado: false,
    };

    component.edit(row);

    expect(component.editId).toBe(15);
  });

  it('del() con confirm=true llama eliminarCliente y luego load()', () => {
    jest.spyOn(window, 'confirm').mockReturnValue(true);
    api.eliminarCliente.mockReturnValue(of(void 0));
    const loadSpy = jest.spyOn(component, 'load').mockImplementation(() => {});

    component.del(9);

    expect(api.eliminarCliente).toHaveBeenCalledWith(9);
    expect(loadSpy).toHaveBeenCalled();
  });

  it('del() con confirm=false NO debe llamar eliminarCliente', () => {
    jest.spyOn(window, 'confirm').mockReturnValue(false);

    component.del(9);

    expect(api.eliminarCliente).not.toHaveBeenCalled();
  });

  it('reset() reinicia editId y form por defecto', () => {
    component.editId = 33;
    component.form = { nombre: 'X' } as any;

    component.reset();

    expect(component.editId).toBeNull();
    expect(component.form).toEqual({
      nombre: '',
      genero: 'Masculino',
      edad: 18,
      identificacion: '',
      direccion: '',
      telefono: '',
      contrasena: '',
      estado: true,
    });
  });
});
