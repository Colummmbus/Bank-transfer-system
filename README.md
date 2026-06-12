# Bank Transfer System
A banking transfer system built with Java Swing, JDBC, and MySQL.

## Features
- User registration and login
- Account creation and management
- Secure bank transfers with account password verification
- Balance validation
- Transfer limit validation
  - Per-transfer limit
  - Daily transfer limit
- Transaction history lookup
- Exception handling for various banking scenarios

## Tech Stack
- Java 21
- Java Swing
- JDBC
- MySQL
- Git / GitHub

## Project Structure

```text
src
├─ dao
├─ domain
├─ exception
├─ gui
├─ main
├─ service
└─ util
```

## Database Design
### Users
- user_id
- login_id
- password
- name
### Accounts
- account_id
- user_id
- account_number
- balance
- account_password
- status
- one_time_limit
- daily_limit
- created_at
### Transactions
- transaction_id
- from_account_id
- to_account_id
- amount
- type
- t_status
- t_created_at
### Screenshots
- Login
- Sign Up
- Home
- Account Inquiry
- Transfer
- Transaction History
### Future Improvements
- Password encryption (BCrypt)
- Account lock policy
- Administrator dashboard
- JUnit testing
- Spring Boot migration
