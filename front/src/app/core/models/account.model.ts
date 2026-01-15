export interface Account {
  id: string;
  accountNumber: string;
  accountType: 'SAVINGS' | 'CHECKING';
  initialBalance: number;
  dailyLimit: number;
  status: boolean;
  customerId: string;
  customerName?: string;
}

export interface AccountRequest {
  customerId: string;
  accountType: 'SAVINGS' | 'CHECKING';
  initialBalance: number;
  dailyLimit: number;
  status?: boolean;
}
