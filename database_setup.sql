-- MoneyMap database setup
-- Run this once in MySQL before starting the application.
-- (The app will also auto-create the table itself if it's missing,
--  but you must create the database first.)

CREATE DATABASE IF NOT EXISTS MoneyMap;

USE MoneyMap;

CREATE TABLE IF NOT EXISTS transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(20) NOT NULL,
    amount DOUBLE NOT NULL,
    transaction_date DATE NOT NULL
);
