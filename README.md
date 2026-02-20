# 🏆 Reward360 — Loyalty & Rewards Management Platform

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-orange?logo=openjdk" />
  <img src="https://img.shields.io/badge/Spring_Boot-3.2-green?logo=springboot" />
  <img src="https://img.shields.io/badge/React-18-blue?logo=react" />
  <img src="https://img.shields.io/badge/Vite-5-purple?logo=vite" />
  <img src="https://img.shields.io/badge/MySQL-8-blue?logo=mysql" />
  <img src="https://img.shields.io/badge/Architecture-Microservices-red" />
</p>

Reward360 is a **full-stack microservices-based** loyalty and rewards management platform. Admins create & manage offers, monitor fraud, view analytics, and run promotions. Users browse offers, redeem points, track transactions, and manage their profiles.

---

## 📑 Table of Contents

- [System Architecture](#-system-architecture)
- [Microservices Overview](#-microservices-overview)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Prerequisites](#-prerequisites)
- [Installation & Setup](#-installation--setup)
- [Running the Application](#-running-the-application)
- [API Gateway & Routing](#-api-gateway--routing)
- [JWT Authentication Flow](#-jwt-authentication-flow)
- [Frontend Pages & Features](#-frontend-pages--features)
- [API Endpoints](#-api-endpoints)
- [Environment Variables](#-environment-variables)
- [Troubleshooting](#-troubleshooting)
- [Contributing](#-contributing)

---

## 🏗 System Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           REWARD360 ARCHITECTURE                            │
│                                                                             │
│   ┌─────────────┐         ┌──────────────┐        ┌──────────────────┐     │
│   │  React App  │──HTTP──▶│  API Gateway  │──reg──▶│  Eureka Server   │     │
│   │  (Vite)     │         │  :8086        │◀───────│  :8761           │     │
│   │  :5173      │         │  JWT Filter   │        │  (Discovery)     │     │
│   └─────────────┘         └──────┬────────┘        └──────────────────┘     │
│                                  │                                          │
│         ┌────────────────────────┼────────────────────────┐                 │
│         │                        │                        │                 │
│         ▼                        ▼                        ▼                 │
│   ┌───────────┐         ┌──────────────┐         ┌──────────────┐          │
│   │user-service│         │ CustomerMs   │         │ promotions   │          │
│   │  :8087     │         │  :8081       │         │  :8083       │          │
│   │            │         │              │         │              │          │
│   │ • Auth     │         │ • Points     │         │ • Offers     │          │
│   │ • JWT Gen  │         │ • Tiers      │         │ • Redemption │          │
│   │ • Users    │         │ • Txns       │         │ • Promotions │          │
│   └───────────┘         └──────────────┘         └──────────────┘          │
│                                                                             │
│                         ┌──────────────┐         ┌──────────────┐          │
│                         │  Fraud_MS    │         │ Analytics    │          │
│                         │  :8082       │         │ Service      │          │
│                         │              │         │  :8089       │          │
│                         │ • Fraud Det. │         │ • Reports    │          │
│                         │ • Txn Check  │         │ • Dashboard  │          │
│                         └──────────────┘         └──────────────┘          │
│                                                                             │
│                         ┌──────────────┐                                   │
│                         │  MySQL DB    │                                   │
│                         │  :3306       │                                   │
│                         └──────────────┘                                   │
└─────────────────────────────────────────────────────────────────────────────┘
```

**All client requests** go through the **API Gateway** (`:8086`), which validates JWT tokens and routes to the correct microservice. Services register with **Eureka** for discovery.

---

## 🔧 Microservices Overview

| Service | Port | Description |
|---------|------|-------------|
| **Eureka Discovery** | `8761` | Service registry — all microservices register here |
| **API Gateway** | `8086` | Central entry point — JWT validation, routing, CORS |
| **user-service** | `8087` | Authentication (login/register), JWT generation, user management |
| **CustomerMs** | `8081` | Customer profiles, points balance, tiers, transactions |
| **promotions** | `8083` | Offers CRUD, redemption processing, promotions management |
| **Fraud_MS** | `8082` | Fraud detection, transaction monitoring, alerts |
| **AnalyticsService** | `8089` | Dashboard analytics, reports, data aggregation |

---

## 💻 Tech Stack

### Backend
| Technology | Purpose |
|-----------|---------|
| Java 17 | Language |
| Spring Boot 3.2 | Framework |
| Spring Cloud Gateway | API Gateway |
| Spring Cloud Netflix Eureka | Service Discovery |
| Spring Security | Authentication & password hashing |
| Spring Data JPA / Hibernate | ORM & database access |
| OpenFeign | Inter-service communication |
| JJWT (io.jsonwebtoken) | JWT token generation & validation |
| MySQL 8 | Database |
| Lombok | Boilerplate reduction |
| SpringDoc OpenAPI | Swagger documentation |
| Maven | Build tool |

### Frontend
| Technology | Purpose |
|-----------|---------|
| React 18 | UI framework |
| Vite 5 | Build tool & dev server |
| React Router 6 | Client-side routing |
| Axios | HTTP client with interceptors |
| Chart.js + react-chartjs-2 | Dashboard charts |
| jsPDF + jspdf-autotable | PDF report generation |
| xlsx | Excel export |
| Plain CSS | Styling (animations, responsive) |

---

## 📁 Project Structure

```
Microservices/
├── Reward360Application/          # Frontend
│   └── frontend/
│       ├── package.json
│       ├── vite.config.js
│       └── src/
│           ├── App.jsx                    # Router & layout
│           ├── main.jsx                   # Entry point
│           ├── styles.css                 # Global styles & animations
│           ├── api/
│           │   └── client.js              # Axios instance + JWT interceptor
│           ├── components/
│           │   ├── Header.jsx
│           │   ├── Footer.jsx
│           │   ├── ProtectedRoute.jsx     # Auth guard (any logged-in user)
│           │   └── AdminRoute.jsx         # Auth guard (ADMIN role only)
│           ├── context/
│           │   └── UserContext.jsx         # Global user state management
│           ├── services/
│           │   ├── userService.js          # User API calls
│           │   ├── analyticsService.js     # Analytics API calls
│           │   └── fraudService.js         # Fraud API calls
│           └── pages/
│               ├── Landing.jsx            # Landing page with animations
│               ├── auth/
│               │   ├── Login.jsx
│               │   ├── Register.jsx
│               │   ├── ForgotPassword.jsx
│               │   └── OtpVerify.jsx
│               ├── user/
│               │   ├── Dashboard.jsx      # Points, tier, charts
│               │   ├── Profile.jsx        # User profile management
│               │   ├── Offers.jsx         # Browse & redeem offers
│               │   ├── Redemptions.jsx    # Redemption history
│               │   └── Transactions.jsx   # Transaction history
│               └── admin/
│                   ├── Promotions.jsx     # Manage promotions
│                   ├── OffersAdmin.jsx    # Create/edit/toggle offers
│                   ├── CampaignBuilder.jsx # Build campaigns
│                   ├── FraudMonitor.jsx   # Fraud alerts & monitoring
│                   └── Reports.jsx        # Analytics reports + PDF/Excel
│
├── Reward360Microservices/        # Backend
│   ├── JWTSecurity.txt            # JWT flow documentation
│   ├── eureka-discovery-space/    # Eureka Server
│   │   └── src/main/...
│   ├── apigateway/                # API Gateway + JWT Filter
│   │   └── src/main/.../filter/JwtAuthFilter.java
│   ├── user-service/              # Auth & User Management
│   │   └── src/main/.../
│   │       ├── controller/AuthController.java
│   │       ├── util/JwtUtil.java
│   │       └── config/SecurityConfig.java
│   ├── CustomerMs/                # Customer Profiles & Transactions
│   ├── promotions/                # Offers & Redemptions
│   ├── Fraud_MS/                  # Fraud Detection
│   └── AnalyticsService/          # Analytics & Reporting
```

---

## 📋 Prerequisites

| Requirement | Version |
|-------------|---------|
| **Java JDK** | 17+ |
| **Maven** | 3.8+ |
| **Node.js** | 18+ |
| **npm** | 9+ |
| **MySQL** | 8.0+ |

---

## ⚙ Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/KRevanthCTS/Microservices.git
cd Microservices
```

### 2. Database Setup (MySQL)

Create the required databases:

```sql
CREATE DATABASE IF NOT EXISTS userservicedb;
CREATE DATABASE IF NOT EXISTS customerdb;
CREATE DATABASE IF NOT EXISTS promotiondb;
CREATE DATABASE IF NOT EXISTS frauddb;
CREATE DATABASE IF NOT EXISTS analyticsdb;
```

> Each microservice has its own `application.properties` with database config. Update `spring.datasource.username` and `spring.datasource.password` to match your MySQL credentials.

### 3. Set Environment Variables (PowerShell)

```powershell
$env:JWT_SECRET = "Zm9yLWRldmVsb3BtZW50LXNvbHZlLXlvdXItc2VjcmV0LWNoYW5nZQ=="
$env:MYSQL_ROOT_PASSWORD = "your-mysql-password"
```

### 4. Install Frontend Dependencies

```powershell
cd Reward360Application/frontend
npm install
```

---

## 🚀 Running the Application

### Start Order (Important!)

Services must be started in this order:

```
1. Eureka Server       (must be up first for service registration)
2. API Gateway         (registers with Eureka, sets up routes)
3. user-service        (auth must be available before other services)
4. CustomerMs          (depends on user-service via Feign)
5. promotions          (depends on CustomerMs via Feign)
6. Fraud_MS            (independent, can start anytime after Eureka)
7. AnalyticsService    (independent, can start anytime after Eureka)
8. Frontend            (needs API Gateway running)
```

### Backend (run each in a separate terminal)

```powershell
# Terminal 1 — Eureka
cd Reward360Microservices/eureka-discovery-space
./mvnw spring-boot:run

# Terminal 2 — API Gateway
cd Reward360Microservices/apigateway
./mvnw spring-boot:run

# Terminal 3 — User Service
cd Reward360Microservices/user-service
./mvnw spring-boot:run

# Terminal 4 — Customer Service
cd Reward360Microservices/CustomerMs
./mvnw spring-boot:run

# Terminal 5 — Promotions
cd Reward360Microservices/promotions
./mvnw spring-boot:run

# Terminal 6 — Fraud Detection
cd Reward360Microservices/Fraud_MS
./mvnw spring-boot:run

# Terminal 7 — Analytics
cd Reward360Microservices/AnalyticsService
./mvnw spring-boot:run
```

### Frontend

```powershell
# Terminal 8
cd Reward360Application/frontend
npm run dev
```

### Access Points

| Component | URL |
|-----------|-----|
| 🌐 **Frontend** | http://localhost:5173 |
| 🔀 **API Gateway** | http://localhost:8086 |
| 📡 **Eureka Dashboard** | http://localhost:8761 |
| 📘 **Swagger (Gateway)** | http://localhost:8086/swagger-ui.html |

---

## 🔀 API Gateway & Routing

All frontend requests go to the **API Gateway** (`:8086`), which routes them to the correct microservice:

| Path Pattern | Target Service | JWT Required |
|-------------|---------------|:------------:|
| `/auth/**` | user-service `:8087` | ❌ |
| `/user/**` | user-service `:8087` | ✅ |
| `/api/users/**` | CustomerMs `:8081` | ✅ |
| `/api/promotions/**` | promotions `:8083` | ✅ |
| `/api/v1/transactions/**` | Fraud_MS `:8082` | ✅ |
| `/api/analytics/**` | AnalyticsService `:8089` | ✅ |
| `/admin/**` | *(role check)* | ✅ ADMIN only |
| `/swagger/**`, `/v3/**` | *(docs)* | ❌ |

---

## 🔐 JWT Authentication Flow

```
┌────────────┐       ┌────────────┐       ┌──────────────┐       ┌────────┐
│   React    │       │  Gateway   │       │ user-service │       │  MySQL │
│  Frontend  │       │   :8086    │       │    :8087     │       │   DB   │
└─────┬──────┘       └─────┬──────┘       └──────┬───────┘       └───┬────┘
      │                    │                     │                   │
      │ POST /auth/login   │                     │                   │
      │ {email, password}  │                     │                   │
      │───────────────────▶│  (no JWT check)     │                   │
      │                    │────────────────────▶│  verify password  │
      │                    │                     │──────────────────▶│
      │                    │                     │  ✅ generate JWT  │
      │                    │  {token, role}      │                   │
      │◀───────────────────│◀────────────────────│                   │
      │                    │                     │                   │
      │ localStorage.setItem('token', jwt)       │                   │
      │                    │                     │                   │
      │ GET /api/users/1   │                     │                   │
      │ Authorization:     │                     │                   │
      │ Bearer eyJhb...    │                     │                   │
      │───────────────────▶│  JwtAuthFilter:     │                   │
      │                    │  ✅ verify token    │                   │
      │                    │  ✅ extract claims  │                   │
      │                    │  add X-User-* hdrs  │                   │
      │                    │────────────────────▶│                   │
      │  200 OK {data}     │                     │                   │
      │◀───────────────────│◀────────────────────│                   │
```

**Key points:**
- **Token generation** happens in `user-service` → `JwtUtil.java`
- **Token validation** happens in `apigateway` → `JwtAuthFilter.java`
- Both services share the **same JWT secret** (`jwt.secret` property)
- Tokens expire after **24 hours** (`86400000 ms`)
- The gateway forwards **`X-User-Id`**, **`X-User-Role`**, **`X-User-Email`** headers to downstream services
- Frontend **Axios interceptor** (`client.js`) auto-attaches the `Bearer` token to every request

> 📄 See [`JWTSecurity.txt`](Reward360Microservices/JWTSecurity.txt) for detailed flow diagrams and failure scenarios.

---

## 🖥 Frontend Pages & Features

### Public Pages
| Page | Route | Description |
|------|-------|-------------|
| Landing | `/` | Animated hero section, About Us, trust badges, CTA |
| Login | `/login` | Role toggle (User/Admin), email & password, role mismatch modal |
| Register | `/register` | Two-column form, preferences chips, communication preference |
| Forgot Password | `/forgot` | Password reset request |

### User Pages (Protected)
| Page | Route | Description |
|------|-------|-------------|
| Dashboard | `/user` | Points balance, tier status, charts, recent activity |
| Profile | `/user/profile` | View & edit profile details |
| Offers | `/user/offers` | Browse active offers, redeem with points |
| Redemptions | `/user/redemptions` | Redemption history |
| Transactions | `/user/transactions` | Transaction history |

### Admin Pages (Admin Role Required)
| Page | Route | Description |
|------|-------|-------------|
| Promotions | `/admin` | Manage promotions & campaigns |
| Offers Admin | `/admin/offers` | Create, edit, toggle, delete offers |
| Campaign Builder | `/admin/campaigns/new` | Build new campaigns |
| Fraud Monitor | `/admin/fraud` | Fraud alerts, anomalies, transaction audit |
| Reports | `/admin/reports` | Analytics dashboard, PDF & Excel export |

---

## 📡 API Endpoints

### Auth (Public — no JWT)
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/auth/register` | Register new user |
| `POST` | `/auth/login` | Login & receive JWT token |
| `GET` | `/auth/me` | Get current user info (needs token) |
| `GET` | `/auth/Users` | List all users |

### Customer / User
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/users/{id}` | Get customer profile |
| `GET` | `/api/users/{id}/transactions` | Get user transactions |
| `GET` | `/api/users/{id}/points` | Get points balance |
| `PUT` | `/api/users/{id}` | Update customer profile |

### Promotions / Offers
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/promotions/offers` | List all offers |
| `POST` | `/api/promotions/offers` | Create new offer |
| `PUT` | `/api/promotions/offers/{id}` | Update offer |
| `DELETE` | `/api/promotions/offers/{id}` | Delete offer |
| `POST` | `/api/promotions/offers/{id}/redeem` | Redeem an offer |

### Fraud Detection
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/transactions` | Get all transactions |
| `GET` | `/api/v1/transactions/flagged` | Get flagged transactions |

### Analytics
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/analytics/dashboard` | Dashboard analytics data |
| `GET` | `/api/analytics/reports` | Generate reports |

---

## 🔑 Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `JWT_SECRET` | Base64 dev key | Shared HMAC secret for JWT signing/verification |
| `MYSQL_ROOT_PASSWORD` | (save it in local system) | MySQL root password for database access |

> ⚠ **Production:** Always set `JWT_SECRET` to a strong random key (≥ 32 bytes). Never use the default dev key.

---

## 🐛 Troubleshooting

| Problem | Solution |
|---------|----------|
| **Services can't register with Eureka** | Make sure Eureka (`8761`) is started first |
| **401 Unauthorized on API calls** | Check that `JWT_SECRET` is the same in both `user-service` and `apigateway` `application.properties` |
| **403 Forbidden on admin routes** | Ensure you're logged in with an ADMIN role account |
| **CORS errors in browser** | API Gateway CORS is configured for `localhost:5173` and `5174`. Check the gateway's `application.properties` |
| **Frontend can't connect** | Ensure API Gateway is running on `:8086`. Frontend `client.js` uses `http://localhost:8086` as base URL |
| **Database connection errors** | Verify MySQL is running and credentials match in each service's `application.properties` |
| **Eureka dashboard shows no services** | Wait 30 seconds after starting services for heartbeat registration |

---

## 📄 License

This project is for Training purposes.

---

<p align="center">
  Built with ❤️ using Spring Boot + React
</p>
