import { Component, OnInit, signal, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AccountService } from '@core/services/account.service';
import { CustomerService } from '@core/services/customer.service';
import { Account, AccountRequest } from '@core/models/account.model';
import { Customer } from '@core/models/customer.model';
import { PageResponse } from '@core/models/api-response.model';
import { TableComponent, TableColumn } from '@shared/components/table.component';
import { ModalComponent } from '@shared/components/modal.component';
import { CustomerSelectorModalComponent } from '@shared/components/customer-selector-modal.component';
import { NotificationService } from '@core/services/notification.service';

@Component({
  selector: 'app-accounts',
  standalone: true,
  imports: [TableComponent, ModalComponent, ReactiveFormsModule, CustomerSelectorModalComponent],
  templateUrl: './accounts.component.html',
  styleUrl: './accounts.component.css'
})
export class AccountsComponent implements OnInit {
  private accountService = inject(AccountService);
  private customerService = inject(CustomerService);
  private fb = inject(FormBuilder);
  private notificationService = inject(NotificationService);

  accountsData = signal<PageResponse<Account> | null>(null);
  customers = signal<Customer[]>([]);
  isModalOpen = signal(false);
  isCustomerSelectorOpen = signal(false);
  isEditMode = signal(false);
  isLoading = signal(false);
  modalTitle = signal('Nueva Cuenta');
  selectedAccountId = signal<string | null>(null);
  selectedCustomer = signal<Customer | null>(null);

  currentPage = 0;
  currentSearch = '';

  columns: TableColumn[] = [
    { key: 'accountNumber', label: 'Número de Cuenta' },
    { key: 'customerName', label: 'Cliente' },
    { key: 'accountType', label: 'Tipo', type: 'accountType' },
    { key: 'initialBalance', label: 'Saldo', type: 'number' },
    { key: 'status', label: 'Estado', type: 'boolean' }
  ];

  accountForm!: FormGroup;

  ngOnInit() {
    this.initForm();
    this.loadAccounts();
    this.loadCustomers();
  }

  initForm() {
    this.accountForm = this.fb.group({
      customerId: ['', Validators.required],
      accountType: ['', Validators.required],
      initialBalance: [0, [Validators.required, Validators.min(0)]],
      dailyLimit: [1000, [Validators.required, Validators.min(1)]],
      status: [true]
    });
  }

  loadAccounts() {
    this.accountService
      .findAllPaginated(this.currentPage, 10, 'id', 'ASC', this.currentSearch || undefined)
      .subscribe({
        next: (response: any) => {
          this.accountsData.set(response.result);
        },
        error: (err: any) => {
          console.error('Error loading accounts:', err);
          const errorMsg = this.notificationService.translateError(err.error) || 'Error al cargar cuentas';
          this.notificationService.error(errorMsg);
        }
      });
  }

  loadCustomers() {
    this.customerService
      .findAllPaginated(0, 100, 'name', 'ASC')
      .subscribe({
        next: (response: any) => {
          this.customers.set(response.result.content);
        },
        error: (err: any) => {
          console.error('Error loading customers:', err);
          const errorMsg = this.notificationService.translateError(err.error) || 'Error al cargar clientes';
          this.notificationService.error(errorMsg);
        }
      });
  }

  openCreateModal() {
    this.isEditMode.set(false);
    this.modalTitle.set('Nueva Cuenta');
    this.selectedAccountId.set(null);
    this.selectedCustomer.set(null);
    this.accountForm.reset({ initialBalance: 0, dailyLimit: 1000, status: true });
    this.isModalOpen.set(true);
  }

  openEditModal(account: Account) {
    this.isEditMode.set(true);
    this.modalTitle.set('Editar Cuenta');
    this.selectedAccountId.set(account.id);

    // Buscar el customer seleccionado
    const customer = this.customers().find(c => c.id === account.customerId);
    this.selectedCustomer.set(customer || null);

    this.accountForm.patchValue(account);
    this.isModalOpen.set(true);
  }

  openCustomerSelector() {
    this.isCustomerSelectorOpen.set(true);
  }

  onCustomerSelected(customer: Customer) {
    this.selectedCustomer.set(customer);
    this.accountForm.patchValue({ customerId: customer.id });
    this.isCustomerSelectorOpen.set(false);
  }

  closeCustomerSelector() {
    this.isCustomerSelectorOpen.set(false);
  }

  closeModal() {
    this.isModalOpen.set(false);
    this.accountForm.reset();
  }

  saveAccount() {
    if (this.accountForm.invalid) return;

    this.isLoading.set(true);
    const formValue = this.accountForm.value;

    if (this.isEditMode()) {
      this.accountService.update(this.selectedAccountId()!, formValue).subscribe({
        next: () => {
          this.isLoading.set(false);
          this.closeModal();
          this.loadAccounts();
          this.notificationService.success('Cuenta actualizada correctamente');
        },
        error: (err: any) => {
          console.error('Error updating account:', err);
          const errorMsg = this.notificationService.translateError(err.error) || 'Error al actualizar cuenta';
          this.notificationService.error(errorMsg);
          this.isLoading.set(false);
        }
      });
    } else {
      this.accountService.create(formValue).subscribe({
        next: () => {
          this.isLoading.set(false);
          this.closeModal();
          this.loadAccounts();
          this.notificationService.success('Cuenta creada correctamente');
        },
        error: (err: any) => {
          console.error('Error creating account:', err);
          const errorMsg = this.notificationService.translateError(err.error) || 'Error al crear cuenta';
          this.notificationService.error(errorMsg);
          this.isLoading.set(false);
        }
      });
    }
  }

  deleteAccount(account: Account) {
    if (!confirm(`¿Está seguro de eliminar la cuenta ${account.accountNumber}?`)) return;

    this.accountService.delete(account.id).subscribe({
      next: () => {
        this.loadAccounts();
        this.notificationService.success('Cuenta eliminada correctamente');
      },
      error: (err: any) => {
        console.error('Error deleting account:', err);
        const errorMsg = this.notificationService.translateError(err.error) || 'Error al eliminar cuenta';
        this.notificationService.error(errorMsg);
      }
    });
  }

  onSearch(search: string) {
    this.currentSearch = search;
    this.currentPage = 0;
    this.loadAccounts();
  }

  onPageChange(page: number) {
    this.currentPage = page;
    this.loadAccounts();
  }
}
