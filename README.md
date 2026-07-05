# ERASM — Setup & Build Guide (Windows + Eclipse + MySQL)

This is a working Spring Boot backend skeleton for your ERASM project. It already
implements: JWT authentication, role-based security, User/Skill/Employee/Project
modules, the Resource Request approval workflow, the Allocation module (with the
100% allocation rule), global exception handling, and SLF4J logging. You'll extend
it for the remaining modules (Audit, Reports, Utilization Dashboard) using the same
patterns — instructions are at the bottom.

## 1. Install prerequisites (Windows)

Install these in order, and restart your PC after step 1:

1. **JDK 17** — download from https://adoptium.net (choose "Temurin 17, JDK, .msi").
   During install, tick "Set JAVA_HOME variable" and "Add to PATH".
   Verify: open Command Prompt → `java -version` should show 17.x.
2. **Eclipse IDE for Enterprise Java and Web Developers** — from
   https://www.eclipse.org/downloads/packages/ (this bundle includes Maven support
   built in — you don't need to install Maven separately).
3. **MySQL Community Server 8.x** — from https://dev.mysql.com/downloads/installer/.
   During setup, set a root password and **remember it** — you'll need it in step 4.
   Also install **MySQL Workbench** (offered in the same installer) to view your data visually.
4. **Postman** — from https://www.postman.com/downloads/ (for testing the APIs).

## 2. Get the project into Eclipse

1. Unzip the project folder you downloaded from this chat somewhere like `C:\projects\erasm-backend`.
2. Open Eclipse → **File → Import → Maven → Existing Maven Projects**.
3. Browse to `C:\projects\erasm-backend`, select it, click **Finish**.
4. Eclipse will download all dependencies automatically (needs internet, takes a few minutes the first time).
   Watch the bottom-right progress bar until it's done.

## 3. Configure MySQL

1. Open **MySQL Workbench**, connect using the root password you set.
2. You don't need to create the database manually — the app does it automatically
   (`createDatabaseIfNotExist=true` in the connection URL). But you do need to tell
   the app your MySQL password:
3. In Eclipse, open `src/main/resources/application.properties` and change this line:
   ```
   spring.datasource.password=YOUR_MYSQL_PASSWORD
   ```
   to your actual MySQL root password.

## 4. Run the application

1. In Eclipse's **Project Explorer**, expand `erasm-backend → src/main/java → com.erasm`.
2. Right-click `ErasmApplication.java` → **Run As → Java Application**.
3. Watch the **Console** tab. You should see Spring Boot's startup banner, Hibernate
   creating tables, and finally `Started ErasmApplication in X seconds`.
4. If you see a MySQL connection error, double check step 3 and that the MySQL80
   service is running (Windows → Services → "MySQL80" → should say "Running").

Your API is now live at `http://localhost:8080`.

## 5. Test it with Postman

1. Open Postman → **Import** → select `postman/ERASM.postman_collection.json` from the project.
2. Run **Auth → Register Admin** first (creates an ADMIN user + returns a JWT token).
3. Copy the `token` value from the response.
4. Click the collection name → **Variables** tab → paste it into the `token` variable's **Current Value** → Save.
5. Now every other request (which sends `Authorization: Bearer {{token}}`) will be authenticated as that admin.
6. Try them in this order to see the full flow: Add Skill → Create Project → Create Resource Request →
   Submit → Approve → Allocate Employee.

## 6. Understand the project structure

```
src/main/java/com/erasm/
  entity/       → JPA entities = your database tables (User, Employee, Skill, Project, ...)
  repository/   → Spring Data interfaces — no SQL needed, method names generate queries
  dto/          → objects that cross the API boundary (never expose entities directly)
  service/      → business logic (validation, workflow rules) lives here
  controller/   → REST endpoints — thin, just call services
  security/     → JWT creation/validation + how Spring Security loads users
  config/       → SecurityConfig (who can call what) + DataInitializer (seeds roles)
  exception/    → custom exceptions + GlobalExceptionHandler (turns errors into clean JSON)
```

**Request flow:** Controller receives HTTP request → calls Service → Service uses
Repository to talk to MySQL via Hibernate → Service returns a DTO → Controller
returns it as JSON.

## 7. What's already built vs. what you still need to add

**Already implemented** (study these to learn the pattern before adding more):
- Module 1 (User Management incl. JWT auth) — `AuthController`/`AuthService`
- Module 2 (Skill Management) — `SkillController`/`SkillService`
- Module 3 (Employee Skill Profile) — `EmployeeController`/`EmployeeService`
- Module 4 (Project Management) — `ProjectController`/`ProjectService`
- Module 5 & 6 (Resource Requests + Approval Workflow) — `ResourceRequestController`/`Service`
- Module 7 (Resource Allocation incl. the "can't exceed 100%" rule) — `AllocationController`/`Service`
- Security (JWT, `@PreAuthorize` role checks, BCrypt password hashing)
- Global exception handling + SLF4J logging (INFO/WARN/ERROR as required)
- One sample Mockito test (`AllocationServiceTest`) demonstrating the testing pattern

**You still need to build** (same pattern every time — repository → service → controller):
- **Module 8 (Utilization Dashboard):** add a `UtilizationService` with methods like
  `getBillablePercent(employeeId)` = `(billableHours / totalHours) * 100`. You'll need
  to decide how "hours" are tracked — e.g. add a `billableHours`/`totalHours` field to
  `Allocation`, or a separate `Timesheet` entity.
- **Module 9 (Audit Management):** the `AuditLog` entity and repository already exist.
  Add an `AuditService.log(action, entityName, entityId)` method, then call it from
  inside `AllocationService`, `ResourceRequestService`, etc. wherever a critical action happens.
- **Module 10 (Reports):** add a `ReportController` with endpoints like
  `GET /reports/skills` (group employees by skill — use `EmployeeSkillRepository.findBySkillId`)
  and `GET /reports/utilization`.
- **More tests:** copy the pattern in `AllocationServiceTest.java` for each service
  class until you hit 80% coverage (Eclipse: right-click project → **Coverage As → JUnit Test**
  after installing the EclEmma plugin from Eclipse Marketplace).

## 8. Git workflow (for your deliverable)

```bash
cd erasm-backend
git init
git checkout -b develop
git checkout -b feature/skill-management
git add .
git commit -m "feat: implement skill management module"
git checkout develop
git merge feature/skill-management
```
Push `develop` and `main` branches to a GitHub repo you create at https://github.com/new,
then submit that repo link as required.

## 9. Deliverables checklist mapped to what's in this folder

| Requirement | Where |
|---|---|
| Source Code | this whole folder → push to GitHub |
| SQL Script | `database/erasm_schema.sql` |
| Postman Collection | `postman/ERASM.postman_collection.json` |
| Test Report | Eclipse → right-click project → **Run As → Maven test** → results in `target/surefire-reports/` |
| Documentation | write this up in Word with screenshots — see docx skill if you want help generating it |

## 10. If something goes wrong

- **Port 8080 already in use:** change `server.port=8081` in `application.properties`.
- **"Access denied for user 'root'":** your MySQL password in `application.properties` is wrong.
- **Lombok getters/setters "not found" errors in Eclipse:** run the Lombok installer —
  download `lombok.jar` from https://projectlombok.org/download, double-click it, point it
  at your Eclipse install folder, restart Eclipse.
- **Table not created:** check `spring.jpa.hibernate.ddl-auto=update` is present and the app
  actually reached "Started ErasmApplication" in the console (errors above that line mean it
  crashed before touching the DB).
