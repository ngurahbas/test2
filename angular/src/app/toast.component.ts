import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService, ToastType } from './toast.service';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (toastService.visible()) {
      <div 
        class="fixed bottom-4 right-4 z-50 px-4 py-3 rounded-lg shadow-lg max-w-sm"
        [class.bg-green-500]="toastService.type() === 'success'"
        [class.bg-red-500]="toastService.type() === 'error'"
        [class.bg-blue-500]="toastService.type() === 'info'"
        [class.text-white]="true"
      >
        <div class="flex items-center justify-between gap-3">
          <span class="text-sm">{{ toastService.message() }}</span>
          <button 
            (click)="toastService.hide()" 
            class="text-white hover:text-gray-200"
          >
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
            </svg>
          </button>
        </div>
      </div>
    }
  `
})
export class ToastComponent {
  toastService = inject(ToastService);
}
