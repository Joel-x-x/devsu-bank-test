import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-modal',
  standalone: true,
  templateUrl: './modal.component.html',
  styleUrl: './modal.component.css'
})
export class ModalComponent {
  title = input.required<string>();
  isOpen = input.required<boolean>();
  close = output<void>();

  onClose() {
    this.close.emit();
  }
}
