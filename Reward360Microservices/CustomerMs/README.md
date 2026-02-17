# CustomerMs (Customer Microservice)

This microservice manages customer profiles, points claims, redemptions and transactions. It exposes HTTP REST endpoints and registers with a Eureka discovery server (if available). Swagger (OpenAPI) UI is available for interactive API documentation.

## Quick facts
- Spring Boot application (Java 17)
- Runs by default on port: `8081` (configured in `src/main/resources/application.properties`)
- Registers with Eureka at `http://localhost:8761/eureka/` (if Eureka server is running)
- Swagger UI available at `/swagger-ui/index.html` (after app start)


## How to run
From the project root:

Build (skip tests):

```powershell
mvn -DskipTests clean package
```

Run with Maven:

```powershell
mvn -DskipTests spring-boot:run
```

Or run the generated WAR:

```powershell
java -jar target\CustomerMs-0.0.1-SNAPSHOT.war
```

Open the app logs to verify Eureka registration and Swagger availability.


## Base URL
All endpoints are under:

```
http://localhost:8081/api/users
```


## Endpoints and JSON shapes

Note: the controller currently expects certain request bodies (DTOs) — examples are below.

1) Redeem an Offer

- Endpoint:
  POST /api/users/redeem/offer/{offerId}/user/{userId}

- Description: Redeems an offer for a user. The controller reads the offer ID and store from the request body. The path contains `{offerId}` and `{userId}` but the code uses the `userId` path variable and `offerId` from the request body (so include offerId in the JSON body).

- Request body (application/json): RedeemRequest

```json
{
  "offerId": 123,
  "store": "StoreName"
}
```

- Successful response: HTTP 201 Created with a `Redemption` entity JSON. Example fields in the `Redemption` entity include:

```json
{
  "id": 10,
  "confirmationCode": "CONF-1670000000000",
  "transactionId": "RED-1670000000000",
  "date": "2026-02-10",
  "costPoints": 500,
  "offerTitle": "10% Off",
  "store": "StoreName",
  "user": {
    "id": 42,
    "name": "Alice",
    "email": "alice@example.com"
  }
}
```

Behavior: Points are deducted from the user's `CustomerProfile.pointsBalance`, a `Transaction` record (type `REDEMPTION`) is created, and a `Redemption` record is stored.


2) Claim Points (Claim an offer/code)

- Endpoint:
  POST /api/users/claim/user/{userId}

- Description: Adds points to the user's profile (a claim). The request body contains the claim details.

- Request body (application/json): ClaimRequest

```json
{
  "note": "Referral bonus",
  "activationcode": "ABC-123",
  "points": 250
}
```

- Successful response: HTTP 201 Created with the same `ClaimRequest` JSON (current implementation returns the incoming DTO). A `Transaction` record (type `CLAIM`) is created with `pointsEarned` set.

Behavior: Points are added to `CustomerProfile.pointsBalance`. A `Transaction` entry is created with an expiry of 1 year.


3) Get Transactions for a User

- Endpoint:
  GET /api/users/transactions/user/{userId}

- Response: JSON array of `Transaction` objects, e.g.

```json
[
  {
    "id": 5,
    "externalId": "RED-1670000000000",
    "type": "REDEMPTION",
    "pointsEarned": 0,
    "pointsRedeemed": 500,
    "store": "StoreName",
    "date": "2026-02-10",
    "expiry": null,
    "note": "Redeemed: 10% Off"
  }
]
```


4) Get Available Offers for a Tier

- Endpoint:
  GET /api/users/offers/teir/{tier}

- Note: The path uses `teir` (typo) in the controller. Call it as-is, e.g. `/api/users/offers/teir/gold`.

- Response: JSON array of `Offers` objects, fields include `id`, `title`, `category`, `description`, `costPoints`, `imageUrl`, `active`.

Example:
```json
[
  {
    "id": 1,
    "title": "10% Off",
    "category": "retail",
    "description": "10% off select items",
    "costPoints": 500,
    "imageUrl": "https://...",
    "active": true
  }
]
```


5) Get Redemptions for a User

- Endpoint:
  GET /api/users/redemptions/user/{userId}

- Response: JSON array of `Redemption` objects (see example above).


## Entities / DTOs used (brief)

- RedeemRequest
  - offerId: Long
  - store: String

- ClaimRequest
  - note: String
  - activationcode: String
  - points: int

- Redemption entity fields: id, confirmationCode, transactionId, date, costPoints, offerTitle, store, user
- Transaction entity fields: id, externalId, type, pointsEarned, pointsRedeemed, store, date, expiry, note, user
- Offers entity fields: id, title, category, description, costPoints, imageUrl, active


## Notes & Caveats

- Path quirk: The `redeem` endpoint mapping includes `{offerId}` as a path variable but the controller method reads `offerId` from the `RedeemRequest` body. To avoid inconsistency, include the `offerId` in the JSON body; the `{offerId}` path variable is ignored by the method signature.

- Typo in offers endpoint: `offers/teir/{tier}` — keep this exact path when calling until the controller is updated.

- Eureka registration: Ensure the Eureka server runs at `http://localhost:8761/` before starting this service if you want it to register.

- Swagger/OpenAPI: After the application starts, open `http://localhost:8081/swagger-ui/index.html` to explore and test endpoints.


## Example workflow

1. Start Eureka server (if using discovery).
2. Start this service:
```powershell
mvn -DskipTests spring-boot:run
```
3. Use Swagger UI to test endpoints or call them with `curl` / Postman.


## Next improvements (suggested)
- Make `offerId` usage consistent: either read it from the path or the body (pick one) and update signatures accordingly.
- Fix the `teir` typo to `tier` in the offers endpoint.
- Return more informative DTOs for claim endpoints (e.g., `ClaimOfferResponse` with transaction id, new balance) instead of echoing the request.
- Add endpoint security (JWT/OAuth) for production.


---

If you want, I can:
- Update the controller to consume `offerId` from the path (and remove it from the body) or vice versa.
- Fix the `teir` typo to `tier` and update usages.
- Improve the claim response to return transaction ID and new balance.

Which change would you like me to make next?