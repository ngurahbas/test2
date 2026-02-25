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

export interface PatientResponse extends PatientRequest {
  id: string;
}

export interface IdentifierType {
  value: string;
  label: string;
}

export interface PatientIdentifier {
  id: string;
  idType: string;
  idValue: string;
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

  getPatient(id: string): Observable<PatientResponse> {
    return this.http.get<PatientResponse>(`${this.baseUrl}/${id}`);
  }

  updatePatient(id: string, data: PatientRequest): Observable<PatientResponse> {
    return this.http.put<PatientResponse>(`${this.baseUrl}/${id}`, data);
  }

  getIdentifierTypes(): Observable<IdentifierType[]> {
    return this.http.get<IdentifierType[]>('/api/enum/identifier-type');
  }

  getPatientIdentifiers(patientId: string): Observable<PatientIdentifier[]> {
    return this.http.get<PatientIdentifier[]>(`${this.baseUrl}/${patientId}/identifier`);
  }

  addPatientIdentifier(patientId: string, data: { idType: string; idValue: string }): Observable<PatientIdentifier> {
    return this.http.post<PatientIdentifier>(`${this.baseUrl}/${patientId}/identifier`, data);
  }

  deletePatientIdentifier(patientId: string, identifierId: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${patientId}/identifier/${identifierId}`);
  }
}
