-- ERASM Database Schema
-- NOTE: You do NOT need to run this manually. Hibernate creates/updates
-- these tables automatically on startup because application.properties has
-- spring.jpa.hibernate.ddl-auto=update
--
-- This script is provided as a deliverable (per the project's "SQL Script"
-- requirement) and as a reference for the actual structure created.

CREATE DATABASE IF NOT EXISTS erasm_db;
USE erasm_db;

CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    created_date DATETIME,
    modified_date DATETIME
);

CREATE TABLE IF NOT EXISTS employees (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    designation VARCHAR(255),
    user_id BIGINT NOT NULL UNIQUE,
    role_id BIGINT NOT NULL,
    total_allocation_percent INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE IF NOT EXISTS skills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS employee_skills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    skill_id BIGINT NOT NULL,
    level VARCHAR(50),
    years_of_experience INT,
    FOREIGN KEY (employee_id) REFERENCES employees(id),
    FOREIGN KEY (skill_id) REFERENCES skills(id)
);

CREATE TABLE IF NOT EXISTS certifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    issued_by VARCHAR(255),
    issue_date DATE,
    employee_id BIGINT NOT NULL,
    FOREIGN KEY (employee_id) REFERENCES employees(id)
);

CREATE TABLE IF NOT EXISTS projects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    client_name VARCHAR(255),
    start_date DATE,
    end_date DATE,
    technology_stack VARCHAR(500),
    budget DOUBLE,
    status VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS resource_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    skill_id BIGINT NOT NULL,
    required_count INT,
    status VARCHAR(50),
    created_by VARCHAR(255),
    modified_by VARCHAR(255),
    created_date DATETIME,
    modified_date DATETIME,
    FOREIGN KEY (project_id) REFERENCES projects(id),
    FOREIGN KEY (skill_id) REFERENCES skills(id)
);

CREATE TABLE IF NOT EXISTS allocations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    project_id BIGINT NOT NULL,
    allocation_percent INT,
    start_date DATE,
    end_date DATE,
    active BOOLEAN DEFAULT TRUE,
    created_by VARCHAR(255),
    created_date DATETIME,
    FOREIGN KEY (employee_id) REFERENCES employees(id),
    FOREIGN KEY (project_id) REFERENCES projects(id)
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    action VARCHAR(255),
    entity_name VARCHAR(255),
    entity_id VARCHAR(255),
    created_by VARCHAR(255),
    created_date DATETIME
);

-- Seed roles (also done automatically by DataInitializer.java on first app run)
INSERT IGNORE INTO roles (name) VALUES ('ADMIN'), ('DELIVERY_MANAGER'), ('RESOURCE_MANAGER'), ('EMPLOYEE'), ('AUDITOR');
