import { Component, EventEmitter, Output, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PatientService, PatientRequest } from './patient.service';

@Component({
  selector: 'app-add-patient-dialog',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg shadow-xl w-full max-w-md mx-4">
        <div class="flex justify-between items-center p-4 border-b">
          <h2 class="text-lg font-semibold">Add New Patient</h2>
          <button (click)="close.emit()" class="text-gray-500 hover:text-gray-700">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
            </svg>
          </button>
        </div>

        <form (ngSubmit)="save()" class="p-4 space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">First Name *</label>
            <input
              type="text"
              [(ngModel)]="form.firstName"
              name="firstName"
              required
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Last Name</label>
            <input
              type="text"
              [(ngModel)]="form.lastName"
              name="lastName"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Date of Birth</label>
            <input
              type="date"
              [(ngModel)]="form.dob"
              name="dob"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Gender</label>
            <select
              [(ngModel)]="form.gender"
              name="gender"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">Select gender</option>
              <option value="MALE">Male</option>
              <option value="FEMALE">Female</option>
              <option value="OTHER">Other</option>
            </select>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Phone Number</label>
            <input
              type="tel"
              [(ngModel)]="form.phoneNo"
              name="phoneNo"
              placeholder="0412345678"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <div class="border-t pt-4 mt-4">
            <h3 class="text-sm font-medium text-gray-700 mb-2">Address (Optional)</h3>
            <div class="space-y-2">
              <input
                type="text"
                [(ngModel)]="form.australianAddress!.address"
                name="address"
                placeholder="Address"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
              <div class="grid grid-cols-3 gap-2">
                <input
                  type="text"
                  [(ngModel)]="form.australianAddress!.suburb"
                  name="suburb"
                  placeholder="Suburb"
                  class="px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                <input
                  type="text"
                  [(ngModel)]="form.australianAddress!.state"
                  name="state"
                  placeholder="State"
                  class="px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                <input
                  type="text"
                  [(ngModel)]="form.australianAddress!.postcode"
                  name="postcode"
                  placeholder="Postcode"
                  class="px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
            </div>
          </div>

          @if (error()) {
            <div class="text-red-600 text-sm">{{ error() }}</div>
          }

          <div class="flex justify-end gap-2 pt-2">
            <button
              type="button"
              (click)="close.emit()"
              class="px-4 py-2 text-gray-700 bg-gray-200 rounded-lg hover:bg-gray-300"
            >
              Cancel
            </button>
            <button
              type="submit"
              [disabled]="saving()"
              class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50"
            >
              {{ saving() ? 'Saving...' : 'Save' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  `
})
export class AddPatientDialogComponent {
  private patientService = inject(PatientService);

  @Output() close = new EventEmitter<void>();
  @Output() saved = new EventEmitter<void>();

  saving = signal(false);
  error = signal<string | null>(null);

  form: PatientRequest = {
    firstName: '',
    lastName: '',
    dob: '',
    gender: undefined,
    phoneNo: '',
    australianAddress: {
      address: '',
      suburb: '',
      state: '',
      postcode: ''
    }
  };

  save() {
    if (!this.form.firstName?.trim()) {
      this.error.set('First name is required');
      return;
    }

    this.saving.set(true);
    this.error.set(null);

    const data: PatientRequest = {
      firstName: this.form.firstName,
      lastName: this.form.lastName || undefined,
      dob: this.form.dob || undefined,
      gender: this.form.gender as 'MALE' | 'FEMALE' | 'OTHER' | undefined,
      phoneNo: this.form.phoneNo || undefined,
      australianAddress: this.form.australianAddress?.address ? {
        address: this.form.australianAddress.address,
        suburb: this.form.australianAddress.suburb,
        state: this.form.australianAddress.state,
        postcode: this.form.australianAddress.postcode
      } : undefined
    };

    this.patientService.createPatient(data).subscribe({
      next: () => {
        this.saving.set(false);
        this.saved.emit();
      },
      error: (err) => {
        this.saving.set(false);
        this.error.set(err.error?.message || 'Failed to save patient');
      }
    });
  }
}
