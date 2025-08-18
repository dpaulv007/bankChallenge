import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from 'src/app/core/services/api.service';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule],
  selector: 'app-clientes',
  templateUrl: './clientes.component.html',
  styleUrls: ['./clientes.component.css'],
})
export class ClientesComponent implements OnInit {
  rows: any[] = [];
  err: string = '';
  form: any = {
    nombre: '',
    genero: 'Masculino',
    edad: 18,
    identificacion: '',
    direccion: '',
    telefono: '',
    contrasena: '',
    estado: true,
  };
  editId: number | null = null;

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.api.listarClientes().subscribe({
      next: (data) => {
        this.rows = data;
        this.err = '';
      },
      error: (error) => {
        this.err = error.error?.message || 'Error al cargar clientes';
      },
    });
  }

  submit(): void {
    if (this.editId === null) {
      this.api.crearCliente(this.form).subscribe({
        next: () => {
          this.reset();
          this.load();
          this.err = '';
        },
        error: (error) => (this.err = error.error?.message || 'Error al crear'),
      });
    } else {
      this.api.actualizarCliente(this.editId, this.form).subscribe({
        next: () => {
          this.reset();
          this.load();
          this.err = '';
        },
        error: (error) => (this.err = error.error?.message || 'Error al actualizar'),
      });
    }
  }

  reset(): void {
    this.editId = null;
    this.form = {
      nombre: '',
      genero: 'Masculino',
      edad: 18,
      identificacion: '',
      direccion: '',
      telefono: '',
      contrasena: '',
      estado: true,
    };
  }

  edit(row: any): void {
    this.editId = row.id;
    this.form = {
      nombre: row.nombre,
      genero: row.genero,
      edad: row.edad,
      identificacion: row.identificacion,
      direccion: row.direccion,
      telefono: row.telefono,
      contrasena: row.contrasena,
      estado: row.estado,
    };
  }

  del(id: number): void {
    if (window.confirm('¿Está seguro de eliminar?')) {
      this.api.eliminarCliente(id).subscribe({
        next: () => this.load(),
        error: (error) => (this.err = error.error?.message || 'Error al eliminar'),
      });
    }
  }
}
