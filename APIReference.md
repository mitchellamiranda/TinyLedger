Here’s your **enhanced TinyLedger API Reference** with support for all transaction types—including payments, transfers, fees, and adjustments—plus example `curl` commands for each. This version is ready for your updated backend!

***

# TinyLedger API Reference

## **Introduction**

TinyLedger is a lightweight ledger system for managing accounts and recording transactions. The API supports creating accounts, checking balances, recording various transaction types, listing transaction history, and transferring funds between accounts.

### **Base URL**

    http://localhost:8080/api/v1

***

## **Usage Notes**

*   **Authentication:**  
    No authentication required (demo mode). For production, add token-based authentication.
*   **Content-Type:**  
    All requests/responses use `application/json`.
*   **UUIDs:**  
    Accounts and transactions are uniquely identified by UUID.
*   **Error Handling:**
    *   `400 Bad Request` – Invalid input or missing parameters.
    *   `404 Not Found` – Account or transaction not found.
    *   `500 Internal Server Error` – Unexpected server error.
*   **Currencies:**  
    Supported: `EUR`, `USD`, `GBP` (ISO 4217).

***

## **Endpoints**

### **Create Account**

**Endpoint:**  
`POST /accounts`

**Request Body:**

```json
{
  "name": "Alice",
  "currency": "EUR",
  "initialBalance": 1000.00
}
```

**Response:**

```json
{
  "accountId": "<UUID>",
  "currency": "EUR",
  "balance": 1000.0,
  "name": "Alice"
}
```

**Example `curl`:**

```bash
curl -X POST http://localhost:8080/api/v1/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Alice",
    "currency": "EUR",
    "initialBalance": 1000.00
  }'
```

***

### **Get Balance**

**Endpoint:**  
`GET /accounts/balance?id=<UUID>`

**Response:**

```json
{
  "accountId": "<UUID>",
  "balance": 1050.0
}
```

**Example `curl`:**

```bash
curl -X GET "http://localhost:8080/api/v1/accounts/balance?id=<UUID>"
```

***

### **Record Transaction**

**Endpoint:**  
`POST /transactions?accountId=<UUID>`

**Request Body:**  
`type` can be one of: `DEPOSIT`, `WITHDRAWAL`, `PAYMENT`, `FEE`, `ADJUSTMENT`

```json
{
  "type": "PAYMENT",
  "amount": 75.00,
  "currency": "EUR"
}
```

**Response:**

```json
{
  "transactionId": "<UUID>",
  "accountId": "<UUID>",
  "type": "PAYMENT",
  "amount": 75.0,
  "currency": "EUR",
  "occurredAt": "2025-12-01T12:34:56Z"
}
```

**Example `curl`:**

```bash
curl -X POST "http://localhost:8080/api/v1/transactions?accountId=<UUID>" \
  -H "Content-Type: application/json" \
  -d '{
    "type": "PAYMENT",
    "amount": 75.00,
    "currency": "EUR"
  }'
```

***

### **List Transactions**

**Endpoint:**  
`GET /accounts/transactions?id=<UUID>`

**Response:**

```json
{
  "accountId": "<UUID>",
  "transactions": [
    {
      "id": "<UUID>",
      "type": "DEPOSIT",
      "amount": 50.0,
      "currency": "EUR",
      "occurredAt": "2025-12-01T12:34:56Z"
    },
    {
      "id": "<UUID>",
      "type": "PAYMENT",
      "amount": 75.0,
      "currency": "EUR",
      "occurredAt": "2025-12-01T13:00:00Z"
    }
  ]
}
```

**Example `curl`:**

```bash
curl -X GET "http://localhost:8080/api/v1/accounts/transactions?id=<UUID>"
```

***

### **Transfer Funds**

**Endpoint:**  
`POST /accounts/transfer`

**Request Body:**

```json
{
  "sourceAccountId": "<UUID>",
  "destinationAccountId": "<UUID>",
  "amount": 100.00,
  "currency": "EUR"
}
```

**Response:**

```json
{
  "transactionId": "<UUID>",
  "sourceAccountId": "<UUID>",
  "destinationAccountId": "<UUID>",
  "amount": 100.0,
  "currency": "EUR"
}
```

**Example `curl`:**

```bash
curl -X POST http://localhost:8080/api/v1/accounts/transfer \
  -H "Content-Type: application/json" \
  -d '{
    "sourceAccountId": "<UUID>",
    "destinationAccountId": "<UUID>",
    "amount": 100.00,
    "currency": "EUR"
  }'
```

***

## **Supported Transaction Types**

*   `DEPOSIT` – Add funds to an account
*   `WITHDRAWAL` – Remove funds from an account
*   `PAYMENT` – Record a payment (removes funds)
*   `TRANSFER` – Move funds between accounts
*   `FEE` – Deduct a fee
*   `ADJUSTMENT` – Adjust balance (positive or negative)

***
