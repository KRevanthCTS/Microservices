# Reward360 Microservices - Local Dev Guide

This README explains environment variables, ports, and a recommended startup order for local development of the Reward360 microservices in this workspace.

## Important environment variables

- `JWT_SECRET` - Shared HMAC secret used by services and the API Gateway to sign and verify JWT tokens. Use a sufficiently long secret (>= 32 bytes) or a base64-encoded key. Example:

  Windows (PowerShell):
  $env:JWT_SECRET = "this-is-a-dev-secret-that-is-long-enough-12345"

- Database connection strings for services that need them (if using MySQL/Postgres). See individual service `application.properties` files for keys such as `spring.datasource.url`, `spring.datasource.username`, `spring.datasource.password`.

## Common ports used (defaults in local application.properties)

- API Gateway: 8086
- CustomerMs: 8081
- Fraud_MS: 8082
- Promotionservice: 8083
- AnalyticsService: 8089
- user-service: 8087
- Eureka (discovery): 8761 (if used)

Make sure those ports are free or update the modules' `application.properties` files.

## Recommended local startup order

1. (Optional) Start Eureka discovery server (if you plan to use `lb://` routing). Otherwise the gateway is configured to use direct `http://localhost` URLs for local development.
2. Start CustomerMs (port 8081).
3. Start Promotionservice (port 8083).
4. Start user-service (port 8087).
5. Start other supporting services (Fraud_MS, AnalyticsService) if needed.
6. Start API Gateway (port 8086).
7. Start frontend (if present in `../Reward360Application/frontend`) using `npm run dev`.

Note: the gateway validates and forwards JWT tokens. The user-service also contains an inbound JWT validation filter for defense-in-depth.

## Login / token contract

- The login endpoint returns a JSON object that includes a `token` (JWT) and `role` and user info. The front-end expects a `token` string to be stored in localStorage and set on the `Authorization: Bearer <token>` header for subsequent requests.

- Example login response shape:

```json
{
  "token": "eyJ...",
  "role": "ADMIN",
  "user": { "userId": 1, "name": "Alice", "email": "alice@example.com" }
}
```

## Running tests

Each microservice contains its own unit tests. Example (from the microservice folder):

mvn -f user-service test

This will run the unit tests (including JWT util and filter tests added to `user-service`).

## Troubleshooting

- If you see 401 for authenticated endpoints, verify `JWT_SECRET` is identical for the gateway and the services.
- If a service fails to start, inspect its `application.properties` for port conflicts or missing datasource configuration.

## Next steps

- Add more integration tests to validate end-to-end JWT propagation through the gateway to downstream services.
- Harden secrets handling and consider using a local vault or environment-specific configuration.

---

If you want, I can also add a small script/batch file to start the common services in the correct order.
