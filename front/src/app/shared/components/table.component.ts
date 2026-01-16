import { Component, input, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { PageResponse } from '../../core/models/api-response.model';

export interface TableColumn {
  key: string;
  label: string;
  type?: 'text' | 'number' | 'boolean' | 'date' | 'accountType' | 'movementType';
}

@Component({
  selector: 'app-table',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './table.component.html',
  styleUrl: './table.component.css'
})
export class TableComponent<T> {
  columns = input.required<TableColumn[]>();
  data = input<PageResponse<T> | null>(null);
  showActions = input<boolean>(true);

  create = output<void>();
  edit = output<T>();
  delete = output<T>();
  search = output<string>();
  pageChange = output<number>();

  searchTerm = '';
  Math = Math;

  private accountTypeTranslations: Record<string, string> = {
    'SAVINGS': 'Ahorros',
    'CHECKING': 'Corriente'
  };

  private movementTypeTranslations: Record<string, string> = {
    'CREDIT': 'Crédito',
    'DEBIT': 'Débito'
  };

  onCreate() {
    this.create.emit();
  }

  onEdit(item: T) {
    this.edit.emit(item);
  }

  onDelete(item: T) {
    this.delete.emit(item);
  }

  onSearch() {
    this.search.emit(this.searchTerm);
  }

  onPageChange(page: number) {
    this.pageChange.emit(page);
  }

  getColumnValue(row: T, key: string, type?: string): string {
    const value = (row as Record<string, unknown>)[key];
    return this.formatValue(value, type);
  }

  formatValue(value: unknown, type?: string): string {
    if (value === null || value === undefined) return '-';

    switch (type) {
      case 'boolean':
        return value ? 'Activo' : 'Inactivo';
      case 'date':
        return new Date(value as string | number | Date).toLocaleDateString();
      case 'number':
        return typeof value === 'number' ? value.toLocaleString() : String(value);
      case 'accountType':
        return this.accountTypeTranslations[String(value)] || String(value);
      case 'movementType':
        return this.movementTypeTranslations[String(value)] || String(value);
      default:
        return String(value);
    }
  }
}
