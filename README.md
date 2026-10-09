# 🌿 Verdant — Online Gardening Community Platform

**Verdant** is a Java-based web application designed to bring gardening enthusiasts together in one place. Users can explore gardening tips, participate in community discussions, share garden projects, and access plant-care guidance through a modern, responsive interface.

The project demonstrates the practical application of Java programming, object-oriented design, database integration, and web technologies using the Spring Boot framework.

## ✨ Features

- **Gardening Tips:** Browse and manage useful gardening tips.
- **Community Discussions:** Create questions and share knowledge about gardening.
- **Garden Projects:** Explore and showcase gardening projects.
- **Plant Advisor:** Provide plant-care guidance through specialized advisor classes.
- **Database Management:** Persist and retrieve application data using H2, Spring Data JPA, and JDBC.
- **Exception Handling:** Handle application errors through custom exceptions and centralized exception handling.
- **Web Integration:** Serve dynamic pages using Spring MVC and Thymeleaf.
- **Animated Interface:** Includes cloud-inspired WebGL visuals and a scrolling garden-reveal effect.

## 🛠️ Technology Stack

| Technology | Purpose |
|---|---|
| Java | Core programming language |
| Spring Boot | Application framework |
| Spring MVC | Web request handling |
| Thymeleaf | Dynamic HTML rendering |
| Spring Data JPA | Database persistence |
| Hibernate | Object-relational mapping |
| JDBC | Direct database connectivity |
| H2 Database | Data storage |
| Jakarta Servlet API | Request and response filtering |
| HTML, CSS, JavaScript | User interface |
| WebGL / GLSL | Animated visual effects |
| Maven | Build and dependency management |
| JUnit | Automated testing |

## 🏗️ Project Architecture

Verdant uses a layered architecture to separate responsibilities and make the application easier to maintain.

- **Controller Layer:** Handles incoming web requests and returns appropriate views or responses.
- **Service Layer:** Contains application logic and coordinates operations.
- **Repository / DAO Layer:** Manages data access using Spring Data JPA and JDBC.
- **Model Layer:** Represents the application's entities and domain objects.
- **Advisor Layer:** Organizes plant-care guidance through specialized advisor classes.
- **View Layer:** Uses Thymeleaf templates to render the web interface.
- **Static Resources:** Contains stylesheets, JavaScript, and animation assets.

## 🚀 Getting Started

### Prerequisites

- Java Development Kit (JDK)
- Git
- Maven, or the Maven Wrapper included in the project

### 1. Clone the repository

```bash
git clone https://github.com/AsHura-Wnd/Verdant.git
cd Verdant
```

> Replace the repository URL and directory name if your GitHub repository uses a different name.

### 2. Run the application

**Windows — PowerShell**

```powershell
.\mvnw.cmd spring-boot:run
```

**macOS / Linux**

```bash
./mvnw spring-boot:run
```

Alternatively, if Maven is installed:

```bash
mvn spring-boot:run
```

### 3. Open the application

Once the application starts, open:

**http://localhost:8080**

### Database

The application is configured to use an H2 database with file-based storage. Database settings are maintained in the application configuration.

For a local development setup, check the database configuration before running the application from a different working directory.

## 🧭 Main Application Routes

| Route | Description |
|---|---|
| `/` | Home page |
| `/tips` | Browse gardening tips |
| `/discussions` | View community discussions |
| `/projects` | Explore garden projects |
| `/about` | About the platform |
| `/tips/new` | Create a gardening tip |
| `/discussions/new` | Start a discussion |
| `/projects/new` | Create a garden project |

## 📚 Core Java Concepts

The project incorporates Java concepts relevant to the academic evaluation of web-based applications.

- **Object-Oriented Programming:** Organizes application functionality into classes with clear responsibilities.
- **Inheritance and Polymorphism:** Uses specialized plant-advisor classes based on a common advisor abstraction.
- **Generics:** Supports reusable, type-safe data structures.
- **Exception Handling:** Uses custom exceptions and centralized exception handling.
- **JDBC:** Demonstrates direct database access through Java database connectivity APIs.
- **Database Access Classes:** Separates database operations from other application logic.
- **Servlet Integration:** Uses a servlet filter for HTTP request and response processing.

## 🧪 Running Tests

Run the project's automated tests with the Maven Wrapper.

**Windows**

```powershell
.\mvnw.cmd test
```

**macOS / Linux**

```bash
./mvnw test
```

## 📋 Project Objectives

- Build a functional web application using Java and Spring Boot.
- Apply object-oriented programming principles to a practical project.
- Integrate a database for persistent data management.
- Demonstrate JDBC-based database operations.
- Implement web request handling and servlet-based integration.
- Develop a clear, maintainable, and user-friendly interface.

## 🔮 Future Enhancements

- User registration and authentication
- User profiles and personalized plant recommendations
- Garden-project image uploads
- Advanced search and filtering
- Plant-care reminders and seasonal guidance
- Deployment to a public hosting platform

## 👨‍💻 Project Information

- **Project Name:** Verdant
- **Project Type:** Java Web-Based Application
- **Domain:** Gardening and Community
- **Backend:** Java and Spring Boot
- **Database:** H2
- **Build Tool:** Maven

---

*Verdant — Grow knowledge. Share nature.*
