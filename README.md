# Student Life Assistance Application

A comprehensive desktop application designed to help students manage their academic life, including calendar management, user authentication, and personal organization tools.

## 🎯 Overview

The Student Life Assistance Application is a multi-module Java application built with Spring Boot backend and JavaFX frontend. It provides students with tools to manage their academic schedules, personal events, and daily activities through an intuitive desktop interface.

## 🏗️ Architecture

This project follows a modular architecture with three main components:

- **Frontend Module** (`student-app-frontend`): JavaFX-based desktop application
- **Backend Module** (`student-app-backend`): Spring Boot REST API server
- **Common Module** (`student-app-common`): Shared models and utilities

### Technology Stack

- **Backend**: Spring Boot 3.3.1, Spring Security, JWT Authentication
- **Frontend**: JavaFX 24.0.1, FXML for UI layouts
- **Database**: Microsoft SQL Server
- **Build Tool**: Maven
- **Java Version**: 23

## 🚀 Features

### Core Functionality
- **User Authentication**: Secure login and registration system with JWT tokens
- **Calendar Management**: Create, view, and manage calendar events
- **Event Types**: Support for different event categories (Meeting, Personal, Financial, Appointment, Other)
- **Meeting Links**: Store and manage meeting URLs for virtual events
- **User Profiles**: Individual user accounts with personalized data

### Event Management
- Create events with title, description, start/end times
- Categorize events by type
- Add meeting links for virtual meetings
- View events in a calendar interface
- Personal event organization

## 📁 Project Structure

```
student-life-assistance/
├── pom.xml                          # Parent Maven configuration
├── student-app-frontend/            # JavaFX desktop application
│   ├── src/main/java/
│   │   └── com/studentapp/frontend/
│   │       ├── controller/          # UI controllers
│   │       ├── client/              # API client
│   │       └── view/                # View components
│   └── src/main/resources/
│       └── com/studentapp/frontend/ # FXML files
├── student-app-backend/             # Spring Boot REST API
│   ├── src/main/java/
│   │   └── com/studentapp/backend/
│   │       ├── controller/          # REST controllers
│   │       ├── service/             # Business logic
│   │       ├── repository/          # Data access layer
│   │       └── security/            # Authentication & authorization
│   └── src/main/resources/
│       ├── application.properties   # Configuration
│       └── db/sql/                  # Database scripts
└── student-app-common/              # Shared models
    └── src/main/java/
        └── com/studentapp/common/
            └── model/               # Entity classes
```

## 🛠️ Prerequisites

Before running the application, ensure you have:

- **Java 23** or higher
- **Maven 3.6+**
- **Microsoft SQL Server** (running on localhost:1433)
- **Database**: `StudentLifeDB` (will be created automatically)

## ⚙️ Setup Instructions

### 1. Database Setup

1. Install and start Microsoft SQL Server
2. The application will automatically create the database and tables on first run
3. Mock data will be inserted automatically

### 2. Configuration

1. Update `student-app-backend/src/main/resources/application.properties`:
   - Modify database connection settings if needed
   - Update JWT secret key for production use

### 3. Building the Project

```bash
# Build all modules
mvn clean install

# Build specific modules
mvn clean install -pl student-app-backend
mvn clean install -pl student-app-frontend
mvn clean install -pl student-app-common
```

### 4. Running the Application

#### Start the Backend Server
```bash
cd student-app-backend
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

#### Start the Frontend Application
```bash
cd student-app-frontend
mvn javafx:run
```


## 📝 License

This project is developed for educational purposes.

---
