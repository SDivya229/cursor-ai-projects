# Clean Architecture Game App

A modern web application built with Clean Architecture principles, featuring a React frontend and Spring Boot backend with real-time capabilities.

## 🎮 Features

- **User Authentication**: Secure registration and login system
- **Real-time Updates**: WebSocket support for instant notifications
- **Keyboard Controls**: ← and → for navigation, P to pause, R to restart
- **Single Page Application**: Smooth transitions and dynamic content loading
- **Progress Tracking**: Score and timer display with level indicators
- **Pause & Restart**: Full game control via keyboard shortcuts

## 🏗️ Architecture

This project follows Clean Architecture principles with four main layers:

- **Domain Layer**: Core business logic and entities (Pure Java)
- **Application Layer**: Use cases and application services
- **Infrastructure Layer**: Database, caching, and external integrations
- **API Layer**: RESTful and WebSocket APIs using Spring Boot
- **Frontend**: React SPA with modern UI/UX

## 🛠️ Technology Stack

### Backend
- **Java** with Spring Boot
- **Spring Security** for authentication
- **Spring Data JPA** for database operations
- **WebSocket** for real-time communication
- **PostgreSQL** for data persistence
- **Redis** for caching and sessions

### Frontend
- **React.js** (SPA)
- **React Router** for navigation
- **Axios** for API communication
- **Modern CSS** (styled-components/TailwindCSS)

### DevOps & Monitoring
- **Prometheus** for metrics collection
- **Grafana** for performance monitoring
- **Spring Cloud Gateway** for API routing

## 🚀 Quick Start

### Prerequisites
- Java 17 or later
- Node.js 16 or later
- PostgreSQL
- Redis
- Maven

### Backend Setup
1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Configure database connection in `application.properties`

3. Start the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```

### Frontend Setup
1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm start
   ```

4. Open [http://localhost:3000](http://localhost:3000) in your browser

## 📁 Project Structure

```
clean-architecture-game-app/
├── backend/           # Spring Boot application
├── frontend/          # React SPA
├── domain/            # Core business logic
├── application/       # Use cases and services
├── infrastructure/    # Database and external services
├── api/              # API layer
└── docs/             # Architecture documentation
```

## 🔧 Development

### Running Tests
```bash
# Backend tests
cd backend && mvn test

# Frontend tests
cd frontend && npm test
```

### Building for Production
```bash
# Backend
cd backend && mvn clean package

# Frontend
cd frontend && npm run build
```

## 📊 Monitoring

The application includes comprehensive monitoring:
- **Prometheus** metrics collection
- **Grafana** dashboards for visualization
- **Health checks** for all services

## 🔐 Security

- JWT-based authentication
- Role-based access control
- Secure password hashing
- CORS configuration
- Input validation and sanitization

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For questions or issues:
- Check the [documentation](docs/)
- Review the [architecture guide](docs/architecture.md)
- Open an issue on GitHub

---

**Built with ❤️ using Clean Architecture principles** 