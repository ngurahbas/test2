import { Component, EventEmitter, Output, Input, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PatientService, PatientRequest, PatientResponse, IdentifierType, PatientIdentifier } from './patient.service';

@Component({
  selector: 'app-add-patient-dialog',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg shadow-xl w-full max-w-md mx-4">
        <div class="flex justify-between items-center p-4 border-b">
          <h2 class="text-lg font-semibold">{{ mode() === 'edit' ? 'Edit Patient' : 'Add New Patient' }}</h2>
          <button (click)="close.emit()" class="text-gray-500 hover:text-gray-700">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
            </svg>
          </button>
        </div>

        @if (loading()) {
          <div class="p-8 text-center text-gray-500">Loading...</div>
        } @else {
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

          @if (mode() === 'edit') {
            <div class="border-t pt-4 mt-4">
              <div class="flex justify-between items-center mb-2">
                <h3 class="text-sm font-medium text-gray-700">Identifiers</h3>
                @if (!showAddIdentifier()) {
                  <button
                    type="button"
                    (click)="showAddIdentifier.set(true)"
                    class="text-sm text-blue-600 hover:text-blue-800"
                  >
                    + Add Identifier
                  </button>
                }
              </div>

              @if (identifiers().length > 0) {
                <div class="space-y-2 mb-3">
                  @for (identifier of identifiers(); track identifier.id) {
                    <div class="flex items-center justify-between bg-gray-50 px-3 py-2 rounded">
                      <div>
                        <span class="text-xs text-gray-500">{{ getIdentifierTypeLabel(identifier.idType) }}</span>
                        <div class="text-sm text-gray-800">{{ identifier.idValue }}</div>
                      </div>
                      <button
                        type="button"
                        (click)="removeIdentifier(identifier.id)"
                        class="text-red-600 hover:text-red-800 text-sm"
                      >
                        Remove
                      </button>
                    </div>
                  }
                </div>
              }

              @if (showAddIdentifier()) {
                <div class="bg-gray-50 p-3 rounded space-y-2">
                  <select
                    [(ngModel)]="newIdentifier.idType"
                    name="idType"
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  >
                    <option value="">Select type</option>
                    @for (type of identifierTypes(); track type.value) {
                      <option [value]="type.value">{{ type.label }}</option>
                    }
                  </select>
                  <input
                    type="text"
                    [(ngModel)]="newIdentifier.idValue"
                    name="idValue"
                    placeholder="Identifier value"
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                  <div class="flex gap-2">
                    <button
                      type="button"
                      (click)="addIdentifier()"
                      [disabled]="!newIdentifier.idType || !newIdentifier.idValue || addingIdentifier()"
                      class="px-3 py-1 bg-blue-600 text-white text-sm rounded hover:bg-blue-700 disabled:opacity-50"
                    >
                      {{ addingIdentifier() ? 'Adding...' : 'Add' }}
                    </button>
                    <button
                      type="button"
                      (click)="showAddIdentifier.set(false); newIdentifier = { idType: '', idValue: '' }"
                      class="px-3 py-1 text-gray-600 text-sm"
                    >
                      Cancel
                    </button>
                  </div>
                </div>
              }
            </div>
          }

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
        }
      </div>
    </div>
  `
})
export class AddPatientDialogComponent implements OnInit {
  private patientService = inject(PatientService);

  @Input() patientId?: string;
  @Output() close = new EventEmitter<void>();
  @Output() saved = new EventEmitter<void>();

  mode = signal<'add' | 'edit'>('add');
  saving = signal(false);
  loading = signal(false);
  error = signal<string | null>(null);

  identifiers = signal<PatientIdentifier[]>([]);
  identifierTypes = signal<IdentifierType[]>([]);
  showAddIdentifier = signal(false);
  addingIdentifier = signal(false);
  newIdentifier = { idType: '', idValue: '' };

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

  ngOnInit() {
    this.patientService.getIdentifierTypes().subscribe({
      next: (types) => this.identifierTypes.set(types)
    });

    if (this.patientId) {
      this.mode.set('edit');
      this.loading.set(true);
      this.patientService.getPatient(this.patientId).subscribe({
        next: (patient) => {
          this.form = {
            firstName: patient.firstName,
            lastName: patient.lastName || '',
            dob: patient.dob || '',
            gender: patient.gender,
            phoneNo: patient.phoneNo || '',
            australianAddress: patient.australianAddress || {
              address: '',
              suburb: '',
              state: '',
              postcode: ''
            }
          };
          this.loading.set(false);
        },
        error: () => {
          this.error.set('Failed to load patient');
          this.loading.set(false);
        }
      });
      this.patientService.getPatientIdentifiers(this.patientId).subscribe({
        next: (ids) => this.identifiers.set(ids)
      });
    }
  }

  getIdentifierTypeLabel(value: string): string {
    const type = this.identifierTypes().find(t => t.value === value);
    return type?.label || value;
  }

  addIdentifier() {
    if (!this.patientId || !this.newIdentifier.idType || !this.newIdentifier.idValue) return;
    this.addingIdentifier.set(true);
    this.patientService.addPatientIdentifier(this.patientId, this.newIdentifier).subscribe({
      next: (identifier) => {
        this.identifiers.update(ids => [...ids, identifier]);
        this.showAddIdentifier.set(false);
        this.newIdentifier = { idType: '', idValue: '' };
        this.addingIdentifier.set(false);
      },
      error: () => {
        this.addingIdentifier.set(false);
      }
    });
  }

  removeIdentifier(id: string) {
    if (!this.patientId) return;
    this.patientService.deletePatientIdentifier(this.patientId, id).subscribe({
      next: () => {
        this.identifiers.update(ids => ids.filter(i => i.id !== id));
      }
    });
  }

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

    const operation = this.mode() === 'edit' && this.patientId
      ? this.patientService.updatePatient(this.patientId, data)
      : this.patientService.createPatient(data);

    operation.subscribe({
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
