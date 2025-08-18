import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from 'src/app/core/services/api.service';

@Component({
  standalone: true,
  selector: 'app-cuentas',
  imports: [CommonModule, FormsModule],
  templateUrl: './cuentas.component.html',
  styleUrls: ['./cuentas.component.css']
})
export class CuentasComponent implements OnInit {
  constructor(public api: ApiService) {}
  clientes: any[] = [];
  rows: any[] = [];
  editId: number | null = null;
  err = '';

  form: any = {
    numero: '',
    tipo: 'Ahorro',
    saldoInicial: 0,
    estado: true,
    clienteId: 0,
  };

  ngOnInit() {
    this.api.listarClientes().subscribe((c) => (this.clientes = c));
    this.load();
  }

  load() {
    this.api.listarCuentas().subscribe((c) => (this.rows = c));
  }

  submit() {
    const payload = { ...this.form };
    const call = this.editId
      ? this.api.actualizarCuenta(this.editId, payload)
      : this.api.crearCuenta(payload);
    call.subscribe({
      next: () => {
        this.reset();
        this.load();
      },
      error: (e) => (this.err = e?.error?.message || e?.message || 'Error'),
    });
  }

  edit(row: any) {
    this.editId = row.id;
    this.form = {
      numero: row.numero,
      tipo: row.tipo,
      saldoInicial: row.saldoInicial ?? row.saldo ?? 0,
      estado: row.estado ?? true,
      clienteId: row.clienteId,
    };
  }

  del(id: number) {
    if (!confirm('¿Eliminar cuenta?')) return;
    this.api.eliminarCuenta(id).subscribe(() => this.load());
  }

  reset() {
    this.editId = null;
    this.form = { numero: '', tipo: 'Ahorro', saldoInicial: 0, estado: true, clienteId: 0 };
  }
}
