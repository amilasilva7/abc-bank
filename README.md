# ABC Bank API

A Spring Boot REST API for banking operations with account management, transactions, and exchange rate lookups.

## Setup

### Prerequisites
- Java 17+

### Run
```bash
./gradlew bootRun
```
API runs on `http://localhost:8080`

## Features

- **Account Management**: Create, read, update, delete accounts (SAVINGS/CURRENT types)
- **Transactions**: Credit/debit operations with search, filtering, and pagination
- **Transaction Statements**: Filter by account type and date range
- **Exchange Rates**: Real-time rates from external API
- **Global Error Handling**: Centralized error responses for 404, 400, 500 errors
- **AOP Logging**: Automatic request/response logging with execution time

## API Endpoints

| Method | Endpoint                          | Purpose |
|--------|-----------------------------------|---------|
| GET/POST | `/api/v1/accounts`                | Account CRUD |
| PATCH | `/api/v1/accounts/{id}/status`    | Update account status |
| GET | `/api/v1/transactions`            | List transactions (paginated) |
| GET | `/api/v1/transactions/search`        | Advanced search with filters |
| GET | `/api/v1/transactions/statement`     | Get statement by account type |
| GET | `/api/v1/transactions/exchange-rate` | Get exchange rates |

## Future Improvements

- **Authentication & Authorization**: JWT-based security
- **Database Sequence**: Use native DB sequences for guaranteed unique account number generation
- **Performance**: Add caching for exchange rates, index frequently searched columns
- **Testing**: Comprehensive integration and performance tests
- **Add DB indexing**: For fast reads
- **Mask Sensitive data**: Mask sensitive information
