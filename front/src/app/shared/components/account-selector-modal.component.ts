import { Component, OnInit, signal, inject, output, input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AccountService } from '@core/services/account.service';
import { Account } from '@core/models/account.model';

@Component({
  selector: 'app-account-selector-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="modal-overlay" (click)="close.emit()">
      <div class="modal-container" (click)="$event.stopPropagation()">
        <div class="modal-header">
          <h2>Seleccionar Cuenta</h2>
          <button class="close-btn" (click)="close.emit()">&times;</button>
        </div>

        <div class="modal-body">
          <div class="search-box">
            <input
              type="text"
              [(ngModel)]="searchTerm"
              (input)="onSearch()"
              placeholder="Buscar por número de cuenta..."
              class="search-input"
            >
          </div>

          @if (isLoading()) {
            <div class="loading">Cargando cuentas...</div>
          } @else {
            <div class="accounts-list">
              @for (account of filteredAccounts(); track account.id) {
                <div
                  class="account-item"
                  [class.selected]="selectedAccount()?.id === account.id"
                  (click)="selectAccount(account)"
                >
                  <div class="account-info">
                    <strong>{{ account.accountNumber }}</strong>
                    <span class="account-type">
                      {{ account.accountType === 'SAVINGS' ? 'Ahorros' : 'Corriente' }}
                    </span>
                  </div>
                  <div class="account-details">
                    <span class="customer-name">{{ account.customerName }}</span>
                    <span class="balance">Saldo: {{ account.initialBalance | number:'1.2-2' }}</span>
                  </div>
                </div>
              } @empty {
                <div class="no-results">No se encontraron cuentas</div>
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
            [disabled]="!selectedAccount()"
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

    .accounts-list {
      display: flex;
      flex-direction: column;
      gap: 8px;
    }

    .account-item {
      padding: 12px;
      border: 2px solid #e5e7eb;
      border-radius: 6px;
      cursor: pointer;
      transition: all 0.2s;
    }

    .account-item:hover {
      border-color: #3b82f6;
      background: #f0f9ff;
    }

    .account-item.selected {
      border-color: #3b82f6;
      background: #dbeafe;
    }

    .account-info {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 6px;
    }

    .account-info strong {
      font-size: 14px;
      color: #111827;
    }

    .account-type {
      font-size: 12px;
      color: #059669;
      background: #d1fae5;
      padding: 2px 8px;
      border-radius: 12px;
      font-weight: 500;
    }

    .account-details {
      display: flex;
      justify-content: space-between;
      font-size: 12px;
      color: #6b7280;
    }

    .customer-name {
      font-weight: 500;
      color: #374151;
    }

    .balance {
      font-weight: 600;
      color: #059669;
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
export class AccountSelectorModalComponent implements OnInit {
  private accountService = inject(AccountService);

  customerId = input<string>();

  accounts = signal<Account[]>([]);
  filteredAccounts = signal<Account[]>([]);
  selectedAccount = signal<Account | null>(null);
  isLoading = signal(false);

  searchTerm = '';

  close = output<void>();
  select = output<Account>();

  ngOnInit() {
    this.loadAccounts();
  }

  loadAccounts() {
    this.isLoading.set(true);
    const custId = this.customerId();

    if (custId) {
      // Cargar solo cuentas del cliente seleccionado
      this.accountService.findByCustomerId(custId).subscribe({
        next: (response) => {
          if (response.success && response.result) {
            this.accounts.set(response.result);
            this.filteredAccounts.set(response.result);
          }
          this.isLoading.set(false);
        },
        error: (err) => {
          console.error('Error loading accounts:', err);
          this.isLoading.set(false);
        }
      });
    } else {
      // Cargar todas las cuentas
      this.accountService.findAllPaginated(0, 1000).subscribe({
        next: (response) => {
          if (response.success && response.result) {
            this.accounts.set(response.result.content);
            this.filteredAccounts.set(response.result.content);
          }
          this.isLoading.set(false);
        },
        error: (err) => {
          console.error('Error loading accounts:', err);
          this.isLoading.set(false);
        }
      });
    }
  }

  onSearch() {
    const term = this.searchTerm.toLowerCase().trim();
    if (!term) {
      this.filteredAccounts.set(this.accounts());
      return;
    }

    const filtered = this.accounts().filter(a =>
      a.accountNumber.toLowerCase().includes(term) ||
      a.customerName?.toLowerCase().includes(term)
    );
    this.filteredAccounts.set(filtered);
  }

  selectAccount(account: Account) {
    this.selectedAccount.set(account);
  }

  confirmSelection() {
    const account = this.selectedAccount();
    if (account) {
      this.select.emit(account);
    }
  }
}
