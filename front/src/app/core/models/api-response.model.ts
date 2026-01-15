export interface ApiResponse<T> {
  success: boolean;
  result: T;
  code: number;
  message: string;
  messageCode: string;
  timestamp: string;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}
