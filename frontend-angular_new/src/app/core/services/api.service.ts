import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ApiService {
  constructor(private http: HttpClient) {}
  private base = '/api';

  // --- Clientes ---
  listarClientes(): Observable<any[]> { return this.http.get<any[]>(`${this.base}/clientes`); }
  crearCliente(body: any): Observable<any> { return this.http.post(`${this.base}/clientes`, body); }
  actualizarCliente(id: number, body: any): Observable<any> { return this.http.put(`${this.base}/clientes/${id}`, body); }
  eliminarCliente(id: number): Observable<void> { return this.http.delete<void>(`${this.base}/clientes/${id}`); }

  // --- Cuentas ---
  listarCuentas(): Observable<any[]> { return this.http.get<any[]>(`${this.base}/cuentas`); }
  crearCuenta(body: any): Observable<any> { return this.http.post(`${this.base}/cuentas`, body); }
  actualizarCuenta(id: number, body: any): Observable<any> { return this.http.put(`${this.base}/cuentas/${id}`, body); }
  eliminarCuenta(id: number): Observable<void> { return this.http.delete<void>(`${this.base}/cuentas/${id}`); }

  // --- Movimientos ---
  depositar(cuentaId: number, monto: number, ref?: string): Observable<any> {
    const params = new HttpParams()
      .set('cuentaId', String(cuentaId))
      .set('monto', String(monto))
      .set('ref', ref || '');
    return this.http.post(`${this.base}/movimientos/deposito`, null, { params });
  }

  retirar(cuentaId: number, monto: number, ref?: string): Observable<any> {
    const params = new HttpParams()
      .set('cuentaId', String(cuentaId))
      .set('monto', String(monto))
      .set('ref', ref || '');
    return this.http.post(`${this.base}/movimientos/retiro`, null, { params });
  }

  transferir(origenId: number, destinoId: number, monto: number, ref?: string): Observable<void> {
    const params = new HttpParams()
      .set('origenId', String(origenId))
      .set('destinoId', String(destinoId))
      .set('monto', String(monto))
      .set('ref', ref || '');
    return this.http.post<void>(`${this.base}/movimientos/transferencia`, null, { params });
  }

  listarMovimientos(): Observable<any[]> {
    return this.http.get<any[]>(`${this.base}/movimientos`);
  }

  // --- Reportes ---
  reporteJSON(clienteId: number, desde: string, hasta: string): Observable<any> {
    const params = new HttpParams()
      .set('clienteId', String(clienteId))
      .set('desde', desde)
      .set('hasta', hasta);
    return this.http.get(`${this.base}/reportes/json`, { params });
  }

  reportePDF(clienteId: number, desde: string, hasta: string): Observable<Blob> {
    const params = new HttpParams()
      .set('clienteId', String(clienteId))
      .set('desde', desde)
      .set('hasta', hasta);
    return this.http.get(`${this.base}/reportes/pdf`, { params, responseType: 'blob' as 'json' }) as Observable<Blob>;
  }
}
