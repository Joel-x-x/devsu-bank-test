export interface Movement {
  id: string;
  movementDate: string;
  movementType: 'CREDIT' | 'DEBIT';
  amount: number;
  balance: number;
  accountId: string;
  accountNumber?: string;
  customerName?: string;
}

export interface MovementRequest {
  accountId: string;
  movementType: 'CREDIT' | 'DEBIT';
  amount: number;
}
