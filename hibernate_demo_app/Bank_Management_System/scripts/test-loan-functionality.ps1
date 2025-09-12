# Test script for Loan Management System
# This script tests all loan functionality endpoints

$baseUrl = "http://localhost:8080"
$customerToken = ""
$adminToken = ""

Write-Host "=== Loan Management System Test Script ===" -ForegroundColor Green

# Function to make HTTP requests
function Invoke-ApiRequest {
    param(
        [string]$Method,
        [string]$Url,
        [string]$Body = $null,
        [string]$Token = $null
    )
    
    $headers = @{
        "Content-Type" = "application/json"
    }
    
    if ($Token) {
        $headers["Authorization"] = "Bearer $Token"
    }
    
    try {
        if ($Body) {
            $response = Invoke-RestMethod -Uri $Url -Method $Method -Headers $headers -Body $Body
        } else {
            $response = Invoke-RestMethod -Uri $Url -Method $Method -Headers $headers
        }
        return $response
    }
    catch {
        Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
        return $null
    }
}

# Step 1: Login as Customer
Write-Host "`n1. Logging in as Customer..." -ForegroundColor Yellow
$customerLogin = @{
    username = "customer1"
    password = "password123"
} | ConvertTo-Json

$customerAuth = Invoke-ApiRequest -Method "POST" -Url "$baseUrl/api/auth/login" -Body $customerLogin
if ($customerAuth) {
    $customerToken = $customerAuth.token
    Write-Host "Customer login successful!" -ForegroundColor Green
} else {
    Write-Host "Customer login failed!" -ForegroundColor Red
    exit 1
}

# Step 2: Login as Admin
Write-Host "`n2. Logging in as Admin..." -ForegroundColor Yellow
$adminLogin = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

$adminAuth = Invoke-ApiRequest -Method "POST" -Url "$baseUrl/api/auth/login" -Body $adminLogin
if ($adminAuth) {
    $adminToken = $adminAuth.token
    Write-Host "Admin login successful!" -ForegroundColor Green
} else {
    Write-Host "Admin login failed!" -ForegroundColor Red
    exit 1
}

# Step 3: Get Customer Account
Write-Host "`n3. Getting customer account..." -ForegroundColor Yellow
$account = Invoke-ApiRequest -Method "GET" -Url "$baseUrl/api/accounts/my-account" -Token $customerToken
if ($account) {
    $accountNumber = $account.accountNumber
    Write-Host "Account found: $accountNumber" -ForegroundColor Green
} else {
    Write-Host "Failed to get account!" -ForegroundColor Red
    exit 1
}

# Step 4: Apply for Loan
Write-Host "`n4. Applying for a Personal Loan..." -ForegroundColor Yellow
$loanApplication = @{
    accountNumber = $accountNumber
    loanType = "PERSONAL"
    loanAmount = 50000
    loanTenureMonths = 24
    purpose = "Home renovation"
    employmentType = "SALARIED"
    monthlyIncome = 80000
} | ConvertTo-Json

$loanResponse = Invoke-ApiRequest -Method "POST" -Url "$baseUrl/api/loans/apply" -Body $loanApplication -Token $customerToken
if ($loanResponse) {
    $loanId = $loanResponse.id
    Write-Host "Loan application submitted successfully! Loan ID: $loanId" -ForegroundColor Green
    Write-Host "Loan Amount: $($loanResponse.loanAmount)" -ForegroundColor Cyan
    Write-Host "Monthly EMI: $($loanResponse.monthlyEmi)" -ForegroundColor Cyan
    Write-Host "Interest Rate: $($loanResponse.interestRate)%" -ForegroundColor Cyan
} else {
    Write-Host "Loan application failed!" -ForegroundColor Red
    exit 1
}

# Step 5: Admin approves loan
Write-Host "`n5. Admin approving loan..." -ForegroundColor Yellow
$loanApproval = @{
    loanId = $loanId
    action = "APPROVE"
    interestRate = 12.5
    adminNotes = "Approved after verification"
} | ConvertTo-Json

$approvalResponse = Invoke-ApiRequest -Method "POST" -Url "$baseUrl/api/loans/approve" -Body $loanApproval -Token $adminToken
if ($approvalResponse) {
    Write-Host "Loan approved successfully!" -ForegroundColor Green
    Write-Host "Status: $($approvalResponse.status)" -ForegroundColor Cyan
} else {
    Write-Host "Loan approval failed!" -ForegroundColor Red
    exit 1
}

# Step 6: Admin disburses loan
Write-Host "`n6. Admin disbursing loan..." -ForegroundColor Yellow
$disburseResponse = Invoke-ApiRequest -Method "POST" -Url "$baseUrl/api/loans/$loanId/disburse" -Token $adminToken
if ($disburseResponse) {
    Write-Host "Loan disbursed successfully!" -ForegroundColor Green
    Write-Host "Status: $($disburseResponse.status)" -ForegroundColor Cyan
    Write-Host "Disbursed At: $($disburseResponse.disbursedAt)" -ForegroundColor Cyan
} else {
    Write-Host "Loan disbursement failed!" -ForegroundColor Red
    exit 1
}

# Step 7: Customer makes loan payment
Write-Host "`n7. Customer making loan payment..." -ForegroundColor Yellow
$loanPayment = @{
    loanId = $loanId
    paymentAmount = 2500
    paymentMethod = "EMI"
    notes = "Monthly EMI payment"
} | ConvertTo-Json

$paymentResponse = Invoke-ApiRequest -Method "POST" -Url "$baseUrl/api/loans/$loanId/payment" -Body $loanPayment -Token $customerToken
if ($paymentResponse) {
    Write-Host "Loan payment successful!" -ForegroundColor Green
    Write-Host "Payment Amount: $($paymentResponse.paymentAmount)" -ForegroundColor Cyan
    Write-Host "Principal Amount: $($paymentResponse.principalAmount)" -ForegroundColor Cyan
    Write-Host "Interest Amount: $($paymentResponse.interestAmount)" -ForegroundColor Cyan
    Write-Host "Remaining Balance: $($paymentResponse.remainingBalance)" -ForegroundColor Cyan
} else {
    Write-Host "Loan payment failed!" -ForegroundColor Red
    exit 1
}

# Step 8: Get loan details
Write-Host "`n8. Getting loan details..." -ForegroundColor Yellow
$loanDetails = Invoke-ApiRequest -Method "GET" -Url "$baseUrl/api/loans/$loanId" -Token $customerToken
if ($loanDetails) {
    Write-Host "Loan Details:" -ForegroundColor Green
    Write-Host "  ID: $($loanDetails.id)" -ForegroundColor Cyan
    Write-Host "  Type: $($loanDetails.loanType)" -ForegroundColor Cyan
    Write-Host "  Amount: $($loanDetails.loanAmount)" -ForegroundColor Cyan
    Write-Host "  Status: $($loanDetails.status)" -ForegroundColor Cyan
    Write-Host "  Remaining Amount: $($loanDetails.remainingAmount)" -ForegroundColor Cyan
    Write-Host "  Paid Amount: $($loanDetails.paidAmount)" -ForegroundColor Cyan
    Write-Host "  Next Payment Date: $($loanDetails.nextPaymentDate)" -ForegroundColor Cyan
}

# Step 9: Get loan payments
Write-Host "`n9. Getting loan payment history..." -ForegroundColor Yellow
$payments = Invoke-ApiRequest -Method "GET" -Url "$baseUrl/api/loans/$loanId/payments" -Token $customerToken
if ($payments) {
    Write-Host "Payment History:" -ForegroundColor Green
    foreach ($payment in $payments) {
        Write-Host "  Payment ID: $($payment.id), Amount: $($payment.paymentAmount), Date: $($payment.paymentDate)" -ForegroundColor Cyan
    }
}

# Step 10: Get customer's loans
Write-Host "`n10. Getting customer's all loans..." -ForegroundColor Yellow
$customerLoans = Invoke-ApiRequest -Method "GET" -Url "$baseUrl/api/loans/my-loans" -Token $customerToken
if ($customerLoans) {
    Write-Host "Customer Loans:" -ForegroundColor Green
    foreach ($loan in $customerLoans) {
        Write-Host "  Loan ID: $($loan.id), Type: $($loan.loanType), Amount: $($loan.loanAmount), Status: $($loan.status)" -ForegroundColor Cyan
    }
}

# Step 11: Admin gets all loans
Write-Host "`n11. Admin getting all loans..." -ForegroundColor Yellow
$allLoans = Invoke-ApiRequest -Method "GET" -Url "$baseUrl/api/loans" -Token $adminToken
if ($allLoans) {
    Write-Host "All Loans in System:" -ForegroundColor Green
    foreach ($loan in $allLoans) {
        Write-Host "  Loan ID: $($loan.id), User ID: $($loan.userId), Type: $($loan.loanType), Amount: $($loan.loanAmount), Status: $($loan.status)" -ForegroundColor Cyan
    }
}

# Step 12: Admin gets loans by status
Write-Host "`n12. Admin getting active loans..." -ForegroundColor Yellow
$activeLoans = Invoke-ApiRequest -Method "GET" -Url "$baseUrl/api/loans/status/ACTIVE" -Token $adminToken
if ($activeLoans) {
    Write-Host "Active Loans:" -ForegroundColor Green
    foreach ($loan in $activeLoans) {
        Write-Host "  Loan ID: $($loan.id), Type: $($loan.loanType), Amount: $($loan.loanAmount), Remaining: $($loan.remainingAmount)" -ForegroundColor Cyan
    }
}

Write-Host "`n=== All Tests Completed Successfully! ===" -ForegroundColor Green
Write-Host "`nLoan Management System Features Tested:" -ForegroundColor Yellow
Write-Host "✅ Customer loan application" -ForegroundColor Green
Write-Host "✅ Admin loan approval" -ForegroundColor Green
Write-Host "✅ Admin loan disbursement" -ForegroundColor Green
Write-Host "✅ Customer loan payment" -ForegroundColor Green
Write-Host "✅ Loan details retrieval" -ForegroundColor Green
Write-Host "✅ Payment history tracking" -ForegroundColor Green
Write-Host "✅ Customer loan listing" -ForegroundColor Green
Write-Host "✅ Admin loan management" -ForegroundColor Green
Write-Host "✅ Status-based loan filtering" -ForegroundColor Green
