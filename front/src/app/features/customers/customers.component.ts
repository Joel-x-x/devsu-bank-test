import { Component, OnInit, signal, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CustomerService } from '@core/services/customer.service';
import { Customer, CustomerRequest } from '@core/models/customer.model';
import { PageResponse } from '@core/models/api-response.model';
import { TableComponent, TableColumn } from '@shared/components/table.component';
import { ModalComponent } from '@shared/components/modal.component';
import { NotificationService } from '@core/services/notification.service';

@Component({
  selector: 'app-customers',
  standalone: true,
  imports: [TableComponent, ModalComponent, ReactiveFormsModule],
  templateUrl: './customers.component.html',
  styleUrl: './customers.component.css'
})
export class CustomersComponent implements OnInit {
  private customerService = inject(CustomerService);
  private fb = inject(FormBuilder);
  private notificationService = inject(NotificationService);

  customersData = signal<PageResponse<Customer> | null>(null);
  isModalOpen = signal(false);
  isEditMode = signal(false);
  isLoading = signal(false);
  modalTitle = signal('Nuevo Cliente');
  selectedCustomerId = signal<string | null>(null);

  currentPage = 0;
  currentSearch = '';

  columns: TableColumn[] = [
    { key: 'customerCode', label: 'Código' },
    { key: 'name', label: 'Nombre' },
    { key: 'identification', label: 'Identificación' },
    { key: 'phone', label: 'Teléfono' },
    { key: 'status', label: 'Estado', type: 'boolean' }
  ];

  customerForm!: FormGroup;

  ngOnInit() {
    this.initForm();
    this.loadCustomers();
  }

  initForm() {
    this.customerForm = this.fb.group({
      name: ['', Validators.required],
      genre: ['', Validators.required],
      birthDate: ['', Validators.required],
      identification: ['', Validators.required],
      address: ['', Validators.required],
      phone: ['', Validators.required],
      password: ['', [Validators.required, Validators.minLength(4)]],
      status: [true]
    });
  }

  loadCustomers() {
    this.customerService
      .findAllPaginated(this.currentPage, 10, 'id', 'ASC', this.currentSearch || undefined)
      .subscribe({
        next: (response: any) => {
          this.customersData.set(response.result);
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
    this.modalTitle.set('Nuevo Cliente');
    this.selectedCustomerId.set(null);
    this.customerForm.reset({ status: true });
    this.customerForm.get('password')?.setValidators([Validators.required, Validators.minLength(4)]);
    this.isModalOpen.set(true);
  }

  openEditModal(customer: Customer) {
    this.isEditMode.set(true);
    this.modalTitle.set('Editar Cliente');
    this.selectedCustomerId.set(customer.id);
    this.customerForm.patchValue(customer);
    this.customerForm.get('password')?.clearValidators();
    this.customerForm.get('password')?.updateValueAndValidity();
    this.isModalOpen.set(true);
  }

  closeModal() {
    this.isModalOpen.set(false);
    this.customerForm.reset();
  }

  saveCustomer() {
    if (this.customerForm.invalid) return;

    this.isLoading.set(true);
    const formValue = this.customerForm.value;

    if (this.isEditMode()) {
      const updateData: Partial<CustomerRequest> = { ...formValue };
      delete updateData.password;

      this.customerService.update(this.selectedCustomerId()!, updateData).subscribe({
        next: () => {
          this.isLoading.set(false);
          this.closeModal();
          this.loadCustomers();
          this.notificationService.success('Cliente actualizado correctamente');
        },
        error: (err: any) => {
          console.error('Error updating customer:', err);
          const errorMsg = this.notificationService.translateError(err.error) || 'Error al actualizar cliente';
          this.notificationService.error(errorMsg);
          this.isLoading.set(false);
        }
      });
    } else {
      this.customerService.create(formValue).subscribe({
        next: () => {
          this.isLoading.set(false);
          this.closeModal();
          this.loadCustomers();
          this.notificationService.success('Cliente creado correctamente');
        },
        error: (err: any) => {
          console.error('Error creating customer:', err);
          const errorMsg = this.notificationService.translateError(err.error) || 'Error al crear cliente';
          this.notificationService.error(errorMsg);
          this.isLoading.set(false);
        }
      });
    }
  }

  deleteCustomer(customer: Customer) {
    if (!confirm(`¿Está seguro de eliminar el cliente ${customer.name}?`)) return;

    this.customerService.delete(customer.id).subscribe({
      next: () => {
        this.loadCustomers();
        this.notificationService.success('Cliente eliminado correctamente');
      },
      error: (err: any) => {
        console.error('Error deleting customer:', err);
        const errorMsg = this.notificationService.translateError(err.error) || 'Error al eliminar cliente';
        this.notificationService.error(errorMsg);
      }
    });
  }

  onSearch(search: string) {
    this.currentSearch = search;
    this.currentPage = 0;
    this.loadCustomers();
  }

  onPageChange(page: number) {
    this.currentPage = page;
    this.loadCustomers();
  }
}
