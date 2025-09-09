## API Reference

This document lists all controllers and endpoints across the project modules, including HTTP methods, request/response DTOs, and access rules (JWT Bearer + roles).

### Auth and Security
- Authentication: JWT Bearer. Send header `Authorization: Bearer <token>` on protected routes.
- Roles used: `Admin`, `Customer` (mapped internally to `ROLE_Admin`, `ROLE_Customer`).
- Public endpoints under `/api/auth/**` are open.

---

## Module: Bank_Management_System (`com.tss`)

### AuthController (`/api/auth`)
- POST `/register`
  - Body: `AuthRegisterRequestDto`
  - Response: `AuthResponseDto` (includes `token`)
  - Access: Public
- POST `/login`
  - Body: `AuthLoginRequestDto`
  - Response: `AuthResponseDto` (includes `token`)
  - Access: Public
- POST `/logout`
  - Response: 204 No Content
  - Access: Public

### AccountController (`/api/accounts`)
- GET `/`
  - Response: `List<AccountResponseDto>`
  - Access: Authenticated (role rules enforced in `SecurityConfig`)
- GET `/{accountNumber}`
  - Path: `accountNumber: String`
  - Response: `AccountResponseDto`
  - Access: Authenticated (role rules enforced in `SecurityConfig`)

### TransactionController (`/api/transactions`)
- POST `/transfer`
  - Body: `TransferRequestDto`
  - Response: `200 OK`
  - Access: `Customer`
- GET `/account/{accountNumber}`
  - Path: `accountNumber: String`
  - Response: `List<Transaction>`
  - Access: `Admin` or `Customer`

### CardController (`/api/cards`)
- POST `/apply`
  - Body: `CardApplicationRequestDto`
  - Response: `CardApplication`
  - Access: `Customer`
- GET `/user/{userId}`
  - Path: `userId: Long`
  - Response: `List<CardApplication>`
  - Access: `Admin` (via `@PreAuthorize("hasRole('ADMIN')")`)
- GET `/`
  - Query: `status: String?`
  - Response: `List<CardApplication>`
  - Access: `Admin` (via `@PreAuthorize("hasRole('ADMIN')")`)

### AdminController (`/api/admin`)
- GET `/users`
  - Response: `List<User>`
  - Access: `Admin`
- POST `/users/update`
  - Body: `AdminUpdateUserRequestDto`
  - Response: `User`
  - Access: `Admin`
- DELETE `/users/{userId}`
  - Path: `userId: Long`
  - Response: `204 No Content`
  - Access: `Admin`
- GET `/accounts`
  - Response: `List<Account>`
  - Access: `Admin`
- POST `/accounts/update`
  - Body: `AdminUpdateAccountRequestDto`
  - Response: `Account`
  - Access: `Admin`
- DELETE `/accounts/{accountNumber}`
  - Path: `accountNumber: String`
  - Response: `204 No Content`
  - Access: `Admin`
- GET `/cards`
  - Response: `List<CardApplication>`
  - Access: `Admin`
- POST `/cards/action`
  - Body: `AdminCardActionRequestDto`
  - Response: `CardApplication`
  - Access: `Admin`
- GET `/transactions`
  - Query: `accountNumber?: String, page: int=0, size: int=10`
  - Response: `Page<Transaction>`
  - Access: `Admin`

---

## Module: spring_security (`com.SpringSecurity`)

### RegisterController (`/api/register`)
- POST `/`
  - Body: `RegistrationDto`
  - Response: `UserResponseDto`
  - Access: Public

### LoginController (`/api/login`)
- POST `/`
  - Body: `LoginDto`
  - Response: `JwtAuthResponse` (token + type)
  - Access: Public

### AccountController (`/api/accounts`)
- GET `/`
  - Response: `List<AccountResponseDto>`
  - Access: `ADMIN` (via `@PreAuthorize("hasRole('ADMIN')")`)
- GET `/{id}`
  - Path: `id: int`
  - Response: `AccountResponseDto`
  - Access: Authenticated
- PUT `/{id}/disable`
  - Path: `id: int`
  - Response: `AccountResponseDto`
  - Access: `ADMIN` (via `@PreAuthorize("hasRole('ADMIN')")`)
- POST `/`
  - Body: `AccountRequestDto`
  - Response: `AccountResponseDto`
  - Access: Authenticated

---

## Security Rules Summary (`SecurityConfig` in Bank_Management_System)
- Public: `/api/auth/**`
- Admin-only: `/api/admin/**`
- Transactions:
  - GET `/api/transactions/**`: `Admin` or `Customer`
  - POST `/api/transactions/**`: `Customer`
- Cards: `/api/cards/**`: `Customer`; admin-only list endpoints are pre-authorized in controller
- Other endpoints: authenticated (and/or controller-level `@PreAuthorize`)

---

## Sample Requests

### Login
POST `/api/auth/login`
```json
{
  "username": "alice",
  "password": "secret"
}
```
Response includes `token` to use as `Authorization: Bearer <token>`

### Get Transactions by Account (Admin or Customer)
GET `/api/transactions/account/1234567890`

### Apply for Card (Customer)
POST `/api/cards/apply`
```json
{
  "userId": 1,
  "cardType": "GOLD"
}
```

---

## Export to PDF
You can export this Markdown file to PDF using your IDE's Markdown export, or run a tool like `pandoc`:

```
pandoc -s docs/API_Endpoints.md -o docs/API_Endpoints.pdf
```


