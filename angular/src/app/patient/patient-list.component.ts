import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { PatientService, PatientListEntry, PatientPage } from './patient.service';
import { AddPatientDialogComponent } from './add-patient-dialog.component';

@Component({
  selector: 'app-patient-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, AddPatientDialogComponent],
  template: `
    <div class="min-h-screen bg-gray-50 p-6">
      <div class="max-w-6xl mx-auto">
        <div class="flex justify-between items-center mb-6">
          <h1 class="text-2xl font-bold text-gray-800">Patients</h1>
        </div>

        <div class="bg-white rounded-lg shadow p-4 mb-4">
          <div class="flex items-center gap-6">
            <div class="flex gap-4">
              <label class="flex items-center gap-2 cursor-pointer">
                <input type="radio" name="searchType" [value]="'name'" [ngModel]="searchType()" (ngModelChange)="searchType.set($event)" class="w-4 h-4 text-blue-600">
                <span class="text-sm text-gray-700">Search by Name</span>
              </label>
              <label class="flex items-center gap-2 cursor-pointer">
                <input type="radio" name="searchType" [value]="'id'" [ngModel]="searchType()" (ngModelChange)="searchType.set($event)" class="w-4 h-4 text-blue-600">
                <span class="text-sm text-gray-700">Search by ID</span>
              </label>
            </div>
            <input
              type="text"
              [(ngModel)]="searchValue"
              (keyup.enter)="search()"
              [placeholder]="searchType() === 'name' ? 'Search by name...' : 'Search by ID...'"
              class="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
            <button
              (click)="search()"
              class="bg-gray-800 text-white px-4 py-2 rounded-lg hover:bg-gray-900 transition-colors"
            >
              Search
            </button>
          </div>
        </div>

        <div class="bg-white rounded-lg shadow overflow-hidden">
          <table class="w-full">
            <thead class="bg-gray-100">
              <tr>
                <th class="px-4 py-3 text-left text-sm font-semibold text-gray-600">ID</th>
                <th class="px-4 py-3 text-left text-sm font-semibold text-gray-600">First Name</th>
                <th class="px-4 py-3 text-left text-sm font-semibold text-gray-600">Last Name</th>
                <th class="px-4 py-3 text-left text-sm font-semibold text-gray-600">Date of Birth</th>
                <th class="px-4 py-3 text-right text-sm font-semibold text-gray-600">Actions</th>
              </tr>
            </thead>
            <tbody>
              @for (patient of patients(); track patient.id) {
                <tr class="border-t hover:bg-gray-50">
                  <td class="px-4 py-3 text-sm text-gray-600">{{ patient.id }}</td>
                  <td class="px-4 py-3 text-sm text-gray-800">{{ patient.firstName }}</td>
                  <td class="px-4 py-3 text-sm text-gray-800">{{ patient.lastName }}</td>
                  <td class="px-4 py-3 text-sm text-gray-600">{{ patient.dob || '-' }}</td>
                  <td class="px-4 py-3 text-right">
                    <button
                      (click)="deletePatient(patient.id)"
                      class="text-red-600 hover:text-red-800 text-sm"
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              } @empty {
                <tr>
                  <td colspan="5" class="px-4 py-8 text-center text-gray-500">
                    No patients found
                  </td>
                </tr>
              }
            </tbody>
          </table>
        </div>

        <div class="mt-4">
          <button
            (click)="showDialog.set(true)"
            class="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors"
          >
            Add Patient
          </button>
        </div>

        <div class="flex justify-between items-center mt-4">
          <span class="text-sm text-gray-600">
            Page {{ page() + 1 }} of {{ totalPages() }} ({{ totalElements() }} total)
          </span>
          <div class="flex gap-2">
            <button
              (click)="previousPage()"
              [disabled]="firstPage()"
              class="px-4 py-2 bg-gray-200 rounded-lg hover:bg-gray-300 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              Previous
            </button>
            <button
              (click)="nextPage()"
              [disabled]="lastPage()"
              class="px-4 py-2 bg-gray-200 rounded-lg hover:bg-gray-300 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              Next
            </button>
          </div>
        </div>
      </div>

      @if (showDialog()) {
        <app-add-patient-dialog
          (close)="showDialog.set(false)"
          (saved)="onPatientSaved()"
        />
      }
    </div>
  `
})
export class PatientListComponent {
  private patientService = inject(PatientService);

  patients = signal<PatientListEntry[]>([]);
  showDialog = signal(false);
  searchType = signal<'id' | 'name'>('name');
  searchValue = '';

  page = signal(0);
  size = 10;
  totalPages = signal(0);
  totalElements = signal(0);
  firstPage = signal(true);
  lastPage = signal(true);

  constructor() {
    this.loadPatients();
  }

  loadPatients(options: { id?: string; name?: string; page?: number } = {}) {
    this.patientService.getPatients({
      id: options.id,
      name: options.name,
      page: options.page ?? this.page(),
      size: this.size
    }).subscribe({
      next: (data) => {
        this.patients.set(data.content);
        this.totalPages.set(data.totalPages);
        this.totalElements.set(data.totalElements);
        this.firstPage.set(data.first);
        this.lastPage.set(data.last);
      },
      error: (err) => console.error('Failed to load patients', err)
    });
  }

  search() {
    const value = this.searchValue.trim();
    if (this.searchType() === 'id') {
      this.loadPatients({ id: value || undefined });
    } else {
      this.loadPatients({ name: value || undefined });
    }
  }

  deletePatient(id: string) {
    if (!confirm('Are you sure you want to delete this patient?')) {
      return;
    }
    this.patientService.deletePatient(id).subscribe({
      next: () => this.loadPatients(),
      error: (err) => console.error('Failed to delete patient', err)
    });
  }

  previousPage() {
    if (!this.firstPage()) {
      this.page.update(p => p - 1);
      this.loadPatients();
    }
  }

  nextPage() {
    if (!this.lastPage()) {
      this.page.update(p => p + 1);
      this.loadPatients();
    }
  }

  onPatientSaved() {
    this.showDialog.set(false);
    this.loadPatients();
  }
}
