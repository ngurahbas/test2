import { Routes } from '@angular/router';
import { PatientListComponent } from './patient/patient-list.component';

export const routes: Routes = [
  { path: '', redirectTo: 'patient', pathMatch: 'full' },
  { path: 'patient', component: PatientListComponent }
];
