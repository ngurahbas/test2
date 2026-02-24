import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PatientListEntry {
  id: string;
  firstName: string;
  lastName: string;
  dob: string | null;
}

export interface PatientPage {
  content: PatientListEntry[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

export interface PatientRequest {
  firstName: string;
  lastName?: string;
  dob?: string;
  gender?: 'MALE' | 'FEMALE' | 'OTHER';
  phoneNo?: string;
  australianAddress?: {
    address: string;
    suburb: string;
    state: string;
    postcode: string;
  };
}

@Injectable({
  providedIn: 'root'
})
export class PatientService {
  private http = inject(HttpClient);
  private baseUrl = '/api/patient';

  getPatients(options: {
    id?: string;
    name?: string;
    page?: number;
    size?: number;
  }): Observable<PatientPage> {
    let params = new HttpParams()
      .set('page', options.page?.toString() ?? '0')
      .set('size', options.size?.toString() ?? '10');

    if (options.id) {
      params = params.set('id', options.id);
    }
    if (options.name) {
      params = params.set('name', options.name);
    }

    return this.http.get<PatientPage>(this.baseUrl, { params });
  }

  createPatient(data: PatientRequest): Observable<PatientRequest> {
    return this.http.post<PatientRequest>(this.baseUrl, data);
  }

  deletePatient(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
