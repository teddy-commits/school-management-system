# 🎓 University Management System

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://java.com)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-orange.svg)](https://mysql.com)
[![JWT](https://img.shields.io/badge/JWT-Authentication-red.svg)](https://jwt.io)
[![License](https://img.shields.io/badge/License-Educational%20Use%20Only-yellow.svg)](LICENSE)
[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)]()

> **A comprehensive University Management System built with Spring Boot for graduation project**

## 📋 Table of Contents
- [Overview](#-overview)
- [Features](#-features)
- [System Architecture](#-system-architecture)
- [Technology Stack](#-technology-stack)
- [Project Structure](#-project-structure)
- [Database Schema](#-database-schema)
- [Installation Guide](#-installation-guide)
- [API Documentation](#-api-documentation)
- [Testing](#-testing)
- [Security](#-security)
- [Future Enhancements](#-future-enhancements)
- [Author](#-author)
- [Acknowledgments](#-acknowledgments)
- [License](#-license)

## 📖 Overview

This **University Management System** is a full-stack application designed to streamline academic and administrative operations for universities. The system provides three main modules:

| Module | Description |
|--------|-------------|
| **Registration Module** | Student enrollment, user management, role-based access |
| **Grading Module** | Course management, grade submission, transcript generation |
| **Finance Module** | Fee management, payment processing, financial reporting |

### 🎯 Project Goals
- ✅ Centralized management for all university stakeholders
- ✅ Secure authentication and role-based authorization
- ✅ Scalable architecture for future expansion
- ✅ Real-world application of Spring Boot best practices

## ✨ Features

### 👨‍🎓 Registration Module
```yaml
Student Features:
  - Self-registration with auto-generated Student ID
  - Profile management and updates
  - View personal information and enrollment status

Admin Features:
  - Create instructors, academic administrators, management staff
  - Activate/deactivate user accounts
  - View user statistics and reports
  - Search and filter users by role, department

Supported Roles:
  - STUDENT | INSTRUCTOR | PROFESSOR
  - ACADEMIC_ADMINISTRATOR | HOD | DEAN | REGISTRAR
  - MANAGEMENT | FINANCE_MANAGER | HR_MANAGER
  - ADMIN | SUPER_ADMIN
Course Management:
  - Create, update, delete courses
  - Set course details (credits, department, schedule)
  - Manage course capacity and enrollment

Enrollment System:
  - Student self-enrollment in courses
  - Prerequisite checking
  - Course withdrawal

Grading System:
  - Grade submission (A+ to F)
  - Automatic GPA calculation (4.0 scale)
  - CGPA computation
  - Transcript generation
  - Grade publishing workflow
Fee Structure:
  - Create fee structures by department/faculty
  - Define fee categories (Tuition, Library, Lab, etc.)
  - Set due dates and late fee penalties

Payment Processing:
  - Full and partial payments
  - Multiple payment methods (Bank, Card, Mobile, Cash)
  - Automatic receipt generation
  - Payment history tracking

Financial Reports:
  - Daily, monthly, semester reports
  - Department-wise collection reports
  - Overdue fee tracking
  - Fee waiver management
┌─────────────────────────────────────────────────────────────────┐
│                         Client Layer                            │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐             │
│  │ React App   │  │ Postman     │  │ Mobile App  │             │
│  │ (Frontend)  │  │ API Testing │  │ (Planned)   │             │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘             │
├─────────┼────────────────┼────────────────┼─────────────────────┤
│         │                │                │                     │
│         ▼                ▼                ▼                     │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │              REST API Layer (Spring Boot)               │   │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐             │   │
│  │  │ Controllers│  │ Services │  │ Repositories│         │   │
│  │  └──────────┘  └──────────┘  └──────────┘             │   │
│  └─────────────────────────────────────────────────────────┘   │
│         │                │                │                     │
├─────────┼────────────────┼────────────────┼─────────────────────┤
│         │                │                │                     │
│         ▼                ▼                ▼                     │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                    Security Layer                        │   │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐             │   │
│  │  │ JWT Auth │  │  Role    │  │ Password │             │   │
│  │  │ Filter   │  │ Check    │  │ Encoder  │             │   │
│  │  └──────────┘  └──────────┘  └──────────┘             │   │
│  └─────────────────────────────────────────────────────────┘   │
│         │                                                     │
├─────────┼─────────────────────────────────────────────────────┤
│         ▼                                                     │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │              Database Layer (MySQL)                      │   │
│  │  ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐          │   │
│  │  │ Users  │ │Courses │ │  Fees  │ │Payments│          │   │
│  │  └────────┘ └────────┘ └────────┘ └────────┘          │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
-- Users table (unified for all roles)
users (
    id, first_name, last_name, email, password, role,
    student_id, employee_id, department, faculty, cgpa,
    designation, is_active, created_at, updated_at
)

-- Courses table
courses (
    id, course_code, course_name, credits, department,
    semester, academic_year, instructor_email, max_students
)

-- Enrollments table
enrollments (
    id, student_id, course_id, enrollment_date, status
)

-- Grades table
grades (
    id, student_id, course_id, score, grade_letter, grade_point
)

-- Fee structures table
fee_structures (
    id, fee_type, description, amount, department, due_date
)

-- Fees table
fees (
    id, student_id, fee_structure_id, amount, paid_amount, status
)

-- Payments table
payments (
    id, transaction_id, student_id, amount, payment_method, status
)

-- Invoices table
invoices (
    id, invoice_number, student_id, total_amount, due_date, status
)

# Required installations
Java 17 or higher
MySQL 8.0 or higher
Maven 3.9 or higher
Git (optional)

git clone https://github.com/teddy-commit/university-management-system.git
cd university-management-system

-- Create database
CREATE DATABASE school_management_db;
USE school_management_db;

-- Database will auto-create tables on first run


# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/school_management_db
spring.datasource.username=root
spring.datasource.password=your_password

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT Configuration
app.jwt.secret=your_super_secure_secret_key_at_least_64_characters_long
app.jwt.expiration=86400000

# Server Configuration
server.port=8080
server.servlet.context-path=/api/v1