# Credit Module - Loan API (Spring Boot)

This is a backend loan management API for a bank. It allows bank employees to create, list, and pay customer loans.

---

## Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA
- Spring Security (Basic Auth)
- H2 In-Memory Database
- JUnit / Mockito

---

## Authentication

All endpoints require **Basic Auth**.

| Username | Password |
|----------|----------|
| admin    | admin123 |

---

## Endpoints

### Loan

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/loans` | Create a new loan |
| `GET`  | `/api/loans/customer/{customerId}` | Get all loans for a customer |
| `GET`  | `/api/loans/{loanId}/installments` | Get all installments of a loan |

### Payment

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/payments` | Pay installments for a loan |

---

## How to Run

```bash
./mvnw spring-boot:run
