import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, PageResponse } from '../models/api-response.model';
import { Movement, MovementRequest } from '../models/movement.model';

@Injectable({
  providedIn: 'root'
})
export class MovementService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/movements`;

  findAllPaginated(page: number = 0, size: number = 10, sortBy: string = 'movementDate',
                   sortDirection: string = 'DESC', search?: string): Observable<ApiResponse<PageResponse<Movement>>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortDirection', sortDirection);

    if (search) {
      params = params.set('search', search);
    }

    return this.http.get<ApiResponse<PageResponse<Movement>>>(`${this.apiUrl}/paginated`, { params });
  }

  findById(id: string): Observable<ApiResponse<Movement>> {
    return this.http.get<ApiResponse<Movement>>(`${this.apiUrl}/${id}`);
  }

  findByAccountId(accountId: string, startDate?: string, endDate?: string): Observable<ApiResponse<Movement[]>> {
    let params = new HttpParams().set('accountId', accountId);
    if (startDate) params = params.set('startDate', startDate);
    if (endDate) params = params.set('endDate', endDate);

    return this.http.get<ApiResponse<Movement[]>>(`${this.apiUrl}/account`, { params });
  }

  create(movement: MovementRequest): Observable<ApiResponse<Movement>> {
    return this.http.post<ApiResponse<Movement>>(this.apiUrl, movement);
  }
}
