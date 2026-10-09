# 🌿 Verdant (GrowGarden) — Online Gardening Community Platform

An interactive, server-rendered **Online Gardening Community Platform** built with **Java Spring Boot**, **Thymeleaf**, **Spring Data JPA**, and direct **JDBC**. 

Verdant provides gardening enthusiasts, urban botanists, and allotment growers with a digital sanctuary to share horticultural tips, diagnose plant diseases and pests, participate in community discussions, access polymorphic plant-care advice, and track personal garden projects from seed to harvest.

---

## 🌟 Key Features

### 1. Community Dashboard (`/`)
- **Real-Time Statistics**: Live counters for total tips, community discussions, solved plant disease problems, and ongoing garden projects.
- **Featured Articles**: Curated high-yield gardening guides and organic recipes.
- **Recent Q&A Feed**: Quick preview of unanswered questions and resolved clinical plant issues.
- **Active Projects Showcase**: Community garden journeys across balconies, windowsills, and raised beds.

### 2. Gardening Tips & Articles (`/tips`)
- **Full CRUD Support**: Create, view, edit, and delete gardening tips and articles.
- **Category & Keyword Filtering**: Filter by categories such as *Plant Care*, *Pest & Disease Control*, *Soil & Fertilizer*, *Propagation*, and *Seasonal Gardening*.
- **Difficulty Badges**: Articles labeled as *Beginner*, *Intermediate*, or *Advanced*.
- **Community Upvoting**: Interactive "Helpful" like counters on articles.

### 3. Community Discussions & Plant Clinic (`/discussions`)
- **Troubleshoot Plant Diseases**: Post plant symptoms (yellowing leaves, powdery mildew, aphid infestations, overwatering).
- **Multi-turn Community Answers**: Gardeners and certified horticulturists reply with practical remedies.
- **Expert Advice Badges**: Flag verified horticultural answers.
- **Status Tracking**: Mark questions as *Solved* or re-open as *Needs Help*.
- **Helpful Upvotes**: Upvote the best remedies.

### 4. Personal Gardening Projects (`/projects`)
- **Garden Tracker**: Manage personal gardens with title, plant species, growing space (*Balcony*, *Backyard*, *Indoor Windowsill*, *Rooftop*), and status (*Planning*, *In Progress*, *Flourishing*, *Completed*).
- **Milestone & Care Journal**: Log progress, soil recipes, germination dates, and target harvest dates.
- **Full CRUD Management**: Create, view journal details, edit project specs, or delete projects.

### 5. Dual Interface: Server-Rendered UI + REST API
- **Thymeleaf HTML5 Templates**: Responsive Bootstrap 5 interface styled with custom natural green tones and plant aesthetics.
- **REST API Endpoints (`/api/*`)**: Clean JSON endpoints for stats, tips, discussions, and projects.

---

## 🛠️ Technology Stack

| Component | Technology |
| :--- | :--- |
| **Language** | Java 17+ (Fully tested and compatible with JDK 17, 21, and 26) |
| **Backend Framework** | Spring Boot 3.1.2 (Spring MVC, Spring Data JPA, Validation) |
| **Frontend Engine** | Thymeleaf 3 (Server-rendered responsive HTML5) |
| **UI Framework** | Bootstrap 5.3 + Bootstrap Icons + Custom Garden Theme CSS |
| **Database** | Embedded **H2 Database** (file-based persistence at `./data/gardendb`) |
| **Optional Database** | MySQL Connector (configured via `application.properties`) |
| **Build Tool** | Apache Maven 3.9+ / Maven Wrapper (`mvnw` / `mvnw.cmd`) |

---

## 📋 Prerequisites

- **Java Development Kit (JDK)**: JDK 17 or higher (JDK 17, 21, or 26)
- **Maven**: Maven Wrapper is included (`mvnw.cmd` on Windows, `./mvnw` on Linux/macOS)

---

## 🚀 Quick Start Guide

### 1. Clone or Open the Repository
```bash
cd Grow-Garden-main/Grow-Graden
```

### 2. Set `JAVA_HOME` (if not already set in environment)
**On Windows (PowerShell):**
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-26.0.2.1"
```
**On Linux / macOS:**
```bash
export JAVA_HOME=/path/to/jdk-17
```

### 3. Build & Test the Project
```bash
# Windows
.\mvnw.cmd clean package

# Linux / macOS
./mvnw clean package
```

### 4. Run the Application
You can run using Spring Boot plugin or directly via the packaged executable JAR:

**Option A: Run with Maven Wrapper**
```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

**Option B: Run Executable JAR**
```bash
java -jar target/Grow-Graden-0.0.1-SNAPSHOT.jar
```

### 5. Access the Platform
Once started, open your web browser and navigate to:
- **Application Dashboard**: [http://localhost:8080](http://localhost:8080)
- **Gardening Tips**: [http://localhost:8080/tips](http://localhost:8080/tips)
- **Community Q&A**: [http://localhost:8080/discussions](http://localhost:8080/discussions)
- **Personal Projects**: [http://localhost:8080/projects](http://localhost:8080/projects)
- **About Page**: [http://localhost:8080/about](http://localhost:8080/about)
- **H2 Web Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
  - *JDBC URL*: `jdbc:h2:file:./data/gardendb`
  - *Username*: `sa`
  - *Password*: *(leave empty)*

---

## 🔌 REST API Endpoints

The application also exposes clean REST endpoints for external clients or headless integrations:

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/stats` | Platform statistics (total tips, discussions, resolved cases, projects) |
| `GET` | `/api/tips` | List all gardening tips (supports `?search=` and `?category=`) |
| `GET` | `/api/tips/{id}` | Retrieve single tip by ID |
| `GET` | `/api/discussions` | List discussions (supports `?search=`, `?category=`, `?resolved=`) |
| `GET` | `/api/discussions/{id}` | Retrieve question details and replies |
| `GET` | `/api/projects` | List personal projects (supports `?search=` and `?status=`) |
| `GET` | `/api/projects/{id}` | Retrieve project details |
| `GET` | `/api/jdbc/metrics` | Direct JDBC analytical aggregates (category metrics & project status) |
| `GET` | `/api/jdbc/tips/search` | Direct JDBC parameterized search via `PreparedStatement` & `SearchResultWrapper<T>` |
| `GET` | `/api/advisor/diagnose` | Polymorphic botanical diagnosis (`?plantType=Tomato&symptom=yellow+leaves`) |
| `GET` | `/api/advisor/care-plan` | Polymorphic care plan generation (`?plantType=Succulent&spaceType=Balcony`) |

---

## 🏛️ Academic Compliance & Core Java Highlights

1. **Direct JDBC Integration (`GardenJdbcDao`)**:
   - Implements genuine `java.sql.Connection`, `java.sql.PreparedStatement`, and `java.sql.ResultSet`.
   - Employs try-with-resources blocks preventing database connection leaks.
   - Executes parameterized queries avoiding SQL injection vulnerabilities.
2. **Inheritance & Runtime Polymorphism (`com.example.domain.advisor`)**:
   - Abstract base class `AbstractPlantAdvisor` with template method contracts.
   - Concrete subclasses (`VegetableAdvisor`, `SucculentAdvisor`, `HerbAdvisor`, `TropicalFoliageAdvisor`).
   - Dynamic method dispatch via `PlantAdvisorRegistry` resolving plant types at runtime.
3. **Custom Exception Hierarchy & Global Advice (`com.example.exception`)**:
   - `ResourceNotFoundException` extends `RuntimeException`.
   - Domain-specific subclasses: `GardeningTipNotFoundException`, `GardenProjectNotFoundException`, `DiscussionQuestionNotFoundException`.
   - Centralized `@ControllerAdvice` (`GlobalExceptionHandler`) translating domain errors to HTTP 404 JSON (REST) and user alerts (HTML).
4. **Student-Written Generics & Collections (`com.example.util`)**:
   - Custom generic class `SearchResultWrapper<T>` with generic method mapping `map(Function<T, R>)`.
5. **Jakarta Servlet API Filter (`GardenSecurityHeadersFilter`)**:
   - Intercepts requests, attaches `X-Application-Name`, `X-Content-Type-Options`, `X-Frame-Options`, and `X-Response-Time-Ms`.
6. **Presentation Deliverables**:
   - Presentation files included in root: `GrowGarden_Project_Presentation.pptx` and `GrowGarden_Project_Presentation.pdf`.

---

## 🗄️ Database Configuration

By default, GrowGarden uses an **embedded, persistent file-based H2 database** (`./data/gardendb`). All seed data and user contributions survive application restarts without needing any local database server installed.

### Optional: Connecting to MySQL
If you prefer running a local MySQL database, open `src/main/resources/application.properties` and configure:
```properties
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/growgarden?createDatabaseIfNotExist=true&useSSL=false
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
```

---

## 🧪 Testing

Execute automated integration tests verifying database persistence, tip CRUD, Q&A replies, direct JDBC DAO, polymorphic advisors, custom exceptions, and servlet filter headers (18/18 tests pass):
```bash
# Windows
.\mvnw.cmd test

# Linux / macOS
./mvnw test
```

---

## 📦 GitHub Repository & Setup

- **GitHub Repository**: [https://github.com/AsHura-Wnd/Verdant.git](https://github.com/AsHura-Wnd/Verdant.git)
- **Primary Branch**: `feature/academic-compliance` (complete, tested academic submission)

```bash
# Clone the repository
git clone https://github.com/AsHura-Wnd/Verdant.git
cd Verdant
```

---

## 🚢 Production Deployment

### 1. Standalone JAR Deployment
```bash
nohup java -jar target/Grow-Graden-0.0.1-SNAPSHOT.jar --server.port=8080 > app.log 2>&1 &
```

### 2. Docker Deployment
Create a `Dockerfile` in `Grow-Graden`:
```dockerfile
FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp
COPY target/Grow-Graden-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```
Build and run:
```bash
docker build -t grow-garden-app .
docker run -p 8080:8080 grow-garden-app
```
