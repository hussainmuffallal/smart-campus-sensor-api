# Smart Campus Sensor API 🏢🔌

An enterprise-grade RESTful web service built to manage smart campus infrastructure, including rooms, IoT sensors, and real-time environmental telemetry data. 

Built entirely with raw **Java (JAX-RS)** and the **Grizzly HTTP Server** to demonstrate core backend architecture without relying on abstraction-heavy frameworks.

---

## 🏗️ Architectural Decisions & Patterns

As per the project constraints, no external database was used. The architecture relies on advanced Java concurrency and RESTful patterns to ensure reliability and thread safety.

* **In-Memory Data Storage (Singleton Pattern):** The database is simulated using a Singleton `DataStore` class. To prevent race conditions when multiple API requests occur simultaneously, data is stored using `ConcurrentHashMap` and `CopyOnWriteArrayList`.
* **Sub-Resource Locators:** To maintain clean relational hierarchy, `SensorReading` endpoints are dynamically routed through the `SensorResource` class, ensuring that telemetry data is strictly bound to valid, existing sensors.
* **Global Exception Handling:** A custom `@Provider` class (`GlobalExceptionMapper`) intercepts all unexpected server crashes and translates them into uniform, user-friendly JSON HTTP responses (e.g., `404 Not Found`, `409 Conflict`), preventing HTML stack-trace leaks.
* **API Observability (Filters):** Implemented JAX-RS `ContainerRequestFilter` and `ContainerResponseFilter` to act as an automated logging system, calculating and tracking the millisecond execution time of every HTTP request.

---

## 📡 API Endpoints

### Rooms (`/api/v1/rooms`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/rooms` | Retrieve all campus rooms. |
| `GET` | `/rooms/{roomId}` | Retrieve specific room metadata. |
| `POST` | `/rooms` | Register a new room. |
| `DELETE` | `/rooms/{roomId}` | Decommission a room (blocked if active sensors exist). |

### Sensors (`/api/v1/sensors`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/sensors` | Retrieve all sensors. |
| `GET` | `/sensors?type={type}` | Filter sensors by specific type (e.g., "Temperature"). |
| `POST` | `/sensors` | Register a new sensor to a room. |

### Sensor Readings (`/api/v1/sensors/{sensorId}/readings`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/{sensorId}/readings` | Retrieve all telemetry data for a specific sensor. |
| `POST` | `/{sensorId}/readings` | Record a new data reading (auto-generates timestamp). |

---

## 💻 Sample cURL Commands

Here are five sample commands to test the API directly from your terminal:

**1. Retrieve all rooms:**
`curl -X GET http://localhost:8080/api/v1/rooms`

**2. Register a new room:**
`curl -X POST http://localhost:8080/api/v1/rooms -H "Content-Type: application/json" -d '{"name": "Engineering Lab", "capacity": 40}'`

**3. Register a new sensor:**
`curl -X POST http://localhost:8080/api/v1/sensors -H "Content-Type: application/json" -d '{"type": "CO2", "status": "ACTIVE", "currentValue": 415.5, "roomId": "LIB-301"}'`

**4. Filter sensors by type:**
`curl -X GET "http://localhost:8080/api/v1/sensors?type=CO2"`

**5. Add a new sensor reading:**
`curl -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings -H "Content-Type: application/json" -d '{"value": 24.5}'`

---

## 🚀 How to Run Locally

1. **Clone the repository:**
   ```bash
   git clone https://github.com/hussainmuffallal/smart-campus-sensor-api.git

2. **Open the project** in Apache NetBeans.

3. **Resolve Maven Dependencies** (`pom.xml`).

4. **Run the Server:** 
    Navigate to `src/main/java/com/mycompany/smart/campus/api/Main.java` and execute the file.

5. **Test the API:** The server runs on `http://localhost:8080/api/v1/`. 
    Send a `GET` request to `/rooms` to verify the connection.

---

## 📝 Architectural & Conceptual Report

**1. Lifecycle of a JAX-RS Resource Class**
By default, JAX-RS creates a new instance of a Resource class for every single incoming HTTP request. The runtime does not treat it as a singleton. Because instances are constantly created and destroyed, any variables stored directly inside the Resource class will be lost between requests. To prevent data loss, I decoupled the storage by implementing a Singleton `DataStore` class. To prevent race conditions when multiple per-request threads access this shared DataStore simultaneously, I utilized thread-safe collections (`ConcurrentHashMap` and `CopyOnWriteArrayList`).

**2. HATEOAS and Hypermedia**
Providing Hypermedia (links within responses) is a hallmark of advanced RESTful design because it decouples the client from the server's specific URL routing. Instead of a client developer hardcoding URLs (which break if the API changes paths), the client navigates the API dynamically by following links provided by the server, much like a human clicking links on a webpage. This allows the API to evolve without breaking existing clients.

**3. Returning Lists: IDs vs. Full Objects**
When returning a list of rooms, returning only IDs drastically reduces network bandwidth and server processing time, which is ideal for massive collections. However, it forces the client to make N+1 subsequent API calls if they want to display the room names. Returning full objects increases the payload size and network latency but allows the client to render the UI immediately with a single HTTP request.

**4. DELETE Idempotency**
Yes, the DELETE operation in this implementation is idempotent. If a client mistakenly sends the exact same DELETE request for a room multiple times, the first request will successfully remove the room and return a `204 No Content`. The subsequent requests will check the DataStore, see the room is missing, and return a `404 Not Found`. Despite the different status codes, the server's internal state remains exactly the same (the room is absent), satisfying the definition of idempotency.

**5. @Consumes and Media Type Mismatches**
By explicitly using `@Consumes(MediaType.APPLICATION_JSON)`, we instruct the JAX-RS runtime to strictly evaluate the `Content-Type` header of incoming requests. If a client attempts to send `text/plain` or `application/xml`, the JAX-RS framework intercepts the request before it ever reaches the Java method. It automatically rejects the payload and returns an HTTP `415 Unsupported Media Type` error, protecting the application from parsing exceptions.

**6. Query Parameters vs. Path Parameters for Filtering**
In REST architecture, Path parameters (e.g., `/sensors/{id}`) are used to define the hierarchical identity of a specific, singular resource. Query parameters (e.g., `?type=CO2`) are used to sort, filter, or modify a collection. Using a query parameter is superior for searching because a filter is not a unique entity; it is an optional modifier applied to an existing collection endpoint.

**7. Architectural Benefits of Sub-Resource Locators**
The Sub-Resource Locator pattern enforces the Separation of Concerns principle. Instead of defining every nested path in one massive controller class (a "God Object"), delegating logic to separate classes keeps the parent `SensorResource` focused only on sensor metadata. It encapsulates the telemetry logic into `SensorReadingResource`, making the codebase significantly easier to read, maintain, and unit-test.

**8. HTTP 422 vs HTTP 404 for Dependency Validation**
An HTTP 404 implies that the target URI itself does not exist. HTTP 422 (Unprocessable Entity) is far more semantically accurate for a dependency failure. It tells the client: "The URI you posted to is correct, and the JSON syntax is perfectly valid, but the semantic instructions inside the payload cannot be processed because the foreign key reference (Room ID) is missing from the system."

**9. Cybersecurity Risks of Stack Traces**
Exposing internal Java stack traces gives attackers a blueprint of the backend architecture. A stack trace reveals internal package structures, exact framework versions (e.g., Jersey/Grizzly versions), database drivers, and local server file paths. Attackers can cross-reference these specific versions against known CVEs (Common Vulnerabilities and Exposures) databases to launch highly targeted exploit attacks.

**10. JAX-RS Filters vs. Manual Logging**
Using JAX-RS filters for cross-cutting concerns enforces the DRY (Don't Repeat Yourself) principle. If an architect manually inserts `Logger.info()` inside every resource method, the business logic becomes polluted with infrastructure code. Furthermore, developers might forget to add logs to new endpoints. A `ContainerRequestFilter` intercepts the HTTP pipeline globally, ensuring 100% observability across the entire API from a single, maintainable file.
