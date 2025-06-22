package chat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * ChatServlet handles HTTP requests for sending and retrieving chat messages.
 * <p>
 * <b>Purpose:</b> Provides RESTful endpoints for chat operations.
 * <br><b>Key Features:</b>
 * <ul>
 *   <li>POST /chat: Send a message to another user.</li>
 *   <li>GET /chat: Retrieve messages between users (paginated).</li>
 *   <li>Session-based authentication required.</li>
 * </ul>
 * <b>Usage:</b>
 * <pre>
 *   // Send message (POST)
 *   fetch('/chat', {method: 'POST', body: ...});
 *   // Get messages (GET)
 *   fetch('/chat?receiver=bob&limit=50');
 * </pre>
 * <b>Dependencies:</b> Requires HttpSession, DBUtil, and Message classes.
 */
@WebServlet("/chat")
public class ChatServlet extends HttpServlet {
    /**
     * Handles POST requests to send a chat message.
     * @param req HttpServletRequest with 'receiver' and 'content' params
     * @param resp HttpServletResponse
     * @throws ServletException
     * @throws IOException
     * <p><b>Errors:</b> 401 if not authenticated, 400 for invalid input.</p>
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        String sender = (String) session.getAttribute("username");
        String receiver = req.getParameter("receiver");
        String content = req.getParameter("content");
        if (!DBUtil.isValidUsername(receiver) || content == null || content.length() > 1000) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        DBUtil.saveMessage(sender, receiver, content);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Handles GET requests to retrieve messages between users.
     * @param req HttpServletRequest with 'receiver', 'limit', 'offset' params
     * @param resp HttpServletResponse (JSON array of messages)
     * @throws ServletException
     * @throws IOException
     * <p><b>Errors:</b> 401 if not authenticated, 400 for invalid params.</p>
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String user1 = validateSession(req, resp);
        if (user1 == null) return;

        Params params = parseAndValidateParams(req, resp);
        if (params == null) return;

        List<Message> messages = fetchMessages(user1, params.receiver, params.limit, params.offset);
        writeMessagesJson(resp, messages);
    }

    /**
     * Validates the session and returns the username if authenticated.
     * @param req HttpServletRequest
     * @param resp HttpServletResponse
     * @return Username if authenticated, null otherwise
     * @throws IOException
     */
    private String validateSession(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }
        return (String) session.getAttribute("username");
    }

    /**
     * Helper class for parsed GET parameters.
     */
    private static class Params {
        String receiver;
        int limit;
        int offset;
    }

    /**
     * Parses and validates GET parameters for message retrieval.
     * @param req HttpServletRequest
     * @param resp HttpServletResponse
     * @return Params object if valid, null otherwise
     * @throws IOException
     */
    private Params parseAndValidateParams(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Params params = new Params();
        params.receiver = req.getParameter("receiver");
        if (!DBUtil.isValidUsername(params.receiver)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        params.limit = 50;
        params.offset = 0;
        try {
            if (req.getParameter("limit") != null) params.limit = Integer.parseInt(req.getParameter("limit"));
            if (req.getParameter("offset") != null) params.offset = Integer.parseInt(req.getParameter("offset"));
        } catch (NumberFormatException ignored) {}
        return params;
    }

    /**
     * Fetches messages between two users using DBUtil.
     * @param user1 First user
     * @param user2 Second user
     * @param limit Max messages
     * @param offset Offset for pagination
     * @return List of Message objects
     */
    private List<Message> fetchMessages(String user1, String user2, int limit, int offset) {
        return DBUtil.getMessages(user1, user2, limit, offset);
    }

    /**
     * Writes a list of messages as a JSON array to the response.
     * @param resp HttpServletResponse
     * @param messages List of Message objects
     * @throws IOException
     */
    private void writeMessagesJson(HttpServletResponse resp, List<Message> messages) throws IOException {
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            out.print("[");
            boolean first = true;
            for (Message m : messages) {
                if (!first) out.print(",");
                out.print("{\"sender\":\"" + m.getSender() + "\",\"content\":\"" + m.getContent() + "\",\"timestamp\":\"" + m.getTimestamp() + "\"}");
                first = false;
            }
            out.print("]");
        }
    }
} 