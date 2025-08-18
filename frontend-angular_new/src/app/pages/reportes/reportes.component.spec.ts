import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReportesComponent } from './reportes.component';
import { ApiService } from 'src/app/core/services/api.service';
import { of, throwError } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';

type ApiMock = {
  listarClientes: jest.Mock;
  reporteJSON: jest.Mock;
  reportePDF: jest.Mock;
};

describe('ReportesComponent', () => {
  let fixture: ComponentFixture<ReportesComponent>;
  let component: ReportesComponent;
  let api: ApiMock;

  beforeEach(async () => {
    api = {
      listarClientes: jest.fn(),
      reporteJSON: jest.fn(),
      reportePDF: jest.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [ReportesComponent],
      providers: [{ provide: ApiService, useValue: api }],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();

    fixture = TestBed.createComponent(ReportesComponent);
    component = fixture.componentInstance;
  });

  it('debe crearse y cargar clientes en ngOnInit()', () => {
    const clientesMock = [{ id: 1, nombre: 'Cliente 1' }];
    api.listarClientes.mockReturnValue(of(clientesMock));

    fixture.detectChanges();

    expect(api.listarClientes).toHaveBeenCalled();
    expect(component.clientes).toEqual(clientesMock);
    const hoy = new Date().toISOString().slice(0, 10);
    expect(component.desde).toBe(hoy);
    expect(component.hasta).toBe(hoy);
  });

  it('ver() debe llamar reporteJSON si hay clienteId', () => {
    const dataMock = { movimientos: [] };
    api.reporteJSON.mockReturnValue(of(dataMock));
    component.clienteId = 1;
    component.desde = '2025-01-01';
    component.hasta = '2025-01-31';

    component.ver();

    expect(api.reporteJSON).toHaveBeenCalledWith(1, '2025-01-01', '2025-01-31');
    expect(component.data).toEqual(dataMock);
    expect(component.err).toBe('');
  });

  it('ver() no hace nada si clienteId es null', () => {
    component.clienteId = null;

    component.ver();

    expect(api.reporteJSON).not.toHaveBeenCalled();
  });

  it('ver() debe setear error si falla', () => {
    api.reporteJSON.mockReturnValue(
      throwError(() => ({ error: { message: 'Error reporte' } }))
    );
    component.clienteId = 2;
    component.desde = '2025-01-01';
    component.hasta = '2025-01-31';

    component.ver();

    expect(component.err).toBe('Error reporte');
  });

  it('descargar() debe llamar reportePDF y simular descarga', () => {
    const blob = new Blob(['data'], { type: 'application/pdf' });
    api.reportePDF.mockReturnValue(of(blob));
    const removeSpy = jest.fn();
    const clickSpy = jest.fn();
    jest.spyOn(document, 'createElement').mockReturnValue({
      set href(value: string) {},
      set download(value: string) {},
      click: clickSpy,
      remove: removeSpy,
    } as any);

    component.clienteId = 5;
    component.desde = '2025-01-01';
    component.hasta = '2025-01-31';

    component.descargar();

    expect(api.reportePDF).toHaveBeenCalledWith(5, '2025-01-01', '2025-01-31');
  });
});
