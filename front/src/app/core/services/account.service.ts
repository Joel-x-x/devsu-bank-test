import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, PageResponse } from '../models/api-response.model';
import { Account, AccountRequest } from '../models/account.model';

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/accounts`;

  findAllPaginated(page: number = 0, size: number = 10, sortBy: string = 'id',
                   sortDirection: string = 'ASC', search?: string): Observable<ApiResponse<PageResponse<Account>>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortDirection', sortDirection);

    if (search) {
      params = params.set('search', search);
    }

    return this.http.get<ApiResponse<PageResponse<Account>>>(`${this.apiUrl}/paginated`, { params });
  }

  findById(id: string): Observable<ApiResponse<Account>> {
    return this.http.get<ApiResponse<Account>>(`${this.apiUrl}/${id}`);
  }

  findByCustomerId(customerId: string): Observable<ApiResponse<Account[]>> {
    return this.http.get<ApiResponse<Account[]>>(`${this.apiUrl}/customer/${customerId}`);
  }

  create(account: AccountRequest): Observable<ApiResponse<Account>> {
    return this.http.post<ApiResponse<Account>>(this.apiUrl, account);
  }

  update(id: string, account: Partial<AccountRequest>): Observable<ApiResponse<Account>> {
    return this.http.patch<ApiResponse<Account>>(`${this.apiUrl}/${id}`, account);
  }

  delete(id: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`);
  }
}
