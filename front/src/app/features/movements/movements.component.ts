import { Component } from '@angular/core';

@Component({
  selector: 'app-movements',
  standalone: true,
  template: `
    <div class="page">
      <div class="page-header">
        <h1>Movimientos</h1>
      </div>
      <div class="page-content">
        <p>Módulo de movimientos en construcción...</p>
      </div>
    </div>
  `,
  styles: [`
    .page {
      padding: 24px;
    }
    .page-header h1 {
      margin: 0;
      font-size: 24px;
      font-weight: 600;
      color: #333;
    }
    .page-content {
      margin-top: 24px;
    }
  `]
})
export class MovementsComponent {}
