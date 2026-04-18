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

## 🚀 How to Run Locally

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/hussainmuffallal/smart-campus-sensor-api.git](https://github.com/hussainmuffallal/smart-campus-sensor-api.git)