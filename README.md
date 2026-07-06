# MoneyMap – Personal Expense Tracker

A clean, beginner-friendly desktop app built with **Java Swing** and **MySQL**.
Track income and expenses and see your current balance at a glance — no
logins, no charts, no clutter.

## Features

- Add Income / Add Expense (amount only — date is stamped automatically)
- Current Balance, calculated live as `SUM(Income) - SUM(Expense)`
- Full transaction history in a table, newest first
- Input validation with friendly error dialogs

## Project Structure

```
MoneyMap/
├── database_setup.sql
├── lib/                        <- put mysql-connector-j-x.x.x.jar here
└── src/
    ├── Main.java
    ├── database/
    │   └── DBConnection.java   <- connection + auto table creation
    ├── model/
    │   └── Transaction.java    <- plain data model
    ├── service/
    │   └── TransactionService.java  <- all SQL (PreparedStatement only)
    └── ui/
        ├── MainFrame.java      <- the whole Swing UI
        └── RoundedButton.java  <- small custom rounded button
```

## 1. Set up the database

Make sure MySQL Server is running, then run:

```bash
mysql -u root -p < database_setup.sql
```

This creates the `MoneyMap` database and `transactions` table. (The app
will also create the table itself on first run if it's missing, but the
database itself must already exist.)

## 2. Get the MySQL JDBC driver

Download **mysql-connector-j** (the MySQL Connector/J `.jar`) from
https://dev.mysql.com/downloads/connector/j/ and place the `.jar` file
inside the `lib/` folder.

## 3. Configure your credentials

Open `src/database/DBConnection.java` and update `USERNAME` / `PASSWORD`
(and `URL` if your MySQL isn't on `localhost:3306`) to match your setup.

## 4. Compile and run

From the project root:

```bash
# Compile
javac -cp "lib/*" -d out $(find src -name "*.java")

# Run (include the driver jar on the classpath)
java -cp "out:lib/*" Main
```

On Windows, use a semicolon instead of a colon in the classpath:
`java -cp "out;lib/*" Main`

Or simply open the folder as a project in **NetBeans** or **IntelliJ IDEA**,
add the connector jar as a library, and run `Main.java`.

## Notes

- The balance is never stored — it's recalculated from the transactions
  table every time, using SQL `SUM()`.
- All queries use `PreparedStatement`, never a plain `Statement`.
- Intentionally simple by design: no login, no categories, no charts, no
  edit/delete — just add and view.
