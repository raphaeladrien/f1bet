# Betting API Documentation

# Architectural Decisions for the Challenge

For this challenge, the following architectural choices were made:

---

## Database
We chose to use **H2 Database**.

- **Reasoning**:  
  H2 is lightweight, requires no external setup, and is ideal for local development and testing scenarios.

- **Production Consideration**:  
  In a real-world application, this could easily be replaced by a production-ready relational database such as **PostgreSQL**, **MySQL**, or another enterprise solution.

---

## Caching
We used **Caffeine** as the caching solution.

- **Reasoning**:  
  Caffeine is simple to integrate, provides efficient in-memory caching, and suits the needs of this challenge.

- **Production Consideration**:  
  For a real production environment, a **distributed cache** would be more appropriate to handle scalability and availability requirements.  
  A common option in this case would be **Redis**.

---

## Bet Processing After Event Outcome
The process of handling bets after an event outcome was extracted into a **scheduled job**.

- **Reasoning**:  
  We cannot process all bets immediately at the moment the event outcome is received, so we opted for a background approach.

- **Implementation in the Challenge**:
    - Bet results are stored in a table.
    - A scheduled job runs every **15 seconds** to check for new results and process pending bets.

- **Production Consideration**:  
  In a real-world application, this would most likely be handled by a **dedicated worker service**, leveraging a **message broker** such as **Kafka** or **RabbitMQ** for scalability, reliability, and decoupling.

---

# API basic usage

This guide explains how to interact with the Betting API, including how to retrieve events and place bets.

1. Retrieve Events

### Endpoint
```http
GET /api/v1/events
```

### Description
Retrieves a list of available events. Supports filters and pagination.

| Parameter     | Type   | Required | Default | Description                                              |
| ------------- | ------ | -------- | ------- | -------------------------------------------------------- |
| `sessionType` | string | No       | —       | Filters by session type (e.g., "qualification", "race"). |
| `year`        | int    | No       | —       | Filters events by year.                                  |
| `country`     | string | No       | —       | Filters events by country code or name.                  |
| `page`        | int    | No       | 0       | Page index (starting at 0).                              |
| `size`        | int    | No       | 10      | Number of results per page.                              |

### Example Request
```http
GET /api/v1/events?sessionType=Race&year=2023&country=BRA&page=0&size=10
```
### Example Response

```json
{
"event": [{
      "type": "Race",
      "name": "Sprint",
      "country": "Brazil",
      "circuit": "Interlagos",
      "sessionKey": 9204,
      "year": 2023,
      "id": "d77fd881-d3fc-48d3-9d91-cfa868678649",
      "drivers": [
        {
          "name": "Max VERSTAPPEN",
          "number": 1,
          "odd": 3
        },....
      }, ...
      }]
      "page": 0,
      "size": 10,
      "totalElements": 125
}
```

**!Important: The id field from each event will be required to place a bet.**

2. Place a bet

### Endpoint
```http
POST /api/v1/odds
```

### Headers
| Header            | Required | Description                                                                                         |
| ----------------- | -------- | --------------------------------------------------------------------------------------------------- |
| `Idempotency-Key` | Yes      | A unique key per request to prevent duplicate bets. Example: `550e8400-e29b-41d4-a716-446655440000` |

### Request Body

```json
{
"userId": 2,
"amount": 100,
"eventId": "d77fd881-d3fc-48d3-9d91-cfa868678649",
"driverNumber": 1
}
```
### Fields
* userId (int): ID of the user placing the bet. 
* amount (number): The bet amount. 
* eventId (string): Event ID obtained from the /events endpoint. 
* driverNumber (int): The driver number being bet on.

**!Important: To test proposes 3 users were created in database with the IDs 1,2 and 3.**

### Example of response

```json
{
"requestId": "f1f6342e-70e5-448f-b300-f07767538317"
}
```

## Summary

1. Call GET /api/v1/events to list events.
2. Use the id from the event in your bet request.
3. Place a bet with POST /api/v1/odds, including Idempotency-Key to ensure safe and unique bet submissions.

---

## Running the Application

To run the application, it is required to have **Docker** and **Docker Compose** installed on your machine.

### Build and Start the Application

```bash
docker-compose build f1bet
docker-compose up f1bet
```

Run the Tests

It is also possible to run the tests using Docker Compose:

```bash
docker-compose run tests
```
---

## API Documentation

By default, Swagger UI is available when running the application locally.

```bash
http://localhost:8080/swagger-ui/index.html
```





