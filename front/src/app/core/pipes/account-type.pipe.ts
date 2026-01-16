import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'accountType',
  standalone: true
})
export class AccountTypePipe implements PipeTransform {
  private translations: Record<string, string> = {
    'SAVINGS': 'Ahorros',
    'CHECKING': 'Corriente'
  };

  transform(value: string): string {
    return this.translations[value] || value;
  }
}
