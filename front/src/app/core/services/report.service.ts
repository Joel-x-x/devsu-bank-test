import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/reports`;

  generateAccountStatement(customerId: string, startDate: string, endDate: string, format: 'excel' | 'pdf'): Observable<Blob> {
    return this.http.get(
      `${this.apiUrl}/account-statement/${customerId}`,
      {
        params: { startDate, endDate, format },
        responseType: 'blob'
      }
    );
  }
}
