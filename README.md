# Smart Campus API

## Overview
A RESTful API built with JAX-RS (Jersey) and Grizzly HTTP server for managing campus rooms and sensors.

## How to Build and Run

1. Make sure you have JDK 21 and Maven installed
2. Clone this repository
3. Open in NetBeans as a Maven project
4. Run the project - the server starts at http://localhost:8080/api/v1/

## Sample curl Commands

### Get all rooms
curl -X GET http://localhost:8080/api/v1/rooms

### Create a room
curl -X POST http://localhost:8080/api/v1/rooms -H "Content-Type: application/json" -d "{\"id\":\"ROOM-101\",\"name\":\"Computer Lab\",\"capacity\":30}"

### Get all sensors
curl -X GET http://localhost:8080/api/v1/sensors

### Create a sensor
curl -X POST http://localhost:8080/api/v1/sensors -H "Content-Type: application/json" -d "{\"id\":\"TEMP-001\",\"type\":\"Temperature\",\"status\":\"ACTIVE\",\"currentValue\":22.5,\"roomId\":\"LIB-301\"}"

### Get sensor readings
curl -X GET http://localhost:8080/api/v1/sensors/TEMP-001/readings

## Answers to Questions

### Part 1 - Service Architecture & Setup

**JAX-RS Resource Lifecycle:**
By default, JAX-RS creates a new instance of a resource class for every incoming HTTP request (per-request lifecycle). This means each request gets its own object instance. Because of this, shared data cannot be stored as instance variables in resource classes. Instead, this API uses a static DataStore class with ConcurrentHashMap to store rooms, sensors, and readings safely across requests, preventing data loss and race conditions.

**HATEOAS:**
Hypermedia as the Engine of Application State (HATEOAS) is considered advanced REST design because it makes APIs self-describing. Instead of clients needing static documentation to know what actions are available, responses include links to related resources. This benefits developers by allowing them to discover API capabilities dynamically, reducing coupling between client and server.

### Part 2 - Room Management

**Full objects vs IDs:**
Returning full room objects gives clients all the data they need in one request, reducing round trips. Returning only IDs is more bandwidth-efficient but forces clients to make additional requests for each room's details. For small datasets, returning full objects is preferred. For large datasets, returning IDs with pagination is better.

**DELETE idempotency:**
Yes, DELETE is idempotent in this implementation. The first DELETE on a room removes it and returns 204 No Content. Any subsequent DELETE on the same room ID returns 404 Not Found. While the response codes differ, the end state is the same - the room does not exist - which satisfies idempotency.

### Part 3 - Sensor Operations

**@Consumes annotation:**
If a client sends data in a format other than application/json (e.g. text/plain or application/xml), JAX-RS returns a 415 Unsupported Media Type error automatically. The framework checks the Content-Type header and rejects requests that don't match the declared @Consumes type before the method is even called.

**@QueryParam vs path parameter:**
Query parameters like ?type=CO2 are better for filtering because they are optional, can be combined with other filters, and don't change the resource's identity. Path parameters like /sensors/type/CO2 imply that "type/CO2" is a distinct resource, which is semantically incorrect for filtering. Query parameters follow REST conventions for search and filter operations.

### Part 4 - Sub-Resources

**Sub-Resource Locator Pattern:**
The sub-resource locator pattern improves maintainability by delegating nested resource handling to dedicated classes. Instead of one massive controller handling every path, each class has a single responsibility. SensorReadingResource only handles readings logic, making it easier to test, modify, and understand independently of the parent SensorResource.

### Part 5 - Error Handling

**HTTP 422 vs 404:**
HTTP 422 Unprocessable Entity is more semantically accurate than 404 when a referenced resource ID doesn't exist inside a valid JSON payload. A 404 means the requested URL was not found. A 422 means the request was understood and well-formed, but the business logic failed because a referenced entity (the roomId) does not exist. The distinction helps clients understand whether the URL is wrong or the data is wrong.

**Security risks of exposing stack traces:**
Exposing Java stack traces reveals internal class names, package structure, library versions, and server configuration. Attackers can use this to identify known vulnerabilities in specific library versions, understand the application architecture, and craft targeted attacks. The GlobalExceptionMapper prevents this by returning generic error messages.

**Logging filters vs manual logging:**
Using JAX-RS filters for cross-cutting concerns like logging is better because the logging logic is written once and applied to every request automatically. Manual Logger.info() calls in every method would be repetitive, easy to forget, and harder to maintain. Filters follow the separation of concerns principle and keep resource methods focused on business logic.# -SmartCampusAPI
