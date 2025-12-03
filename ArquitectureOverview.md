# TinyLedger Arquitecture Overview

## **Layered, Modular Design:**

### **API Layer:**
Uses Java’s built-in HttpServer to expose RESTful endpoints for account and transaction operations. Handlers like CreateAccountHandler, GetBalanceHandler, PostTransactionHandler, and TransferHandler process HTTP requests and responses.

### **Service Layer:**
Business logic is encapsulated in service interfaces and implementations:
- IAccountService / AccountService
- ITransactionService (with concrete classes for each transaction type: DepositServer, WithdrawalService, PaymentService, FeeService, AdjustmentService)
- ITransferService / TransferService
- ITransactionFactoryService / TransactionFactoryService (routes requests to the correct transaction service based on type)

### **Persistence Layer:**
Uses an in-memory repository (InMemoryLedgerRepository) backed by ConcurrentHashMap for accounts and transactions. This is thread-safe and suitable for demo or small-scale use.

### **Dependency Injection:**
All dependencies are wired using Spring 4.3 XML configuration (applicationContext.xml). This allows for modularity and easy swapping of implementations.

## **Transaction Routing & Factory Pattern:**
The TransactionFactoryService maintains a map of MovementType to the corresponding ITransactionService implementation. When a transaction request arrives, it delegates to the correct handler (deposit, withdrawal, payment, fee, adjustment) based on the type. This makes the system extensible—new transaction types can be added with minimal changes.

***

## **Concurrency & Thread Safety:**

### **Per-Account Locking:**
Each account has a dedicated ReentrantLock (stored in a ConcurrentHashMap). This ensures that concurrent operations on the same account (e.g., multiple deposits/withdrawals) are serialized, preventing race conditions and inconsistent balances.
- For transfers, both source and destination accounts are locked in a consistent order to avoid deadlocks.

### **Scalability Implications:**

#### **Vertical Scaling:**
The use of per-account locks allows the system to handle multiple requests in parallel, as long as they target different accounts. This means the system can efficiently utilize multi-core CPUs.

#### **Horizontal Scaling (Limitations):**
Since all data is in-memory and local to a single JVM, scaling out to multiple nodes (horizontal scaling) would require externalizing state (e.g., using a distributed database or cache) and distributed locking. The current design is best suited for single-node deployments or as a prototype.

***

## **Extensibility:**

### **Adding New Transaction Types:**
To support a new transaction type, implement a new ITransactionService, register it in the factory, and update the enum. No changes are needed to the API or core logic, demonstrating strong modularity.

### **Swappable Persistence:**
The repository interface (ILedgerRepository) allows for easy replacement of the in-memory store with a persistent database if needed.

***

## **Request Handling Flow:**
- 1 - HTTP Request arrives at an endpoint (e.g., /accounts, /transactions, /accounts/transfer).
- 2 - Handler parses the request, validates input, and calls the appropriate service.
- 3 - Service performs business logic, acquires necessary locks, updates state, and records transactions.
- 4 - Repository persists changes in memory.
- 5 - Response is serialized to JSON and returned to the client.

***

## **Scaling Considerations:**
- Efficient for concurrent requests as long as they target different accounts.
- Bottlenecked by single-node memory and JVM heap for large datasets or high throughput.
- Not distributed: To scale horizontally, would need to externalize state and coordinate locks across nodes.
- Easy to extend for new transaction types or persistence backends.

***

## **Scaling for real usage:**

### **Externalize State (Persistence Layer):**

#### **Replace In-Memory Storage:**
Move from ConcurrentHashMap-based in-memory repositories to a shared, external database (e.g., PostgreSQL, MySQL, or a distributed NoSQL store like Cassandra).

#### **Benefits:**
- All nodes see the same data.
- We can add/remove servers without losing data.

#### **Steps:**
- Implement a new ILedgerRepository that uses JDBC/JPA for SQL, or a NoSQL client.
- Migrate account and transaction data to the database.
- Ensure all service instances point to the same database.

### **Distributed Locking:**

#### **Why:**
Per-account ReentrantLock only works within a single JVM. In a distributed setup, we need to coordinate access across nodes.

#### **Options:**
- Database Row-Level Locks: Use SQL SELECT ... FOR UPDATE for critical sections.
- Distributed Lock Managers: Use Redis (RedLock), ZooKeeper, or etcd to acquire locks on account IDs before mutating balances.

#### **Steps:**
- Refactor service logic to acquire/release distributed locks before critical operations (deposits, withdrawals, transfers).
- Handle lock timeouts and failures gracefully.

### **Stateless Service Layer:**

#### **Why:**
Each instance should not keep any local state about accounts, transactions, or locks.

#### **How:**
- Remove any static or instance fields that cache data or locks.
- All stateful operations must go through the shared database or distributed cache.

### **API Gateway & Load Balancer:**
- Deploy behind a load balancer (e.g., NGINX, AWS ELB, Azure Application Gateway).
- All nodes should be stateless and able to handle any request.
- Session stickiness is not required if you follow the above steps.

### **Idempotency & Retry Logic:**

#### **Why:**
In distributed systems, network failures and retries can cause duplicate requests.

#### **How:**
Implement idempotency keys for transaction requests.
Ensure that repeated requests with the same key do not result in duplicate transactions.

### **Optional: Event-Driven Architecture:**
- For high throughput, consider using a message queue (Kafka, RabbitMQ) for transaction processing.
- Producers (API nodes) enqueue requests; consumers (workers) process and update the database.
- This decouples request handling from processing and can improve throughput and resilience.