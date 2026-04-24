-- Registration Process Database Schema

CREATE DATABASE IF NOT EXISTS registration_db;

USE registration_db;

CREATE TABLE IF NOT EXISTS users (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(100)  NOT NULL,
    email    VARCHAR(150)  NOT NULL UNIQUE,
    phone    VARCHAR(15),
    password VARCHAR(255)  NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Sample data: passwords should be hashed by the application (PBKDF2WithHmacSHA256 + salt)
-- before inserting. The rows below are for structural reference only; replace with
-- application-generated hashed values before use.
INSERT INTO users (name, email, phone, password) VALUES
    ('Alice Johnson', 'alice@example.com', '9876543210', '<app-hashed-password>'),
    ('Bob Smith',     'bob@example.com',   '9123456780', '<app-hashed-password>');

-- View all registered users
SELECT * FROM users;
