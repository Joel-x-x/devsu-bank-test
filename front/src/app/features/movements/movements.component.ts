import { Component, OnInit, signal, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MovementService } from '@core/services/movement.service';
import { AccountService } from '@core/services/account.service';
import { NotificationService } from '@core/services/notification.service';
import { Movement, MovementRequest } from '@core/models/movement.model';
import { Account } from '@core/models/account.model';
import { PageResponse } from '@core/models/api-response.model';
import { TableComponent, TableColumn } from '@shared/components/table.component';
import { ModalComponent } from '@shared/components/modal.component';
import { AccountSelectorModalComponent } from '@shared/components/account-selector-modal.component';

@Component({
  selector: 'app-movements',
  standalone: true,
  imports: [TableComponent, ModalComponent, ReactiveFormsModule, AccountSelectorModalComponent],
  templateUrl: './movements.component.html',
  styleUrl: './movements.component.css'
})
export class MovementsComponent implements OnInit {
  private movementService = inject(MovementService);
  private accountService = inject(AccountService);
  private notificationService = inject(NotificationService);
  private fb = inject(FormBuilder);

  movementsData = signal<PageResponse<Movement> | null>(null);
  accounts = signal<Account[]>([]);
  isModalOpen = signal(false);
  isAccountSelectorOpen = signal(false);
  isLoading = signal(false);
  modalTitle = signal('Nuevo Movimiento');
  selectedAccount = signal<Account | null>(null);

  currentPage = 0;
  currentSearch = '';

  columns: TableColumn[] = [
    { key: 'movementDate', label: 'Fecha', type: 'date' },
    { key: 'accountNumber', label: 'Cuenta' },
    { key: 'movementType', label: 'Tipo', type: 'movementType' },
    { key: 'amount', label: 'Monto', type: 'number' },
    { key: 'balance', label: 'Saldo', type: 'number' }
  ];

  movementForm!: FormGroup;

  ngOnInit() {
    this.initForm();
    this.loadMovements();
    this.loadAccounts();
  }

  initForm() {
    this.movementForm = this.fb.group({
      accountId: ['', Validators.required],
      movementType: ['', Validators.required],
      amount: [0, [Validators.required, Validators.min(0.01)]]
    });
  }

  loadMovements() {
    this.movementService
      .findAllPaginated(this.currentPage, 10, 'movementDate', 'DESC', this.currentSearch || undefined)
      .subscribe({
        next: (response: any) => {
          this.movementsData.set(response.result);
        },
        error: (err: any) => {
          console.error('Error loading movements:', err);
          const errorMsg = this.notificationService.translateError(err.error) || 'Error al cargar movimientos';
          this.notificationService.error(errorMsg);
        }
      });
  }

  loadAccounts() {
    this.accountService
      .findAllPaginated(0, 100, 'accountNumber', 'ASC')
      .subscribe({
        next: (response: any) => {
          this.accounts.set(response.result.content);
        },
        error: (err: any) => {
          console.error('Error loading accounts:', err);
          const errorMsg = this.notificationService.translateError(err.error) || 'Error al cargar cuentas';
          this.notificationService.error(errorMsg);
        }
      });
  }

  openCreateModal() {
    this.modalTitle.set('Nuevo Movimiento');
    this.selectedAccount.set(null);
    this.movementForm.reset({ amount: 0 });
    this.isModalOpen.set(true);
  }

  openAccountSelector() {
    this.isAccountSelectorOpen.set(true);
  }

  onAccountSelected(account: Account) {
    this.selectedAccount.set(account);
    this.movementForm.patchValue({ accountId: account.id });
    this.isAccountSelectorOpen.set(false);
  }

  closeAccountSelector() {
    this.isAccountSelectorOpen.set(false);
  }

  closeModal() {
    this.isModalOpen.set(false);
    this.movementForm.reset();
  }

  saveMovement() {
    if (this.movementForm.invalid) return;

    this.isLoading.set(true);
    const formValue = this.movementForm.value;

    this.movementService.create(formValue).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.closeModal();
        this.loadMovements();
        this.notificationService.success('Movimiento creado correctamente');
      },
      error: (err: any) => {
        console.error('Error creating movement:', err);
        const errorMsg = this.notificationService.translateError(err.error) || 'Error al crear movimiento';
        this.notificationService.error(errorMsg);
        this.isLoading.set(false);
      }
    });
  }

  onEdit(movement: Movement) {
    // Movements no se editan, solo se consultan
  }

  onDelete(movement: Movement) {
    // Movements no se eliminan, solo se consultan
  }

  onSearch(search: string) {
    this.currentSearch = search;
    this.currentPage = 0;
    this.loadMovements();
  }

  onPageChange(page: number) {
    this.currentPage = page;
    this.loadMovements();
  }
}

