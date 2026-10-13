# Task Manager Backend

A **Spring Boot 3.5** REST API built with **Java 21** for the Full‑Stack Task Manager demo.

## Project Structure
```
backend/
├── pom.xml
├── README.md                # This file
├── API_CONTRACT.md          # API contract reference
├── .gitignore
├── .env.example             # Example environment variables
└── src/
    ├── main/
    │   ├── java/com/example/taskmanager/
    │   │   ├── TaskManagerApplication.java
    │   │   ├── controller/
    │   │   │   └── TaskController.java
    │   │   ├── service/
    │   │   │   ├── TaskService.java
    │   │   │   └── TaskServiceImpl.java
    │   │   ├── repository/
    │   │   │   └── TaskRepository.java
    │   │   ├── model/
    │   │   │   └── Task.java
    │   │   ├── dto/
    │   │   │   ├── TaskCreateRequest.java
    │   │   │   └── TaskResponse.java
    │   │   ├── exception/
    │   │   │   ├── TaskNotFoundException.java
    │   │   │   └── GlobalExceptionHandler.java
    │   │   └── config/
    │   │       └── WebConfig.java
    │   └── resources/
    │       └── application.properties
    └── test/java/com/example/taskmanager/controller/
        ├── TaskControllerTest.java
        └── TaskControllerAdditionalTest.java
```

## Prerequisites
- **Java 21** (`java -version`)
- **Maven 3.9+** (`mvn -v`)
- **MySQL 8** server

## Database Setup
```sql
-- Create database
CREATE DATABASE task_manager_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create a user with minimum permissions
CREATE USER 'task_app'@'%' IDENTIFIED BY '<choose_a_strong_password>';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, DROP ON task_manager_db.* TO 'task_app'@'%';
FLUSH PRIVILEGES;
```
> **Never** commit the real password to source control. Use environment variables instead.

## Configuration
Create a copy of `.env.example` as `.env` and set the values:
```dotenv
DB_HOST=localhost
DB_PORT=3306
DB_NAME=task_manager_db
DB_USERNAME=task_app
DB_PASSWORD=your_password_here
```
The `application.properties` file reads these variables automatically.

## Build & Run
```bash
# Build the project
mvn clean package

# The executable JAR will be under target/, e.g.:
java -jar target/task-manager-0.0.1-SNAPSHOT.jar
```
If more than one JAR appears, pick the one that ends with `-SNAPSHOT.jar` (the Spring Boot re‑packaged jar).

## Development Server
The application starts on **port 8080**.
- Health check: `curl http://localhost:8080/api/health` → `OK`
- List tasks: `curl http://localhost:8080/api/tasks`
- Create task (example):
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"Learn Spring Boot"}'
```
- Delete task: `curl -X DELETE http://localhost:8080/api/tasks/1`

## Running Tests
```bash
mvn test
```
All unit tests are written with **JUnit 5** and **MockMvc** and do **not** require a live MySQL instance (the service layer is mocked).

## CORS
During development the backend allows requests from the React dev server (`http://localhost:5173`). In production, the frontend will be served from the same origin behind Nginx, so CORS can be disabled or left as‑is.

## Deploying to a VPS (outline)
1. Install Java 21, Maven, and MySQL.
2. Create the database and user as shown above.
3. Transfer the source code, run `mvn clean package` on the server, and copy the JAR to `/opt/task-manager/` (or a location of your choice).
4. Create a systemd service file (example `task-manager.service`) that runs the JAR:
```ini
[Unit]
Description=Task Manager Backend
After=network.target

[Service]
Type=simple
User=taskuser   # a non‑root user
WorkingDirectory=/opt/task-manager
ExecStart=/usr/bin/java -jar /opt/task-manager/task-manager-0.0.1-SNAPSHOT.jar
EnvironmentFile=/opt/task-manager/.env
SuccessExitStatus=143
Restart=on-failure

[Install]
WantedBy=multi-user.target
```
5. Reload systemd and start the service:
```bash
sudo systemctl daemon-reload
sudo systemctl enable task-manager
sudo systemctl start task-manager
```
6. Verify the service is running:
```bash
sudo systemctl status task-manager
curl http://localhost:8080/api/health
```
7. Configure Nginx to proxy `/api/` to the backend:
```nginx
server {
    listen 80;
    server_name your.domain.com;

    location / {
        root /var/www/react-app;   # path to built React files
        try_files $uri /index.html;
    }

    location /api/ {
        proxy_pass http://127.0.0.1:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```
Reload Nginx (`sudo nginx -s reload`).

## License
This demo project is provided for educational purposes. Feel free to modify and extend it.
