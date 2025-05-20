# LoanManagementSystem API (Spring Boot)

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

##  Authentication

The API uses HTTP Basic Auth with two types of users:

###  Admin

- **Username** and **Password** are provided via environment variables:
    - `ADMIN_USERNAME`
    - `ADMIN_PASSWORD`

> Example:
> ```bash
> export ADMIN_USERNAME=admin
> export ADMIN_PASSWORD=a15ec071-34e2-4afc-8e1b-8147a0f40017
> ```
Default values are provided in application.properties file.

###  Customer

- For simplicity customer username and password are already defined. 
- Customers log in with:
    - **Username** = `customer{id}` (for example customer with id 12: `customer12`)
    - **Password** = their assigned `uuid` (generated when the customer is created)

> Example:
> ```json
> {
>   "name": "customer23",
>   "uuid": "a15ec071-34e2-4afc-8e1b-8147a0f40017"
> }
> ```
> Base64 encode: `customer23:a15ec071...` and use in Basic Auth.

--

## Endpoints

### Customer

| Method | Endpoint              | Role              | Description             |
| ------ | --------------------- |-------------------| ----------------------- |
| POST   | `/api/customers`      | ADMIN            | Register a new customer |
| GET    | `/api/customers/{id}` | ADMIN or CUSTOMER | Get a specific customer |
| GET    | `/api/customers`      | ADMIN             | List all customers      |


### Loan

| Method | Endpoint                           | Role           | Description                                |
| ------ | ---------------------------------- | -------------- |--------------------------------------------|
| POST   | `/api/loans`                       | ADMIN or CUSTOMER | Create a loan (only for self if CUSTOMER)  |
| GET    | `/api/loans/customer/{id}`         | ADMIN or CUSTOMER | List loans for a customer                  |
| GET    | `/api/loans/{loanId}/installments` | ADMIN or CUSTOMER | List loan installments                     |



### Payment

| Method | Endpoint        | Role           | Description                 |
| ------ | --------------- | -------------- | --------------------------- |
| POST   | `/api/payments` | ADMIN or CUSTOMER | Pay installments for a loan |

---

## Access Control Rules

| Role     | Permissions                                                    |
| -------- | -------------------------------------------------------------- |
| ADMIN    | Full access to all customer and loan data                      |
| CUSTOMER | Can only access their own data, enforced by UUID and ID checks |


## How to Run

```bash
./mvnw spring-boot:run


```

##  Running Tests

###  Unit Tests
To run all unit tests:

```bash
./mvnw test
```
###  Local Tests
To test APIs in local you can import the Postman collection `loan-api.postman_collection.json` and run the tests there.
Update the authorization tab for each request to use Basic Auth

###  Packaging the App
To build a standalone .jar:

```bash
./mvnw clean package
```

Then run:

```bash
java -jar target/LoanManagementSystem-*.jar
```

App will start on http://localhost:8080

---