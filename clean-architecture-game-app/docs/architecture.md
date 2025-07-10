# Clean Architecture Game App

## Architecture Decisions

The project follows Clean Architecture principles, separating concerns into four main layers:

- **Domain Layer**: Contains core business logic, entities, and domain services. Pure Java, no dependencies on frameworks.
- **Application Layer**: Orchestrates use cases, application services, and interfaces for input/output. Coordinates domain logic and infrastructure.
- **Infrastructure Layer**: Handles technical details such as database access (PostgreSQL), caching (Redis), external APIs, and messaging.
- **API Layer**: Exposes RESTful and WebSocket APIs using Spring Boot. Handles HTTP requests, authentication, and response formatting.
- **Frontend**: React SPA for user interaction, keyboard controls, transitions, and real-time updates.

## Technology Stack

- **Backend**: Java, Spring Boot, Spring Security, Spring Data JPA, WebSocket, Spring Cloud Gateway
- **Database**: PostgreSQL
- **Cache/Session**: Redis
- **Frontend**: React.js (SPA), modern CSS (e.g., styled-components or TailwindCSS), WebSocket client
- **API Gateway**: Spring Cloud Gateway
- **DevOps/Monitoring**: Prometheus, Grafana (for performance monitoring)

## Key Features & Requirements

- **Keyboard Controls**: ← and → for navigation, P to pause, R to restart
- **Single Page View**: All content loads dynamically in one React SPA
- **Smooth Transitions**: Slide/fade effects between screens
- **Progress Indicator**: Shows current level/question (e.g., 2/10)
- **Score/Timer Display**: Tracks user score and time
- **Pause & Restart**: Pause/resume and restart via keyboard

## Data Flow Diagram

```mermaid
flowchart TD
    subgraph Frontend (React SPA)
        A[User Input (Keyboard)]
        B[UI Components]
        C[WebSocket Client]
    end
    subgraph API Layer (Spring Boot)
        D[REST Controllers]
        E[WebSocket Gateway]
        F[Auth Middleware]
    end
    subgraph Application Layer
        G[Use Cases]
        H[Application Services]
    end
    subgraph Domain Layer
        I[Entities]
        J[Domain Services]
    end
    subgraph Infrastructure Layer
        K[PostgreSQL]
        L[Redis]
        M[External APIs]
    end
    A --> B
    B --> D
    B --> C
    C --> E
    D --> F --> G
    E --> G
    G --> H
    H --> I
    H --> J
    H --> K
    H --> L
    H --> M
    K --> H
    L --> H
    M --> H
    F --> L
```

## Rationale

- **Separation of Concerns**: Each layer has a clear responsibility, making the system maintainable and testable.
- **Scalability**: API Gateway, Redis caching, and stateless services support scaling.
- **Real-Time**: WebSocket for instant updates and notifications.
- **Performance**: Caching, DB optimization, and monitoring ensure responsiveness.

---

Further details and diagrams will be added as implementation progresses.
