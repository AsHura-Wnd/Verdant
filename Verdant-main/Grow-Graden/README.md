# Verdant Spring Boot Module (`Verdant-main/Grow-Graden`)

This directory contains the Maven and Java 17 Spring Boot 3.1.2 backend and Thymeleaf frontend for the **Verdant** online gardening community platform.

For the repository-wide overview and Render deployment guide, see the [root README](../../README.md).

---

## Module Structure

```text
src/
├── main/
│   ├── java/com/example/
│   │   ├── config/          # SecurityConfig, DataInitializer, GardenSecurityHeadersFilter
│   │   ├── controller/      # MVC controllers + focused REST controllers (/api/*)
│   │   ├── domain/advisor/  # AbstractPlantAdvisor hierarchy & PlantAdvisorRegistry
│   │   ├── dto/             # Form-binding DTOs (RegisterDto)
│   │   ├── exception/       # Custom exceptions, @RestControllerAdvice, @ControllerAdvice
│   │   ├── model/           # JPA entities (GardeningTip, DiscussionQuestion/Reply, GardenProject, User)
│   │   ├── repository/      # Spring Data JPA repositories + atomic @Modifying updates
│   │   │   └── jdbc/        # GardenJdbcDao (direct java.sql JDBC queries for compliance)
│   │   ├── security/        # CustomUserDetailsService & OwnershipService
│   │   ├── service/         # Business logic interfaces & @Transactional implementations
│   │   └── util/            # Generic SearchResultWrapper<T>
│   └── resources/
│       ├── application.properties      # Default runtime & security configuration
│       ├── application-dev.properties  # Local development profile (enables H2 console)
│       ├── static/                     # CSS, navbar.js, WebGL cloud-shader.js, assets
│       └── templates/                  # Thymeleaf HTML5 views & layout fragments
└── test/java/com/example/              # JUnit 5 + MockMvc integration test suites
```

---

## Build, Test, and Run Locally

Ensure `JAVA_HOME` points to JDK 17 or newer, then run commands from this directory (`Verdant-main/Grow-Graden`):

### Run All Tests
```powershell
.\mvnw.cmd test
```
```bash
./mvnw test
```

### Build Executable JAR
```powershell
.\mvnw.cmd clean package
```
Output artifact: `target/Grow-Graden-0.0.1-SNAPSHOT.jar`

### Run Application
**Default profile (H2 console disabled):**
```powershell
.\mvnw.cmd spring-boot:run
```

**Development profile (H2 console enabled for `ROLE_ADMIN` at `/h2-console`):**
```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## Security & Profiles

- **Authentication & Roles**: Form login at `/login`, self-registration at `/register`, BCrypt password encoding, and ownership checks enforced via `OwnershipService`.
- **H2 Database Console**: Disabled by default (`H2_CONSOLE_ENABLED=false`). When enabled (`dev` profile or `H2_CONSOLE_ENABLED=true`), `/h2-console/**` requires `ROLE_ADMIN`.
- **Render / Container Runtime**: Supports dynamic `PORT` binding (`server.port=${PORT:8080}`) and an ephemeral/overrideable `SPRING_DATASOURCE_URL`.

---

## REST API Controllers

- `RestApiController` (`/api`): `/api/health`, `/api/stats`, `/api/tips`, `/api/discussions`, `/api/projects`
- `JdbcAnalyticsRestController` (`/api/jdbc`): `/api/jdbc/metrics`, `/api/jdbc/tips/search`
- `PlantAdvisorRestController` (`/api/advisor`): `/api/advisor/diagnose`, `/api/advisor/care-plan`
- Error responses are serialized cleanly as JSON via `RestApiExceptionHandler` (`@RestControllerAdvice`).
