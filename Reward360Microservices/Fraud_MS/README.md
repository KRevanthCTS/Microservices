★★★ PROJECT NAME: DASHBOARD BACKEND (MODULE 4.4.1 – DASHBOARD & ALERTS) ★★★
This project is a Spring Boot backend created for the Dashboard & Alerts module.
It provides REST APIs for:

Creating and managing alerts
Storing and retrieving transactions
Fetching flagged transactions
Displaying summary metrics
Running a simple anomaly detection process

The backend follows this clean layered structure:
Controller → Service → Repository → Entity → H2 Database
========================================================
TECHNOLOGIES USED
========================================================
Spring Boot – Main application framework
Spring Web – For REST APIs
Spring Data JPA – For database operations
H2 Database – In-memory DB
Java 17 – Programming language
Maven – Build management
========================================================
PROJECT STRUCTURE
========================================================
src/main/java/com/example/dashboard_backend
BackendDashboardApplication.java
config/
CorsConfig.java
DevDataConfig.java
entity/
Alert.java
AlertSeverity.java
AlertStatus.java
Transaction.java
repository/
AlertRepository.java
TransactionRepository.java
service/
AlertService.java
DashboardService.java
AnomalyService.java
controller/
AlertController.java
DashboardController.java
src/main/resources/
application.properties
data.sql (optional)
========================================================
DATABASE – H2 IN-MEMORY
========================================================
No installation required.
Resets every time the application restarts.
H2 Console URL:
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:reward360db
Username: sa
Password: (empty)
========================================================

API Endpoints
Create Alert
POST /api/alerts
Sample Body:
{
"severity": "HIGH",
"title": "Test Alert",
"description": "Suspicious activity detected",
"assignee": "admin1",
"tags": "fraud,test",
"transactionId": 1
}
Get All Alerts
GET /api/alerts
Get Dashboard Metrics
GET /api/dashboard/metrics
Get Flagged Transactions
GET /api/dashboard/anomalies
Run Anomaly Analysis
POST /api/dashboard/analyze