import { Component, OnInit, signal, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { environment } from '../../../environments/environment';
import { MovementService } from '@core/services/movement.service';
import { CustomerService } from '@core/services/customer.service';
import { AccountService } from '@core/services/account.service';
import { NotificationService } from '@core/services/notification.service';
import { Movement } from '@core/models/movement.model';
import { Customer } from '@core/models/customer.model';
import { Account } from '@core/models/account.model';
import { PageResponse } from '@core/models/api-response.model';
import { AccountTypePipe } from '@core/pipes/account-type.pipe';
import { MovementTypePipe } from '@core/pipes/movement-type.pipe';

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, AccountTypePipe, MovementTypePipe],
  templateUrl: './reports.component.html',
  styleUrl: './reports.component.css'
})
export class ReportsComponent implements OnInit {
  private movementService = inject(MovementService);
  private customerService = inject(CustomerService);
  private accountService = inject(AccountService);
  private notificationService = inject(NotificationService);
  private fb = inject(FormBuilder);

  customers = signal<Customer[]>([]);
  accounts = signal<Account[]>([]);
  movements = signal<Movement[]>([]);
  isLoading = signal(false);
  selectedCustomer = signal<Customer | null>(null);

  filterForm!: FormGroup;
  Math = Math;

  ngOnInit() {
    this.initForm();
    this.loadCustomers();
  }

  initForm() {
    const today = new Date().toISOString().split('T')[0];
    const firstDayOfMonth = new Date(new Date().getFullYear(), new Date().getMonth(), 1)
      .toISOString().split('T')[0];

    this.filterForm = this.fb.group({
      customerId: ['', Validators.required],
      startDate: [firstDayOfMonth, Validators.required],
      endDate: [today, Validators.required]
    });
  }

  loadCustomers() {
    this.customerService.findAllPaginated(0, 1000).subscribe({
      next: (response) => {
        if (response.success && response.result) {
          this.customers.set(response.result.content);
        }
      },
      error: (err) => {
        console.error('Error loading customers:', err);
        const errorMsg = this.notificationService.translateError(err.error) || 'Error al cargar clientes';
        this.notificationService.error(errorMsg);
      }
    });
  }

  generateReport() {
    if (this.filterForm.invalid) return;

    const { customerId, startDate, endDate } = this.filterForm.value;

    // Buscar el cliente seleccionado
    const customer = this.customers().find(c => c.id === customerId);
    this.selectedCustomer.set(customer || null);

    this.isLoading.set(true);

    // Primero cargar las cuentas del cliente
    this.accountService.findByCustomerId(customerId).subscribe({
      next: (accountsResponse) => {
        if (accountsResponse.success && accountsResponse.result) {
          this.accounts.set(accountsResponse.result);

          // Luego cargar los movimientos
          this.movementService.findByCustomerId(customerId, startDate, endDate).subscribe({
            next: (movementsResponse) => {
              if (movementsResponse.success && movementsResponse.result) {
                // Ordenar movimientos por cuenta y fecha
                const sortedMovements = movementsResponse.result.sort((a, b) => {
                  // Primero ordenar por número de cuenta (usar el campo accountNumber del movimiento)
                  const accountA = a.accountNumber || this.getAccountNumber(a.accountId);
                  const accountB = b.accountNumber || this.getAccountNumber(b.accountId);
                  if (accountA !== accountB) {
                    return accountA.localeCompare(accountB);
                  }
                  // Luego por fecha descendente
                  return new Date(b.movementDate).getTime() - new Date(a.movementDate).getTime();
                });

                this.movements.set(sortedMovements);
                this.isLoading.set(false);
                this.notificationService.success('Reporte generado correctamente');
              } else {
                this.isLoading.set(false);
                this.notificationService.info('No se encontraron movimientos');
              }
            },
            error: (err) => {
              console.error('Error loading movements:', err);
              const errorMsg = this.notificationService.translateError(err.error) || 'Error al generar reporte';
              this.notificationService.error(errorMsg);
              this.isLoading.set(false);
            }
          });
        } else {
          this.isLoading.set(false);
          this.notificationService.info('El cliente no tiene cuentas registradas');
        }
      },
      error: (err) => {
        console.error('Error loading accounts:', err);
        const errorMsg = this.notificationService.translateError(err.error) || 'Error al cargar cuentas';
        this.notificationService.error(errorMsg);
        this.isLoading.set(false);
      }
    });
  }

  getInitialBalance(): number {
    const movements = this.movements();
    if (movements.length === 0) return 0;

    // Agrupar por cuenta y calcular saldo inicial de cada una
    const accountBalances = new Map<string, number>();

    this.accounts().forEach(account => {
      const accountMovements = movements.filter(m => m.accountId === account.id);
      if (accountMovements.length > 0) {
        const firstMovement = accountMovements[0];
        const initialBalance = firstMovement.balance - firstMovement.amount;
        accountBalances.set(account.id, initialBalance);
      }
    });

    // Sumar todos los saldos iniciales
    return Array.from(accountBalances.values()).reduce((sum, balance) => sum + balance, 0);
  }

  getFinalBalance(): number {
    const movements = this.movements();
    if (movements.length === 0) return 0;

    // Agrupar por cuenta y obtener el último saldo de cada una
    const accountBalances = new Map<string, number>();

    this.accounts().forEach(account => {
      const accountMovements = movements.filter(m => m.accountId === account.id);
      if (accountMovements.length > 0) {
        const lastMovement = accountMovements[accountMovements.length - 1];
        accountBalances.set(account.id, lastMovement.balance);
      } else {
        // Si no hay movimientos, usar el saldo inicial de la cuenta
        accountBalances.set(account.id, account.initialBalance);
      }
    });

    // Sumar todos los saldos finales
    return Array.from(accountBalances.values()).reduce((sum, balance) => sum + balance, 0);
  }

  getTotalCredits(): number {
    return this.movements()
      .filter(m => m.movementType === 'CREDIT')
      .reduce((sum, m) => sum + m.amount, 0);
  }

  getTotalDebits(): number {
    return this.movements()
      .filter(m => m.movementType === 'DEBIT')
      .reduce((sum, m) => sum + Math.abs(m.amount), 0);
  }

  getAccountNumber(accountId: string, movement?: Movement): string {
    // Si el movimiento ya tiene el número de cuenta, usarlo directamente
    if (movement?.accountNumber) {
      return movement.accountNumber;
    }
    // Fallback: buscar en el array de cuentas
    const account = this.accounts().find(a => a.id === accountId);
    return account ? account.accountNumber : 'Cuenta no encontrada';
  }

  getAccountInfo(accountId: string): string {
    const account = this.accounts().find(a => a.id === accountId);
    if (!account) return 'Cuenta no encontrada';
    return `${account.accountNumber} (${account.accountType === 'SAVINGS' ? 'Ahorros' : 'Corriente'})`;
  }

  getAccountType(accountId: string): string {
    const account = this.accounts().find(a => a.id === accountId);
    if (!account) return '-';
    return account.accountType === 'SAVINGS' ? 'Ahorros' : 'Corriente';
  }

  exportToCSV() {
    const customer = this.selectedCustomer();
    if (!customer) return;

    const { startDate, endDate, customerId } = this.filterForm.value;

    // Llamar al endpoint de PDF del backend
    window.open(
      `${environment.apiUrl}/reports/account-statement/pdf?customerId=${customerId}&startDate=${startDate}&endDate=${endDate}`,
      '_blank'
    );
  }

  clearReport() {
    this.movements.set([]);
    this.accounts.set([]);
    this.selectedCustomer.set(null);
  }
}
