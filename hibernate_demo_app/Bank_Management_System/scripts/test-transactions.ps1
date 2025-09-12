$ErrorActionPreference = 'Stop'

$base = 'http://localhost:8080'

Start-Sleep -Seconds 5

function PostJson {
    param(
        [string]$Path,
        [hashtable]$Body,
        [hashtable]$Headers
    )
    if ($null -ne $Headers) {
        return Invoke-RestMethod -Method Post -Uri ($base + $Path) -ContentType 'application/json' -Headers $Headers -Body ($Body | ConvertTo-Json -Depth 6)
    } else {
        return Invoke-RestMethod -Method Post -Uri ($base + $Path) -ContentType 'application/json' -Body ($Body | ConvertTo-Json -Depth 6)
    }
}

function GetAuthHeaders {
    param([string]$Token)
    return @{ 'Authorization' = ('Bearer ' + $Token) }
}

$u1 = 'alice' + (Get-Random)
$u2 = 'bob' + (Get-Random)

$reg1 = [ordered]@{
    username   = $u1
    password   = 'Passw0rd!'
    fullName   = 'Alice A'
    mobile     = '1111111111'
    email      = ($u1 + '@ex.com')
    aadhar     = '111122223333'
    accountType= 'SAVINGS'
    balance    = 1000
    role       = 'Customer'
}
$reg2 = [ordered]@{
    username   = $u2
    password   = 'Passw0rd!'
    fullName   = 'Bob B'
    mobile     = '2222222222'
    email      = ($u2 + '@ex.com')
    aadhar     = '444455556666'
    accountType= 'SAVINGS'
    balance    = 500
    role       = 'Customer'
}

$r1 = PostJson -Path '/api/auth/register' -Body $reg1
$r2 = PostJson -Path '/api/auth/register' -Body $reg2

$t1 = $r1.token
$t2 = $r2.token

$h1 = GetAuthHeaders -Token $t1
$h2 = GetAuthHeaders -Token $t2

$a1 = Invoke-RestMethod -Method Get -Headers $h1 -Uri ($base + '/api/accounts/my-account')
$a2 = Invoke-RestMethod -Method Get -Headers $h2 -Uri ($base + '/api/accounts/my-account')

$acc1 = $a1.accountNumber
$acc2 = $a2.accountNumber

# Deposit into user1
$depBody = [ordered]@{ accountNumber = $acc1; amount = 100; description = 'Test deposit' }
PostJson -Path '/api/transactions/deposit' -Body $depBody -Headers $h1 | Out-Null

# Withdraw from user1
$wdBody = [ordered]@{ accountNumber = $acc1; amount = 50; description = 'Test withdrawal' }
PostJson -Path '/api/transactions/withdrawal' -Body $wdBody -Headers $h1 | Out-Null

# Transfer from user1 to user2
$trBody = [ordered]@{ fromAccount = $acc1; toAccount = $acc2; amount = 25 }
PostJson -Path '/api/transactions/transfer' -Body $trBody -Headers $h1 | Out-Null

$a1After = Invoke-RestMethod -Method Get -Headers $h1 -Uri ($base + '/api/accounts/my-account')
$a2After = Invoke-RestMethod -Method Get -Headers $h2 -Uri ($base + '/api/accounts/my-account')

$tx1 = Invoke-RestMethod -Method Get -Headers $h1 -Uri ($base + '/api/transactions/account/' + $acc1)
$tx2 = Invoke-RestMethod -Method Get -Headers $h2 -Uri ($base + '/api/transactions/account/' + $acc2)

Write-Output ('User1=' + $u1 + ' Acc=' + $acc1 + ' BalanceBefore=' + $a1.balance + ' BalanceAfter=' + $a1After.balance)
Write-Output ('User2=' + $u2 + ' Acc=' + $acc2 + ' BalanceBefore=' + $a2.balance + ' BalanceAfter=' + $a2After.balance)
Write-Output ('TxCount1=' + ($tx1 | Measure-Object | Select-Object -ExpandProperty Count) + ' TxCount2=' + ($tx2 | Measure-Object | Select-Object -ExpandProperty Count))



