import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from 'src/app/core/services/api.service';

@Component({
  standalone: true,
  selector: 'app-movimientos',
  imports: [CommonModule, FormsModule],
  templateUrl: './movimientos.component.html',
  styleUrls: ['./movimientos.component.css']
})
export class MovimientosComponent implements OnInit {
  constructor(public api: ApiService) {}
  cuentaId: number | null = null;
  monto = 0;
  ref = '';
  transfer = {
    origenId: null as number | null,
    destinoId: null as number | null,
    monto: 0,
    ref: '',
  };
  err = '';
  rows: any[] = [];

  ngOnInit() {
    this.load();
  }
  load() {
    this.api.listarMovimientos().subscribe((r) => (this.rows = r));
  }

  depositar() {
    if (!this.cuentaId || this.monto <= 0) return;
    this.api.depositar(this.cuentaId, this.monto, this.ref).subscribe({
      next: () => {
        this.monto = 0;
        this.ref = '';
        this.load();
      },
      error: (e) => (this.err = e?.error?.message || e?.message || 'Error'),
    });
  }

  retirar() {
    if (!this.cuentaId || this.monto <= 0) return;
    this.api.retirar(this.cuentaId, this.monto, this.ref).subscribe({
      next: () => {
        this.monto = 0;
        this.ref = '';
        this.load();
      },
      error: (e) => (this.err = e?.error?.message || e?.message || 'Error'),
    });
  }

  transferir() {
    const t = this.transfer;
    if (!t.origenId || !t.destinoId || t.monto <= 0) return;
    this.api.transferir(t.origenId, t.destinoId, t.monto, t.ref).subscribe({
      next: () => {
        this.transfer = { origenId: null, destinoId: null, monto: 0, ref: '' };
        this.load();
      },
      error: (e) => (this.err = e?.error?.message || e?.message || 'Error'),
    });
  }
}
