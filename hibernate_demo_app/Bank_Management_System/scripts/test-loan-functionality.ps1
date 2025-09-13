<#
 Self-contained Loan Management System E2E test
 - Registers temporary customer and admin
 - Performs: apply -> approve -> disburse -> pay -> fetch
 - Prints key outputs and verifies tokens work
 - Optional: cascade delete check at end (commented)
#>

$ErrorActionPreference = 'Stop'
$baseUrl = "http://localhost:8080"
Start-Sleep -Seconds 4

function Invoke-ApiRequest {
    param(
        [Parameter(Mandatory=$true)][ValidateSet('GET','POST','DELETE')][string]$Method,
        [Parameter(Mandatory=$true)][string]$Url,
        [string]$Body = $null,
        [string]$Token = $null
    )

    $headers = @{}
    if ($Body) { $headers['Content-Type'] = 'application/json' }
    if ($Token) { $headers['Authorization'] = "Bearer $Token" }

    for ($i=0; $i -lt 3; $i++) {
        try {
            if ($Body) {
                return Invoke-RestMethod -Uri $Url -Method $Method -Headers $headers -Body $Body
            } else {
                return Invoke-RestMethod -Uri $Url -Method $Method -Headers $headers
            }
        } catch {
            if ($i -ge 2) { throw }
            Start-Sleep -Seconds 2
        }
    }
}

Write-Host "=== E2E Loan Flow Test ===" -ForegroundColor Green

# 1) Register temporary customer and admin
$cust = 'cust'+(Get-Random)
$adm  = 'adm'+(Get-Random)

$regCustomer = @{
    username = $cust; password = 'Passw0rd!'; fullName = 'Cust User';
    mobile = '9999999999'; email = ("$cust@ex.com"); aadhar = '123456789012';
    accountType = 'SAVINGS'; balance = 3000; role = 'Customer'
} | ConvertTo-Json

$regAdmin = @{
    username = $adm; password = 'Passw0rd!'; fullName = 'Admin User';
    mobile = '8888888888'; email = ("$adm@ex.com"); aadhar = '210987654321';
    accountType = 'SAVINGS'; balance = 0; role = 'Admin'
} | ConvertTo-Json

$c = Invoke-ApiRequest -Method POST -Url "$baseUrl/api/auth/register" -Body $regCustomer
$a = Invoke-ApiRequest -Method POST -Url "$baseUrl/api/auth/register" -Body $regAdmin

$customerToken = $c.token
$adminToken    = $a.token
Write-Host ("CustomerToken="+$customerToken) -ForegroundColor Yellow
Write-Host ("AdminToken="+$adminToken) -ForegroundColor Yellow

# 2) Get customer account
$account = Invoke-ApiRequest -Method GET -Url "$baseUrl/api/accounts/my-account" -Token $customerToken
$accountNumber = $account.accountNumber
Write-Host ("Account="+$accountNumber) -ForegroundColor Cyan

# 3) Apply loan
$loanApplication = @{
    accountNumber = $accountNumber; loanType = 'PERSONAL'; loanAmount = 50000;
    loanTenureMonths = 12; purpose = 'Test'; employmentType = 'SALARIED'; monthlyIncome = 80000
} | ConvertTo-Json

$loan = Invoke-ApiRequest -Method POST -Url "$baseUrl/api/loans/apply" -Body $loanApplication -Token $customerToken
$loanId = $loan.id
Write-Host ("AppliedLoanId="+$loanId+" Status="+$loan.status) -ForegroundColor Green

# 4) Approve loan (admin)
$loanApproval = @{
    loanId = $loanId; action = 'APPROVE'; interestRate = 12.5; adminNotes = 'ok'
} | ConvertTo-Json

$loanApproved = Invoke-ApiRequest -Method POST -Url "$baseUrl/api/loans/approve" -Body $loanApproval -Token $adminToken
Write-Host ("ApprovedStatus="+$loanApproved.status) -ForegroundColor Green

# 5) Disburse loan (admin)
$loanDisbursed = Invoke-ApiRequest -Method POST -Url "$baseUrl/api/loans/$loanId/disburse" -Token $adminToken
Write-Host ("DisbursedStatus="+$loanDisbursed.status) -ForegroundColor Green

# 6) Make payment (customer)
$loanPayment = @{
    loanId = $loanId; paymentAmount = 2500; paymentMethod = 'EMI'; notes = 'first'
} | ConvertTo-Json

$payment = Invoke-ApiRequest -Method POST -Url "$baseUrl/api/loans/payment" -Body $loanPayment -Token $customerToken
Write-Host ("PaymentId="+$payment.id+" Amount="+$payment.paymentAmount) -ForegroundColor Green

# 7) Fetch details and history
$loanDetails = Invoke-ApiRequest -Method GET -Url "$baseUrl/api/loans/$loanId" -Token $customerToken
$payments    = Invoke-ApiRequest -Method GET -Url "$baseUrl/api/loans/$loanId/payments" -Token $customerToken
$myLoans     = Invoke-ApiRequest -Method GET -Url "$baseUrl/api/loans/my-loans" -Token $customerToken
$allLoans    = Invoke-ApiRequest -Method GET -Url "$baseUrl/api/loans" -Token $adminToken
$activeLoans = Invoke-ApiRequest -Method GET -Url "$baseUrl/api/loans/status/ACTIVE" -Token $adminToken

Write-Host ("LoansForCustomer="+($myLoans | Measure-Object | Select-Object -ExpandProperty Count)) -ForegroundColor Cyan
Write-Host ("PaymentsForLoan="+($payments | Measure-Object | Select-Object -ExpandProperty Count)) -ForegroundColor Cyan
Write-Host ("AllLoansCount="+($allLoans | Measure-Object | Select-Object -ExpandProperty Count)) -ForegroundColor Cyan
Write-Host ("ActiveLoansCount="+($activeLoans | Measure-Object | Select-Object -ExpandProperty Count)) -ForegroundColor Cyan

# 8) Optional: cascade delete verify (delete customer, then check access)
# Invoke-ApiRequest -Method DELETE -Url "$baseUrl/api/admin/users/$($c.userId)" -Token $adminToken | Out-Null
# try {
#     Invoke-ApiRequest -Method GET -Url "$baseUrl/api/accounts/my-account" -Token $customerToken | Out-Null
#     Write-Host "MyAccountAfterDelete=StillAccessible (unexpected)" -ForegroundColor Red
# } catch {
#     Write-Host "MyAccountAfterDelete=Gone" -ForegroundColor Green
# }

Write-Host "=== E2E Loan Flow Completed ===" -ForegroundColor Green
