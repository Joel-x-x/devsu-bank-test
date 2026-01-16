import { Injectable, signal } from '@angular/core';

export interface Notification {
  id: number;
  message: string;
  type: 'success' | 'error' | 'info' | 'warning';
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private notifications = signal<Notification[]>([]);
  private idCounter = 0;

  private errorMessages: Record<string, string> = {
    'ERR_001': 'El recurso solicitado no fue encontrado',
    'ERR_002': 'Ya existe un registro con estos datos',
    'ERR_003': 'Operación no válida',
    'ERR_004': 'Fondos insuficientes',
    'ERR_005': 'Límite diario excedido',
    'ERR_006': 'Cuenta inactiva',
    'ERR_007': 'Cliente inactivo',
    'ERR_008': 'Error de validación de datos',
    'ERR_009': 'No se puede eliminar el registro porque tiene dependencias',
    'ERR_010': 'Operación no permitida'
  };

  getNotifications() {
    return this.notifications.asReadonly();
  }

  translateError(errorResponse: any): string {
    const messageCode = errorResponse?.messageCode;
    const errors = errorResponse?.errors;
    const message = errorResponse?.message;
    const errorDetail = errors && errors.length > 0 ? errors[0] : '';

    // Traducciones específicas basadas en el contenido del mensaje
    if (errorDetail) {
      // Fondos insuficientes
      if (errorDetail.toLowerCase().includes('insufficient funds')) {
        const balanceMatch = errorDetail.match(/balance:\s*([\d.]+)/i);
        const requestedMatch = errorDetail.match(/requested:\s*([\d.]+)/i);
        if (balanceMatch && requestedMatch) {
          return `Fondos insuficientes. Saldo actual: $${balanceMatch[1]}, Solicitado: $${requestedMatch[1]}`;
        }
        return 'Fondos insuficientes para realizar la operación';
      }

      // Cliente inactivo
      if (errorDetail.toLowerCase().includes('inactive customer')) {
        const customerMatch = errorDetail.match(/customer:\s*(\w+)/i);
        const customerCode = customerMatch ? customerMatch[1] : '';
        return `No se puede crear cuenta para cliente inactivo${customerCode ? ': ' + customerCode : ''}`;
      }

      // Cuenta inactiva
      if (errorDetail.toLowerCase().includes('inactive account')) {
        const accountMatch = errorDetail.match(/account:\s*(\d+)/i);
        const accountNumber = accountMatch ? accountMatch[1] : '';
        return `No se puede realizar transacción en cuenta inactiva${accountNumber ? ': ' + accountNumber : ''}`;
      }

      // Límite diario excedido
      if (errorDetail.toLowerCase().includes('daily limit exceeded')) {
        return 'Límite diario excedido';
      }

      // Registro duplicado
      if (errorDetail.toLowerCase().includes('already exists') || errorDetail.toLowerCase().includes('duplicate')) {
        return 'Ya existe un registro con estos datos';
      }

      // Si el mensaje está en inglés técnico, intentar traducir palabras clave
      if (errorDetail.includes('not found')) {
        return 'El recurso solicitado no fue encontrado';
      }

      // Si no hay traducción específica, usar el mensaje del backend directamente
      return errorDetail;
    }

    // Fallback a traducciones por código
    if (messageCode && this.errorMessages[messageCode]) {
      return this.errorMessages[messageCode];
    }

    // Último recurso: usar el mensaje genérico
    if (message) {
      return message;
    }

    return 'Ha ocurrido un error';
  }

  show(message: string, type: 'success' | 'error' | 'info' | 'warning' = 'info', duration: number = 3000) {
    const id = this.idCounter++;
    const notification: Notification = { id, message, type };

    this.notifications.update(current => [...current, notification]);

    setTimeout(() => {
      this.remove(id);
    }, duration);
  }

  success(message: string, duration: number = 3000) {
    this.show(message, 'success', duration);
  }

  error(message: string, duration: number = 4000) {
    this.show(message, 'error', duration);
  }

  info(message: string, duration: number = 3000) {
    this.show(message, 'info', duration);
  }

  warning(message: string, duration: number = 3000) {
    this.show(message, 'warning', duration);
  }

  remove(id: number) {
    this.notifications.update(current => current.filter(n => n.id !== id));
  }
}
