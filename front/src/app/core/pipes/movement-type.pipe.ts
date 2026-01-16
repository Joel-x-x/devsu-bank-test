import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'movementType',
  standalone: true
})
export class MovementTypePipe implements PipeTransform {
  private translations: Record<string, string> = {
    'CREDIT': 'Crédito',
    'DEBIT': 'Débito'
  };

  transform(value: string): string {
    return this.translations[value] || value;
  }
}
