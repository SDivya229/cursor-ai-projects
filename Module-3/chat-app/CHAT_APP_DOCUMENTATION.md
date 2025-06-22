# Chat Application Documentation

---

## Overview

This chat application provides real-time and RESTful messaging between users, supporting user registration, authentication, and message persistence. It is built using Java Servlets, WebSockets, and an H2 database.

### Key Features
- User registration and login with secure password hashing (bcrypt)
- RESTful HTTP endpoints for sending and retrieving messages
- Real-time messaging via WebSockets
- Persistent message storage in H2 database
- Input validation and XSS protection
- Session-based authentication

### Dependencies
- Java Servlet API
- H2 Database
- org.java_websocket (for WebSocket server)
- jBCrypt (for password hashing)

### Configuration
| Environment Variable | Description | Default |
|---------------------|-------------|---------|
| `CHAT_DB_URL`       | JDBC URL for the database | `jdbc:h2:~/chatdb` |
| `CHAT_DB_USER`      | Database username        | `sa`               |
| `CHAT_DB_PASS`      | Database password        | (empty)            |

---

## Class: `DBUtil`

Utility class for all database operations: user management and message storage.

### Methods
- `static Connection getConnection()`
  - **Returns:** JDBC connection to the chat database
  - **Throws:** SQLException
  - **Usage:**
    ```java
    Connection conn = DBUtil.getConnection();
    ```
- `static boolean isValidUsername(String username)`
  - **Returns:** true if username is valid (alphanumeric/underscore, 3-32 chars)
- `static boolean isValidPassword(String password)`
  - **Returns:** true if password is valid (6-64 chars)
- `static String escapeHtml(String input)`
  - **Returns:** HTML-escaped string (prevents XSS)
- `static boolean registerUser(String username, String passwordHash)`
  - **Returns:** true if registration succeeded
  - **Example:**
    ```java
    DBUtil.registerUser("alice", hash);
    ```
- `static String getPasswordHash(String username)`
  - **Returns:** Password hash for the user, or null if not found
- `static void saveMessage(String sender, String receiver, String content)`
  - **Description:** Saves a message after validation and escaping
- `static List<Message> getMessages(String user1, String user2, int limit, int offset)`
  - **Returns:** List of messages exchanged between two users (paginated)
  - **Example:**
    ```java
    List<Message> msgs = DBUtil.getMessages("alice", "bob", 50, 0);
    ```

---

## Class: `ChatServlet`

Handles HTTP requests for sending and retrieving chat messages.

### Endpoints
- `POST /chat`
  - **Params:** `receiver`, `content`
  - **Auth:** Session required
  - **Errors:**
    - 401 Unauthorized if not logged in
    - 400 Bad Request for invalid input
- `GET /chat`
  - **Params:** `receiver`, `limit` (optional), `offset` (optional)
  - **Returns:** JSON array of messages
  - **Auth:** Session required
  - **Errors:**
    - 401 Unauthorized if not logged in
    - 400 Bad Request for invalid input

### Example Usage
```javascript
// Send a message
fetch('/chat', {method: 'POST', body: new URLSearchParams({receiver: 'bob', content: 'Hi Bob!'})});

// Get messages
fetch('/chat?receiver=bob&limit=50')
  .then(res => res.json())
  .then(messages => console.log(messages));
```

---

## Class: `Message`

Represents a single chat message.

### Fields
- `String sender` — Username of the sender
- `String content` — Message content
- `Timestamp timestamp` — Time sent

### Methods
- `String getSender()`
- `String getContent()`
- `Timestamp getTimestamp()`

### Example
```java
Message m = new Message("alice", "Hello!", new Timestamp(System.currentTimeMillis()));
```

---

## Class: `WebSocketServerImpl`

Provides real-time chat using WebSockets.

### Usage
```java
WebSocketServerImpl server = new WebSocketServerImpl(8080);
server.start();
```

### Features
- Broadcasts messages to all connected clients
- Basic rate limiting (500ms per message per connection)
- Basic authentication via handshake token

### Main Methods
- `onOpen(WebSocket conn, ClientHandshake handshake)` — Handles new connections
- `onClose(WebSocket conn, int code, String reason, boolean remote)` — Handles disconnections
- `onMessage(WebSocket conn, String message)` — Handles incoming messages, broadcasts to all
- `onError(WebSocket conn, Exception ex)` — Handles errors
- `onStart()` — Called when server starts

### Error Handling
- Sends `{ "error": "Rate limit exceeded" }` if messages are sent too quickly
- Closes connection if authentication token is missing

---

## Class: `AuthServlet`

Handles user registration and login.

### Endpoint
- `POST /auth`
  - **Params:** `action` (`register` or `login`), `username`, `password`
  - **Register:**
    - Hashes password with bcrypt
    - Stores user in database
    - Redirects to login page on success, register page on failure
  - **Login:**
    - Checks password against stored hash
    - Starts session on success
    - Redirects to chat page on success, login page on failure
  - **Errors:**
    - 400 Bad Request for invalid input
    - Redirects for success/failure

### Example Usage
```javascript
// Register
fetch('/auth', {method: 'POST', body: new URLSearchParams({action: 'register', username: 'alice', password: 'secret123'})});

// Login
fetch('/auth', {method: 'POST', body: new URLSearchParams({action: 'login', username: 'alice', password: 'secret123'})});
```

---

## Error Handling
- All endpoints validate input and return appropriate HTTP status codes
- WebSocket server enforces rate limiting and authentication
- Database errors are logged (not exposed to clients)

---

## Advanced Usage & Patterns
- **Pagination:** Use `limit` and `offset` on `/chat` GET endpoint for paginated message retrieval
- **Session Management:** AuthServlet sets `username` in session on login; required for all chat actions
- **Security:**
  - Passwords are hashed with bcrypt
  - User input is validated and escaped to prevent XSS and SQL injection

---

## API Reference Summary

### HTTP Endpoints
| Method | Path   | Params | Description |
|--------|--------|--------|-------------|
| POST   | /auth  | action, username, password | Register or login |
| POST   | /chat  | receiver, content         | Send message      |
| GET    | /chat  | receiver, limit, offset   | Get messages      |

### WebSocket
- Connect to: `ws://<host>:<port>/`
- Must provide authentication token in handshake (see code for details)

---

## License
This project is for educational/demo purposes. 