import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from 'src/app/core/services/api.service';

@Component({
  standalone: true,
  selector: 'app-reportes',
  imports: [CommonModule, FormsModule],
  templateUrl: './reportes.component.html',
  styleUrls: ['./reportes.component.css']
})
export class ReportesComponent implements OnInit {
  constructor(public api: ApiService) {}

  clientes: any[] = [];
  clienteId: number | null = null;
  desde = '';
  hasta = '';
  data: any = null;
  err = '';

  ngOnInit() {
    this.api.listarClientes().subscribe((c) => (this.clientes = c));
    const today = new Date().toISOString().slice(0, 10);
    this.desde = today;
    this.hasta = today;
  }

  ver() {
    if (!this.clienteId) return;
    this.api.reporteJSON(this.clienteId!, this.desde, this.hasta).subscribe({
      next: (r) => {
        this.data = r;
        this.err = '';
      },
      error: (e) => (this.err = e?.error?.message || e?.message || 'Error'),
    });
  }

  descargar() {
    if (!this.clienteId) return;
    this.api.reportePDF(this.clienteId!, this.desde, this.hasta).subscribe((blob) => {
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `reporte-${this.clienteId}-${this.desde}_a_${this.hasta}.pdf`;
      document.body.appendChild(a);
      a.click();
      a.remove();
      URL.revokeObjectURL(url);
    });
  }
}
