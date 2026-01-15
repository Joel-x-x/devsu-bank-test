import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, PageResponse } from '../models/api-response.model';
import { Customer, CustomerRequest } from '../models/customer.model';

@Injectable({
  providedIn: 'root'
})
export class CustomerService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/customers`;

  findAllPaginated(page: number = 0, size: number = 10, sortBy: string = 'id',
                   sortDirection: string = 'ASC', search?: string): Observable<ApiResponse<PageResponse<Customer>>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortDirection', sortDirection);

    if (search) {
      params = params.set('search', search);
    }

    return this.http.get<ApiResponse<PageResponse<Customer>>>(`${this.apiUrl}/paginated`, { params });
  }

  findById(id: string): Observable<ApiResponse<Customer>> {
    return this.http.get<ApiResponse<Customer>>(`${this.apiUrl}/${id}`);
  }

  create(customer: CustomerRequest): Observable<ApiResponse<Customer>> {
    return this.http.post<ApiResponse<Customer>>(this.apiUrl, customer);
  }

  update(id: string, customer: Partial<CustomerRequest>): Observable<ApiResponse<Customer>> {
    return this.http.patch<ApiResponse<Customer>>(`${this.apiUrl}/${id}`, customer);
  }

  delete(id: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`);
  }
}
