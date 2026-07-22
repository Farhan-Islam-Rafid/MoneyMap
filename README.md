<div align="center">

# 💰 MoneyMap v2.0

### Smart Personal Finance Management System to Track, Control, and Grow Your Money

[![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/)
[![Swing](https://img.shields.io/badge/GUI-Java%20Swing-2f7dd1?style=for-the-badge&logo=java&logoColor=white)](https://docs.oracle.com/javase/tutorial/uiswing/)
[![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![JDBC](https://img.shields.io/badge/Connectivity-JDBC-6DB33F?style=for-the-badge&logo=java&logoColor=white)](https://docs.oracle.com/javase/tutorial/jdbc/)
[![Maven](https://img.shields.io/badge/Build-Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)](#license)

</div>

---

## 📖 Table of Contents

- [About The Project](#-about-the-project)
- [Features](#-features)
- [Screenshots](#-screenshots)
- [Technology Stack](#-technology-stack)
- [System Architecture](#-system-architecture)
- [Project Structure](#-project-structure)
- [Database Design](#-database-design)
- [Installation Guide](#-installation-guide)
- [Database Configuration](#-database-configuration)
- [Future Improvements](#-future-improvements)
- [Developer](#-developer)
- [License](#-license)

---

## 📌 About The Project

**MoneyMap** is a desktop-based personal finance management application built with **Java Swing** and powered by a **PostgreSQL** database. It was created to give individuals a simple, secure, and organized way to take control of their financial life — without relying on complicated spreadsheets or third-party cloud services that raise privacy concerns.

Managing money is one of the most overlooked yet essential parts of daily life. Many people lose track of where their money goes each month simply because they lack a structured, easy-to-use tool. MoneyMap solves this problem by providing a clean desktop interface where users can log in securely, record their income and expenses, and instantly visualize their financial standing.

### Why MoneyMap Was Created

- To provide a **lightweight, offline-friendly** alternative to bloated finance apps.
- To demonstrate a complete **full-stack desktop application** using Java, JDBC, and PostgreSQL following clean software architecture principles.
- To help users build **healthy financial habits** through consistent tracking and monthly insights.

### How It Helps Users

- Provides a **real-time balance** based on recorded income and expenses.
- Offers **monthly comparisons** so users can understand spending trends.
- Maintains a full **transaction history** with search and filtering.
- Automatically **archives** past months for long-term record keeping.
- Keeps each user's data **private and isolated** through secure authentication.

### Real-World Use Cases

- 🎓 Students managing monthly allowances and part-time income.
- 🏠 Individuals/families tracking household expenses.
- 💼 Freelancers monitoring project-based income and spending.
- 📊 Anyone who wants a personal, private finance dashboard without a subscription-based cloud app.

---

## ✨ Features

| Category | Feature |
|---|---|
| 🔐 | **Secure User Authentication** — encrypted credential handling for safe access |
| 📝 | **Register and Login System** — simple onboarding for new and returning users |
| 👤 | **User-specific data management** — every user sees only their own records |
| 💵 | **Income tracking** — log every source of income with categories and dates |
| 💸 | **Expense tracking** — record daily, weekly, or monthly expenses |
| 📊 | **Current Balance calculation** — real-time computed balance from all transactions |
| 📅 | **Monthly financial overview** — clear breakdown of the current month's activity |
| 📈 | **Previous month comparison** — visualize whether spending increased or decreased |
| 📆 | **Last 12 months financial history** — long-term trend visibility |
| 🗃️ | **Automatic monthly archive system** — past months are archived without manual effort |
| ✏️ | **Edit/Delete transactions** — full control to correct or remove entries |
| 🔎 | **Search and filtering** — quickly locate transactions by date, type, or category |
| 📋 | **Transaction history table** — organized, sortable tabular view of all records |
| 🎨 | **Modern Java Swing UI** — clean, intuitive, and responsive desktop interface |

---

## 🖼️ Screenshots

> Add your application screenshots below by placing image files in a `screenshots/` folder and linking them, e.g. `![Login Page](screenshots/login.png)`

| Screen | Description |
|---|---|
| 🔑 **Login Page** | User authentication screen |
| 📝 **Register Page** | New user registration form |
| 📊 **Dashboard** | Financial overview and current balance |
| ➕ **Add Transaction Page** | Form to log income or expenses |
| 📋 **Transaction History** | Searchable table of all past transactions |

---

## 🛠️ Technology Stack

| Layer | Technology |
|---|---|
| 🎨 **Frontend** | Java Swing |
| ⚙️ **Backend** | Java |
| 🗄️ **Database** | PostgreSQL |
| 🔌 **Connectivity** | JDBC |
| 📦 **Build Tool** | Maven |
| 📀 **Packaging** | JAR / EXE |

---

## 🏗️ System Architecture

MoneyMap follows a clean, layered **MVC-inspired architecture** that separates concerns and keeps the codebase maintainable and scalable.

```
        ┌────────────────────┐
        │      UI Layer       │   ← Java Swing (Login, Register, Dashboard, etc.)
        └─────────┬──────────┘
                  │
        ┌─────────▼──────────┐
        │   Service Layer     │   ← Business logic (Auth, Transactions, Calculations)
        └─────────┬──────────┘
                  │
        ┌─────────▼──────────┐
        │   Database Layer     │   ← DBConnection, DAO / Data Access classes
        └─────────┬──────────┘
                  │
        ┌─────────▼──────────┐
        │  PostgreSQL Database │   ← Persistent data storage
        └────────────────────┘
```

**Layer Responsibilities:**

- **UI Layer (`ui/`)** — Handles all user interaction: login, registration, dashboard rendering, and forms. Contains no business logic.
- **Service Layer (`service/`)** — Contains the core business logic: validating input, calculating balances, generating monthly summaries, and managing archiving rules.
- **Model Layer (`model/`)** — Plain Java objects representing entities such as `User` and `Transaction`.
- **Database Layer (`database/`)** — Manages the JDBC connection and low-level database operations.
- **Session Layer (`session/`)** — Maintains the currently logged-in user's context across the application.
- **Utils Layer (`utils/`)** — Shared helper functions (formatting, validation, date handling).

This separation ensures that UI changes never affect business logic, and database changes never leak into the presentation layer — a hallmark of maintainable, production-grade software.

---

## 📁 Project Structure

```
MoneyMap/
│
├── src/
│   ├── database/
│   │   ├── DBConnection.java
│   │   └── DatabaseInitializer.java
│   │
│   ├── model/
│   │
│   ├── service/
│   │
│   ├── session/
│   │
│   ├── ui/
│   │   ├── LoginFrame.java
│   │   ├── RegisterFrame.java
│   │   └── MainFrame.java
│   │
│   ├── utils/
│   │
│   └── Main.java
│
├── pom.xml
└── README.md
```

---

## 🗄️ Database Design

MoneyMap uses **PostgreSQL** as its relational database engine, chosen for its reliability, ACID compliance, and strong support for structured financial data.

### Core Tables

**`users` Table**
Stores registered user credentials and profile information. Each user is uniquely identified and their password is stored securely (hashed) rather than in plain text.

| Column | Type | Description |
|---|---|---|
| `id` | SERIAL (PK) | Unique user identifier |
| `username` | VARCHAR | Unique login name |
| `password` | VARCHAR | Securely hashed password |
| `created_at` | TIMESTAMP | Account creation date |

**`transactions` Table**
Stores every income and expense entry, linked to the user who created it.

| Column | Type | Description |
|---|---|---|
| `id` | SERIAL (PK) | Unique transaction identifier |
| `user_id` | INTEGER (FK → users.id) | Owner of the transaction |
| `type` | VARCHAR | `INCOME` or `EXPENSE` |
| `amount` | NUMERIC | Transaction amount |
| `category` | VARCHAR | e.g., Food, Salary, Rent |
| `description` | TEXT | Optional note |
| `transaction_date` | DATE | Date of the transaction |

### Relationship

- A **one-to-many** relationship exists between `users` and `transactions` — one user can have many transactions, but each transaction belongs to exactly one user (`user_id` foreign key).
- This ensures **strict data isolation**: users can only ever query and view their own financial records.

### Secure Connectivity

MoneyMap connects to PostgreSQL using **JDBC** through a centralized `DBConnection` class, which:

- Loads credentials from a configuration source rather than hardcoding them in the UI.
- Uses parameterized/prepared statements for all queries to prevent **SQL injection**.
- Manages connection lifecycle properly (open, use, close) to avoid resource leaks.

---

## ⚙️ Installation Guide

### Prerequisites

Make sure you have the following installed:

- ☕ **Java JDK** (17 or higher recommended)
- 🐘 **PostgreSQL** (13 or higher)
- 📦 **Maven** (3.8 or higher)

### Setup Steps

**1. Clone the repository**
```bash
git clone https://github.com/your-username/MoneyMap.git
cd MoneyMap
```

**2. Configure the PostgreSQL database**
```sql
CREATE DATABASE moneymap;
```

**3. Update database credentials**

Open `DBConnection.java` (or your `.properties` / config file) and update the connection details to match your local PostgreSQL setup — see [Database Configuration](#-database-configuration) below.

**4. Install dependencies**
```bash
mvn clean install
```

**5. Run the project**
```bash
mvn exec:java -Dexec.mainClass="Main"
```

Or run the generated JAR file directly:
```bash
java -jar target/MoneyMap.jar
```

---

## 🔧 Database Configuration

Update your database connection settings as follows:

```properties
Database URL : jdbc:postgresql://localhost:5432/moneymap
Username     : your_username
Password     : your_password
```

> ⚠️ **Security Tip:** Never commit real database credentials to a public repository. Use environment variables or a `.gitignore`-protected config file in production.

---

## 🚀 Future Improvements

- ☁️ **Cloud database support** — sync data across devices
- 📱 **Mobile application** — companion app for on-the-go tracking
- 📊 **Advanced analytics dashboard** — deeper insights with charts and graphs
- 🤖 **AI spending prediction** — forecast future expenses using historical data
- 🔔 **Expense reminder notifications** — alerts for bills and budget limits

---

## 👨‍💻 Developer

**Farhan Islam Rafid**
*Computer Science & Technology Student*

**Skills:** Java · PostgreSQL · JDBC · Swing · Web Development · Problem Solving

[![GitHub](https://img.shields.io/badge/GitHub-Profile-181717?style=for-the-badge&logo=github)](https://github.com/your-username)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-0077B5?style=for-the-badge&logo=linkedin)](https://linkedin.com/in/your-profile)

---

## 📄 License

This project is licensed under the **MIT License** — feel free to use, modify, and distribute it with attribution.

```
MIT License © 2026 Farhan Islam Rafid
```

<div align="center">

### ⭐ If you found this project useful, consider giving it a star!

</div>