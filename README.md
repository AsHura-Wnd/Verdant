# 🌿 Verdant — Online Gardening Community Platform

An interactive, server-rendered **Online Gardening Community Platform** built with **Java Spring Boot**, **Thymeleaf**, **Spring Data JPA**, and direct **JDBC**. 

Verdant provides gardening enthusiasts, urban botanists, and allotment growers with a digital sanctuary to share horticultural tips, diagnose plant diseases and pests, participate in community discussions, access polymorphic plant-care advice, and track personal garden projects from seed to harvest.

The project demonstrates the practical application of core Java programming, object-oriented design, direct JDBC database connectivity, Jakarta Servlet API integration, and web technologies using the Spring Boot framework.

---

## ✨ Key Features

### 1. Community Dashboard (`/`)
- **Real-Time Statistics**: Live counters for total tips, community discussions, solved plant disease problems, and ongoing garden projects.
- **Featured Articles**: Curated high-yield gardening guides and organic recipes.
- **Recent Q&A Feed**: Quick preview of unanswered questions and resolved clinical plant issues.
- **Active Projects Showcase**: Community garden journeys across balconies, windowsills, and raised beds.
- **Animated Visuals**: Full-viewport WebGL CloudShader background with dynamic GLSL noise rendering.
- **Cinematic Garden Reveal**: Scroll-driven multi-scene narrative transition revealing the community garden grid.

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

### 5. Polymorphic Plant-Care Advisor (`/api/advisor/*`)
- **Specialized Advisors**: Concrete subclasses for *Vegetables*, *Succulents*, *Herbs*, and *Tropical Foliage*.
- **Custom Care Plans**: Generates tailored watering schedules, sunlight requirements, and N-P-K fertilizer guidance.
- **Symptom Diagnosis**: Identifies root rot, leaf scorch, powdery mildew, and nutrient deficiencies with actionable organic fixes.

### 6. Dual Interface: Server-Rendered UI + REST API
- **Thymeleaf HTML5 Templates**: Responsive Bootstrap 5 interface styled with custom botanical green glassmorphism aesthetics.
- **REST API Endpoints (`/api/*`)**: Clean JSON endpoints for stats, tips, discussions, projects, JDBC analytics, and advisor services.

---

## 🛠️ Technology Stack

| Component | Technology |
|---|---|
| **Language** | Java 17+ (Tested on JDK 17, JDK 21, and JDK 26) |
| **Backend Framework** | Spring Boot 3.1.2 (Spring MVC, Spring Data JPA, Validation) |
| **Frontend Engine** | Thymeleaf 3 (Server-rendered responsive HTML5) |
| **UI Framework** | Bootstrap 5.3 + Bootstrap Icons + Custom Glassmorphic Botanical CSS |
| **Graphics / Animation** | WebGL 1.0 / GLSL (Full-viewport animated CloudShader & scroll reveal) |
| **Database** | Embedded **H2 Database** (file-based persistence at `./data/gardendb`) |
| **Direct Database API** | Direct **JDBC** (`java.sql.Connection`, `PreparedStatement`, `ResultSet`) |
| **Web Integration** | Embedded Apache Tomcat + Jakarta Servlet API (`Filter`) |
| **Build Tool** | Apache Maven 3.9+ / Maven Wrapper (`mvnw` / `mvnw.cmd`) |
| **Testing** | JUnit 5, Spring Boot Test, MockMvc (18 integration tests) |

---

## 🏗️ Project Architecture

Verdant uses a clean, layered architecture separating concerns across tiers:

- **Filter / Web Tier**: `GardenSecurityHeadersFilter` intercepts incoming HTTP requests, adding security headers (`X-Content-Type-Options`, `X-Frame-Options`) and measuring execution time (`X-Response-Time-Ms`).
- **Controller Layer (`com.example.controller`)**: Spring MVC controllers handle web requests, validate forms, and render Thymeleaf views; `RestApiController` provides JSON endpoints.
- **Service Layer (`com.example.service`)**: Encapsulates business logic and coordinates database transactions.
- **Persistence Layer (`com.example.repository`)**:
  - **Spring Data JPA**: High-level repository abstraction for standard entity CRUD operations.
  - **Direct JDBC DAO (`com.example.repository.jdbc.GardenJdbcDao`)**: Executes raw SQL with `PreparedStatement` and `ResultSet` for analytical grouping and optimized search.
- **Domain & Advisor Layer (`com.example.domain.advisor`)**: Abstract base class `AbstractPlantAdvisor` with 4 polymorphic subclasses dynamically resolved via `PlantAdvisorRegistry`.
- **Exception Layer (`com.example.exception`)**: `ResourceNotFoundException` hierarchy mapped via `@ControllerAdvice` (`GlobalExceptionHandler`) to standard HTTP 404 JSON error responses.

---

## 📋 Prerequisites

- **Java Development Kit (JDK)**: JDK 17 or higher (JDK 17, 21, or 26)
- **Git**
- **Maven**: Maven Wrapper is included (`mvnw.cmd` on Windows, `./mvnw` on Linux/macOS)

---

## 🚀 Quick Start Guide

### 1. Clone the Repository
```bash
git clone https://github.com/AsHura-Wnd/Verdant.git
cd Verdant
```

### 2. Navigate to Project Directory
```bash
cd Grow-Garden-main/Grow-Graden
```

### 3. Set `JAVA_HOME` (if not already set in environment)
**On Windows (PowerShell):**
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-26.0.2.1"
```
**On Linux / macOS:**
```bash
export JAVA_HOME=/path/to/jdk-17
```

### 4. Build & Package
```bash
# Windows
.\mvnw.cmd clean package

# Linux / macOS
./mvnw clean package
```

### 5. Run the Application
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

### 6. Access the Platform
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

## 🧭 Main Application Routes

| Route | Description |
|---|---|
| `/` | Home page with CloudShader and scroll-driven garden reveal |
| `/tips` | Browse gardening tips and articles |
| `/tips/new` | Create a new gardening tip |
| `/tips/{id}` | Detailed view and upvoting for a tip |
| `/discussions` | View community plant questions and clinic |
| `/discussions/new` | Ask a plant troubleshooting question |
| `/discussions/{id}` | Read discussion answers and post replies |
| `/projects` | Explore personal garden project journals |
| `/projects/new` | Create a garden project |
| `/projects/{id}` | View project timeline, soil recipe, and milestones |
| `/about` | About the platform and mission |

---

## 🔌 REST API Endpoints

The application exposes clean REST endpoints for JSON clients:

| Method | Endpoint | Description |
|---|---|---|
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

## 📚 Core Java & Academic Compliance Highlights

1. **Direct JDBC Integration (`GardenJdbcDao`)**:
   - Implements genuine `java.sql.Connection`, `java.sql.PreparedStatement`, and `java.sql.ResultSet`.
   - Employs try-with-resources blocks preventing connection leaks.
   - Parameterized SQL queries preventing SQL injection vulnerabilities.
2. **Inheritance & Runtime Polymorphism (`com.example.domain.advisor`)**:
   - Abstract base class `AbstractPlantAdvisor` with template method contracts.
   - Concrete subclasses (`VegetableAdvisor`, `SucculentAdvisor`, `HerbAdvisor`, `TropicalFoliageAdvisor`).
   - Dynamic method dispatch via `PlantAdvisorRegistry` resolving advisor beans at runtime.
3. **Custom Exception Hierarchy & Global Advice (`com.example.exception`)**:
   - `ResourceNotFoundException` extends `RuntimeException`.
   - Domain-specific subclasses: `GardeningTipNotFoundException`, `GardenProjectNotFoundException`, `DiscussionQuestionNotFoundException`.
   - Centralized `@ControllerAdvice` (`GlobalExceptionHandler`) translating domain errors to HTTP 404 JSON for REST and user flash alerts for HTML.
4. **Student-Written Generics & Collections (`com.example.util`)**:
   - Custom generic container `SearchResultWrapper<T>` with higher-order `<R> SearchResultWrapper<R> map(Function<T, R>)` transformation.
5. **Jakarta Servlet API Filter (`GardenSecurityHeadersFilter`)**:
   - Intercepts requests, attaches `X-Application-Name`, `X-Content-Type-Options`, `X-Frame-Options`, `X-Servlet-Filter`, and `X-Response-Time-Ms`.
6. **Presentation Deliverables**:
   - Presentation files included in root: `GrowGarden_Project_Presentation.pptx` and `GrowGarden_Project_Presentation.pdf`.

---

## 🗄️ Database Configuration

By default, Verdant uses an **embedded, persistent file-based H2 database** (`./data/gardendb`). All seed data and user contributions survive application restarts without needing any local database server installed.

---

## 🧪 Running Automated Tests

Run the project's automated test suite with the Maven Wrapper (19/19 tests pass):
```bash
# Windows
.\mvnw.cmd test

# Linux / macOS
./mvnw test
```

---

## ☁️ Deploying to Render (Free Web Service)

Verdant is containerized with a production multi-stage Docker build optimized for **Render's Free Web Service plan** (512 MB RAM, 0.1 CPU).

### 🐳 Local Docker Build & Verification

Before deploying to the cloud, you can build and verify the container locally using Docker:

#### 1. Build the Docker Image
From the repository root directory:
```bash
docker build -t verdant:latest .
```

#### 2. Run the Container
Map your local port `8080` to the container port:
```bash
docker run -d --name verdant-app -p 8080:8080 -e PORT=8080 verdant:latest
```

#### 3. Test Container Health & Web UI
```bash
# Verify health check endpoint (HTTP 200, status: UP)
curl http://localhost:8080/api/health

# Verify Web Dashboard (HTTP 200)
curl -I http://localhost:8080/
```

#### 4. Stop and Clean Up Container
```bash
docker stop verdant-app && docker rm verdant-app
```

---

### 🚀 Step-by-Step Render Deployment Guide

#### Step 1: Create a Render Account
Sign up or log in at [render.com](https://render.com).

#### Step 2: Create a New Web Service
1. In the Render Dashboard, click **New +** and select **Web Service**.
2. Connect your GitHub account and select your repository: `https://github.com/AsHura-Wnd/Verdant`.

#### Step 3: Configure Service Parameters
Configure the service with the following settings:

| Setting | Recommended Value | Notes |
|:---|:---|:---|
| **Name** | `verdant` (or your chosen name) | Forms your free public URL: `https://verdant.onrender.com` |
| **Region** | Select region closest to you | e.g., Frankfurt, Ohio, Oregon, Singapore |
| **Branch** | `main` | Production deployment branch |
| **Root Directory** | *(leave blank / empty)* | Defaults to repository root |
| **Runtime / Environment** | **Docker** | Render automatically detects the root `Dockerfile` |
| **Dockerfile Path** | `./Dockerfile` | Standard root Dockerfile location |
| **Docker Context** | `.` | Repository root build context |
| **Instance Type** | **Free** ($0/month, 512 MB RAM, 0.1 CPU) | Free tier compute plan |

#### Step 4: Environment Variables (Optional)
The application works out of the box without requiring manual environment variables. Render automatically injects `PORT` (e.g. `10000`), which Verdant binds to dynamically via `${PORT:8080}`.

Optional configuration variables:

| Variable | Default Value | Description |
|:---|:---|:---|
| `PORT` | *(injected by Render)* | Port for Tomcat to bind to (configured via `${PORT:8080}`). |
| `JAVA_OPTS` | `-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Xss512k -XX:+ExitOnOutOfMemoryError` | JVM memory tuning configured in `Dockerfile` for 512MB RAM. |
| `DATA_DIR` | `./data` | File directory for embedded H2 database. |

#### Step 5: Health Check Path
Under **Advanced Settings** in Render:
- **Health Check Path:** `/api/health`
Render periodically polls this endpoint to verify container readiness before directing live traffic.

#### Step 6: Deploy Service
Click **Create Web Service**. Render will clone the repository, run the multi-stage Docker build, and start the container. Your live application will be accessible at:
```
https://<your-service-name>.onrender.com
```

---

### ⚠️ Database Persistence & Free Tier Notes

1. **H2 Ephemeral Storage on Render Free**:
   - Render Free instances do not support persistent disk volumes.
   - The embedded H2 database (`./data/gardendb`) operates inside the container filesystem.
   - When a free instance spins down after 15 minutes of inactivity or is restarted/redeployed, the container filesystem resets.
   - **Automatic Seeding**: Verdant's built-in `DataInitializer` automatically re-seeds the complete botanical dataset (gardening tips, plant disease Q&As, and ongoing projects) whenever the database initializes, ensuring the website is always immediately usable.
   
2. **Permanent Persistence Option (PostgreSQL)**:
   - For permanent data storage across redeployments, connect an external managed PostgreSQL database (such as Render PostgreSQL, Supabase, Neon, or ElephantSQL).
   - In Render's **Environment** tab, set:
     - `SPRING_DATASOURCE_URL`: `jdbc:postgresql://<host>:<port>/<dbname>`
     - `SPRING_DATASOURCE_USERNAME`: `<username>`
     - `SPRING_DATASOURCE_PASSWORD`: `<password>`
     - `SPRING_DATASOURCE_DRIVER_CLASS_NAME`: `org.postgresql.Driver`
     - `SPRING_JPA_DATABASE_PLATFORM`: `org.hibernate.dialect.PostgreSQLDialect`

3. **Free Tier Cold Starts**:
   - Free instances spin down when idle. The first HTTP request after inactivity may take 30–50 seconds to boot the JVM container.

---

### 🔍 Troubleshooting

| Issue | Cause | Resolution |
|:---|:---|:---|
| **Build fails: `mvnw: permission denied`** | Missing executable bit on wrapper | The `Dockerfile` includes `RUN chmod +x ./mvnw` and UNIX line ending normalization (`sed -i 's/\r$//' ./mvnw`). |
| **Container killed (Exit Code 137 / OOM)** | JVM exceeded 512MB RAM | `JAVA_OPTS` in the `Dockerfile` specifies `-XX:MaxRAMPercentage=75.0 -Xss512k` to cap heap at ~384MB and leave room for native memory. |
| **Port binding error or timeout** | Port mismatch with Render | Verdant dynamically reads `${PORT:8080}` and passes `-Dserver.port=${PORT}` in the Docker ENTRYPOINT. |
| **Health check failure** | Endpoint slow or unreachable during startup | Ensure the health check path is set to `/api/health`. Free tier initial boot can take ~15-20 seconds. |

---

## 📋 Project Objectives

- Build a functional web application using Java and Spring Boot.
- Apply object-oriented programming principles (inheritance, polymorphism, abstraction) to a practical domain.
- Integrate a database for persistent data management with Spring Data JPA.
- Demonstrate direct JDBC-based database operations (`PreparedStatement`, `ResultSet`, aggregated queries).
- Implement web request handling, MVC design pattern, and Jakarta Servlet filter integration.
- Develop a clear, maintainable, and user-friendly interface with animated WebGL visuals.

---

## 🔮 Future Enhancements

- User registration and role-based authentication (Spring Security)
- Personalized plant recommendation engine
- Garden-project image uploads (cloud blob storage)
- Advanced search indexing and multi-criteria filtering
- Automated plant-care reminders and seasonal push notifications
- Cloud production container deployment

---

## 👨‍💻 Project Information

- **Project Name:** Verdant
- **Project Type:** Java Web-Based Application
- **Domain:** Gardening and Botanical Community
- **Backend:** Java 17+ & Spring Boot 3.1.2
- **Database:** H2 Embedded (file-based persistence at `./data/gardendb`)
- **Build Tool:** Apache Maven
- **License:** MIT License (see [LICENSE](LICENSE))

---

*Verdant — Grow knowledge. Share nature.*
