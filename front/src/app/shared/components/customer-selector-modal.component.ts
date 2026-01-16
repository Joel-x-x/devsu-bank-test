import { Component, OnInit, signal, inject, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CustomerService } from '@core/services/customer.service';
import { Customer } from '@core/models/customer.model';

@Component({
  selector: 'app-customer-selector-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="modal-overlay" (click)="close.emit()">
      <div class="modal-container" (click)="$event.stopPropagation()">
        <div class="modal-header">
          <h2>Seleccionar Cliente</h2>
          <button class="close-btn" (click)="close.emit()">&times;</button>
        </div>

        <div class="modal-body">
          <div class="search-box">
            <input
              type="text"
              [(ngModel)]="searchTerm"
              (input)="onSearch()"
              placeholder="Buscar por nombre o identificación..."
              class="search-input"
            >
          </div>

          @if (isLoading()) {
            <div class="loading">Cargando clientes...</div>
          } @else {
            <div class="customers-list">
              @for (customer of filteredCustomers(); track customer.id) {
                <div
                  class="customer-item"
                  [class.selected]="selectedCustomer()?.id === customer.id"
                  (click)="selectCustomer(customer)"
                >
                  <div class="customer-info">
                    <strong>{{ customer.name }}</strong>
                    <span class="customer-id">{{ customer.identification }}</span>
                  </div>
                  <div class="customer-details">
                    <span>{{ customer.phone }}</span>
                  </div>
                </div>
              } @empty {
                <div class="no-results">No se encontraron clientes</div>
              }
            </div>
          }
        </div>

        <div class="modal-footer">
          <button type="button" class="btn-cancel" (click)="close.emit()">
            Cancelar
          </button>
          <button
            type="button"
            class="btn-confirm"
            [disabled]="!selectedCustomer()"
            (click)="confirmSelection()"
          >
            Seleccionar
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .modal-overlay {
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: rgba(0, 0, 0, 0.5);
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 1000;
    }

    .modal-container {
      background: white;
      border-radius: 8px;
      width: 90%;
      max-width: 600px;
      max-height: 80vh;
      display: flex;
      flex-direction: column;
    }

    .modal-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20px;
      border-bottom: 1px solid #e5e7eb;
    }

    .modal-header h2 {
      margin: 0;
      font-size: 20px;
      font-weight: 600;
    }

    .close-btn {
      background: none;
      border: none;
      font-size: 28px;
      cursor: pointer;
      color: #6b7280;
      padding: 0;
      width: 32px;
      height: 32px;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .close-btn:hover {
      color: #374151;
    }

    .modal-body {
      padding: 20px;
      flex: 1;
      overflow-y: auto;
    }

    .search-box {
      margin-bottom: 16px;
    }

    .search-input {
      width: 100%;
      padding: 10px 12px;
      border: 1px solid #d1d5db;
      border-radius: 4px;
      font-size: 14px;
    }

    .search-input:focus {
      outline: none;
      border-color: #3b82f6;
    }

    .loading, .no-results {
      text-align: center;
      padding: 40px 20px;
      color: #6b7280;
    }

    .customers-list {
      display: flex;
      flex-direction: column;
      gap: 8px;
    }

    .customer-item {
      padding: 12px;
      border: 2px solid #e5e7eb;
      border-radius: 6px;
      cursor: pointer;
      transition: all 0.2s;
    }

    .customer-item:hover {
      border-color: #3b82f6;
      background: #f0f9ff;
    }

    .customer-item.selected {
      border-color: #3b82f6;
      background: #dbeafe;
    }

    .customer-info {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 4px;
    }

    .customer-info strong {
      font-size: 14px;
      color: #111827;
    }

    .customer-id {
      font-size: 12px;
      color: #6b7280;
      background: #f3f4f6;
      padding: 2px 8px;
      border-radius: 12px;
    }

    .customer-details {
      font-size: 12px;
      color: #6b7280;
    }

    .modal-footer {
      display: flex;
      gap: 12px;
      justify-content: flex-end;
      padding: 20px;
      border-top: 1px solid #e5e7eb;
    }

    .btn-cancel, .btn-confirm {
      padding: 10px 20px;
      border: none;
      border-radius: 4px;
      font-size: 14px;
      font-weight: 500;
      cursor: pointer;
      transition: all 0.2s;
    }

    .btn-cancel {
      background: #f3f4f6;
      color: #374151;
    }

    .btn-cancel:hover {
      background: #e5e7eb;
    }

    .btn-confirm {
      background: #3b82f6;
      color: white;
    }

    .btn-confirm:hover:not(:disabled) {
      background: #2563eb;
    }

    .btn-confirm:disabled {
      background: #9ca3af;
      cursor: not-allowed;
    }
  `]
})
export class CustomerSelectorModalComponent implements OnInit {
  private customerService = inject(CustomerService);

  customers = signal<Customer[]>([]);
  filteredCustomers = signal<Customer[]>([]);
  selectedCustomer = signal<Customer | null>(null);
  isLoading = signal(false);

  searchTerm = '';

  close = output<void>();
  select = output<Customer>();

  ngOnInit() {
    this.loadCustomers();
  }

  loadCustomers() {
    this.isLoading.set(true);
    this.customerService.findAllPaginated(0, 1000).subscribe({
      next: (response) => {
        if (response.success && response.result) {
          this.customers.set(response.result.content);
          this.filteredCustomers.set(response.result.content);
        }
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Error loading customers:', err);
        this.isLoading.set(false);
      }
    });
  }

  onSearch() {
    const term = this.searchTerm.toLowerCase().trim();
    if (!term) {
      this.filteredCustomers.set(this.customers());
      return;
    }

    const filtered = this.customers().filter(c =>
      c.name.toLowerCase().includes(term) ||
      c.identification.toLowerCase().includes(term) ||
      c.phone?.toLowerCase().includes(term)
    );
    this.filteredCustomers.set(filtered);
  }

  selectCustomer(customer: Customer) {
    this.selectedCustomer.set(customer);
  }

  confirmSelection() {
    const customer = this.selectedCustomer();
    if (customer) {
      this.select.emit(customer);
    }
  }
}
