import { Injectable, signal } from '@angular/core';

export type ToastType = 'success' | 'error' | 'info';

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  message = signal<string | null>(null);
  type = signal<ToastType>('info');
  visible = signal(false);

  private timeoutId: ReturnType<typeof setTimeout> | null = null;

  show(message: string, toastType: ToastType = 'info', duration = 4000) {
    this.message.set(message);
    this.type.set(toastType);
    this.visible.set(true);

    if (this.timeoutId) {
      clearTimeout(this.timeoutId);
    }

    this.timeoutId = setTimeout(() => {
      this.hide();
    }, duration);
  }

  showSuccess(message: string) {
    this.show(message, 'success');
  }

  showError(message: string) {
    this.show(message, 'error', 6000);
  }

  showInfo(message: string) {
    this.show(message, 'info');
  }

  hide() {
    this.visible.set(false);
    this.message.set(null);
  }
}
