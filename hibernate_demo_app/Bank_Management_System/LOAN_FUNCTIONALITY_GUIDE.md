# Loan Management System - Complete Implementation Guide

## Overview
This document describes the comprehensive loan management functionality added to the Bank Management System. The system allows customers to apply for loans, admins to approve/reject applications, and customers to make loan payments.

## Features Implemented

### 🏦 Customer Features
- **Loan Application**: Apply for different types of loans (Personal, Home, Car, Business, Education)
- **Loan Management**: View all personal loans and their status
- **Loan Payment**: Make EMI payments, partial payments, or full payments
- **Payment History**: Track all loan payments made
- **Loan Details**: View detailed loan information

### 👨‍💼 Admin Features
- **Loan Approval**: Approve or reject loan applications
- **Loan Disbursement**: Disburse approved loans to customer accounts
- **Loan Management**: View all loans in the system
- **Status Filtering**: Filter loans by status (PENDING, APPROVED, ACTIVE, etc.)
- **Interest Rate Management**: Set custom interest rates for loans

## Database Schema

### Loan Entity
```sql
CREATE TABLE loans (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    account_number VARCHAR(32) NOT NULL,
    loan_type VARCHAR(50) NOT NULL,
    loan_amount DECIMAL(15,2) NOT NULL,
    interest_rate DECIMAL(5,2) NOT NULL,
    loan_tenure_months INT NOT NULL,
    monthly_emi DECIMAL(15,2) NOT NULL,
    total_amount DECIMAL(15,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    purpose VARCHAR(255),
    employment_type VARCHAR(50),
    monthly_income DECIMAL(15,2),
    applied_at TIMESTAMP NOT NULL,
    approved_at TIMESTAMP,
    disbursed_at TIMESTAMP,
    start_date DATE,
    end_date DATE,
    next_payment_date DATE,
    remaining_amount DECIMAL(15,2),
    paid_amount DECIMAL(15,2) DEFAULT 0,
    admin_notes VARCHAR(500),
    rejection_reason VARCHAR(500),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (account_number) REFERENCES accounts(account_number)
);
```

### LoanPayment Entity
```sql
CREATE TABLE loan_payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    loan_id BIGINT NOT NULL,
    payment_amount DECIMAL(15,2) NOT NULL,
    principal_amount DECIMAL(15,2) NOT NULL,
    interest_amount DECIMAL(15,2) NOT NULL,
    payment_date DATE NOT NULL,
    payment_timestamp TIMESTAMP NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED',
    transaction_reference VARCHAR(100),
    notes VARCHAR(255),
    remaining_balance DECIMAL(15,2) NOT NULL,
    FOREIGN KEY (loan_id) REFERENCES loans(id)
);
```

## API Endpoints

### Customer Endpoints

#### 1. Apply for Loan
```http
POST /api/loans/apply
Authorization: Bearer {customer_token}
Content-Type: application/json

{
    "accountNumber": "ACC123456789",
    "loanType": "PERSONAL",
    "loanAmount": 50000,
    "loanTenureMonths": 24,
    "purpose": "Home renovation",
    "employmentType": "SALARIED",
    "monthlyIncome": 80000
}
```

#### 2. Get My Loans
```http
GET /api/loans/my-loans
Authorization: Bearer {customer_token}
```

#### 3. Make Loan Payment
```http
POST /api/loans/{loanId}/payment
Authorization: Bearer {customer_token}
Content-Type: application/json

{
    "loanId": 1,
    "paymentAmount": 2500,
    "paymentMethod": "EMI",
    "notes": "Monthly EMI payment"
}
```

#### 4. Get Loan Payment History
```http
GET /api/loans/{loanId}/payments
Authorization: Bearer {customer_token}
```

#### 5. Get Loan Details
```http
GET /api/loans/{loanId}
Authorization: Bearer {customer_token}
```

### Admin Endpoints

#### 1. Get All Loans
```http
GET /api/loans
Authorization: Bearer {admin_token}
```

#### 2. Get Loans by Status
```http
GET /api/loans/status/{status}
Authorization: Bearer {admin_token}
```

#### 3. Approve/Reject Loan
```http
POST /api/loans/approve
Authorization: Bearer {admin_token}
Content-Type: application/json

{
    "loanId": 1,
    "action": "APPROVE",
    "interestRate": 12.5,
    "adminNotes": "Approved after verification"
}
```

#### 4. Disburse Loan
```http
POST /api/loans/{loanId}/disburse
Authorization: Bearer {admin_token}
```

## Loan Types and Interest Rates

| Loan Type | Default Interest Rate | Min Tenure | Max Tenure |
|-----------|----------------------|------------|------------|
| PERSONAL  | 12.0%               | 6 months   | 60 months  |
| HOME      | 8.5%                | 12 months  | 360 months |
| CAR       | 10.0%               | 12 months  | 84 months  |
| BUSINESS  | 15.0%               | 6 months   | 120 months |
| EDUCATION | 9.0%                | 12 months  | 120 months |

## Loan Status Flow

```
PENDING → APPROVED → ACTIVE → COMPLETED
    ↓         ↓
 REJECTED  (Can be disbursed to ACTIVE)
```

- **PENDING**: Loan application submitted, awaiting admin approval
- **APPROVED**: Admin approved the loan, ready for disbursement
- **ACTIVE**: Loan disbursed, customer can make payments
- **COMPLETED**: Loan fully paid off
- **REJECTED**: Loan application rejected by admin

## Business Rules

### Loan Application Rules
1. Customer can only have one active loan at a time
2. Minimum loan amount: ₹1,000
3. Maximum loan amount: ₹10,000,000
4. Minimum tenure: 6 months
5. Maximum tenure: 360 months (30 years)
6. Account must belong to the applying user

### Payment Rules
1. Payment amount must be greater than 0
2. Account must have sufficient balance for payment
3. Only active loans can receive payments
4. Payment automatically calculates principal and interest portions
5. Loan status changes to COMPLETED when fully paid

### Admin Rules
1. Only admins can approve/reject loans
2. Only admins can disburse approved loans
3. Admin can set custom interest rates during approval
4. Admin can add notes for approval/rejection decisions

## EMI Calculation

The system uses the standard EMI formula:
```
EMI = P × r × (1 + r)^n / ((1 + r)^n - 1)
```

Where:
- P = Principal loan amount
- r = Monthly interest rate (annual rate / 12 / 100)
- n = Loan tenure in months

## Security Features

1. **Role-based Access Control**: Customers can only access their own loans
2. **JWT Authentication**: All endpoints require valid authentication
3. **Input Validation**: Comprehensive validation for all input fields
4. **Transaction Integrity**: All loan operations are transactional
5. **Audit Trail**: All loan activities are logged

## Testing

Use the provided test script `scripts/test-loan-functionality.ps1` to test all loan functionality:

```powershell
.\scripts\test-loan-functionality.ps1
```

The test script covers:
- Customer loan application
- Admin loan approval
- Admin loan disbursement
- Customer loan payment
- All retrieval endpoints
- Error scenarios

## Error Handling

The system handles various error scenarios:
- Insufficient account balance
- Invalid loan status transitions
- Unauthorized access attempts
- Invalid input data
- Resource not found errors

## Future Enhancements

Potential future improvements:
1. **Automated EMI Processing**: Automatic monthly EMI deductions
2. **Loan Prepayment**: Support for early loan closure
3. **Loan Refinancing**: Ability to refinance existing loans
4. **Credit Score Integration**: External credit score checks
5. **Loan Insurance**: Optional loan insurance products
6. **Mobile Notifications**: Payment reminders and alerts
7. **Loan Calculator API**: Public API for EMI calculations

## Conclusion

The loan management system provides a complete solution for:
- Customer loan applications and management
- Admin loan approval and disbursement
- Comprehensive payment tracking
- Robust security and validation
- Scalable architecture for future enhancements

All functionality is fully integrated with the existing Bank Management System and follows the same architectural patterns and security standards.
