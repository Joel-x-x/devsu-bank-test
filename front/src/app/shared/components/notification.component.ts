import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService } from '@core/services/notification.service';

@Component({
  selector: 'app-notification',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="notification-container">
      @for (notification of notificationService.getNotifications()(); track notification.id) {
        <div
          class="notification"
          [class.success]="notification.type === 'success'"
          [class.error]="notification.type === 'error'"
          [class.info]="notification.type === 'info'"
          [class.warning]="notification.type === 'warning'"
          (click)="notificationService.remove(notification.id)"
        >
          <span>{{ notification.message }}</span>
          <button class="close" (click)="notificationService.remove(notification.id)">&times;</button>
        </div>
      }
    </div>
  `,
  styles: [`
    .notification-container {
      position: fixed;
      top: 20px;
      right: 20px;
      z-index: 9999;
      display: flex;
      flex-direction: column;
      gap: 8px;
      max-width: 400px;
    }

    .notification {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 12px 16px;
      border-radius: 4px;
      background: white;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
      font-size: 14px;
      animation: slideIn 0.3s ease-out;
      cursor: pointer;
      border-left: 4px solid #6b7280;
    }

    @keyframes slideIn {
      from {
        transform: translateX(100%);
        opacity: 0;
      }
      to {
        transform: translateX(0);
        opacity: 1;
      }
    }

    .notification span {
      flex: 1;
      color: #374151;
    }

    .notification.success {
      border-left-color: #10b981;
      background: #f0fdf4;
    }

    .notification.success span {
      color: #065f46;
    }

    .notification.error {
      border-left-color: #ef4444;
      background: #fef2f2;
    }

    .notification.error span {
      color: #991b1b;
    }

    .notification.info {
      border-left-color: #3b82f6;
      background: #eff6ff;
    }

    .notification.info span {
      color: #1e40af;
    }

    .notification.warning {
      border-left-color: #f59e0b;
      background: #fffbeb;
    }

    .notification.warning span {
      color: #92400e;
    }

    .close {
      background: none;
      border: none;
      font-size: 20px;
      color: #9ca3af;
      cursor: pointer;
      padding: 0;
      margin-left: 12px;
      width: 20px;
      height: 20px;
      display: flex;
      align-items: center;
      justify-content: center;
      transition: color 0.2s;
    }

    .close:hover {
      color: #4b5563;
    }

    @media (max-width: 768px) {
      .notification-container {
        left: 20px;
        right: 20px;
        max-width: none;
      }
    }
  `]
})
export class NotificationComponent {
  notificationService = inject(NotificationService);
}
