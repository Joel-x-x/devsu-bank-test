export interface Customer {
  id: string;
  name: string;
  genre: 'M' | 'F' | 'O';
  birthDate: string;
  identification: string;
  address: string;
  phone: string;
  customerCode: string;
  password: string;
  status: boolean;
}

export interface CustomerRequest {
  name: string;
  genre: 'M' | 'F' | 'O';
  birthDate: string;
  identification: string;
  address: string;
  phone: string;
  password: string;
  status?: boolean;
}
