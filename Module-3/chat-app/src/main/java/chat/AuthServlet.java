package chat;

import org.mindrot.jbcrypt.BCrypt;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * AuthServlet handles user registration and login for the chat application.
 * <p>
 * <b>Purpose:</b> Provides authentication endpoints for users.
 * <br><b>Key Features:</b>
 * <ul>
 *   <li>POST /auth?action=register: Register a new user.</li>
 *   <li>POST /auth?action=login: Authenticate and start a session.</li>
 *   <li>Uses bcrypt for password hashing.</li>
 * </ul>
 * <b>Usage:</b>
 * <pre>
 *   // Register
 *   fetch('/auth', {method: 'POST', body: ...});
 *   // Login
 *   fetch('/auth', {method: 'POST', body: ...});
 * </pre>
 * <b>Dependencies:</b> Requires DBUtil and jBCrypt.
 */
@WebServlet("/auth")
public class AuthServlet extends HttpServlet {
    /**
     * Handles POST requests for user registration and login.
     * @param req HttpServletRequest with 'action', 'username', 'password' params
     * @param resp HttpServletResponse
     * @throws ServletException
     * @throws IOException
     * <p><b>Errors:</b> 400 for invalid input, redirects for success/failure.</p>
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (!DBUtil.isValidUsername(username) || !DBUtil.isValidPassword(password)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid input");
            return;
        }

        if ("register".equals(action)) {
            String hash = BCrypt.hashpw(password, BCrypt.gensalt(10)); // Stronger salt rounds
            boolean success = DBUtil.registerUser(username, hash);
            if (success) {
                resp.sendRedirect("/static/login.html?msg=Registered");
            } else {
                resp.sendRedirect("/static/register.html?err=UserExists");
            }
        } else if ("login".equals(action)) {
            String storedHash = DBUtil.getPasswordHash(username);
            if (storedHash != null && BCrypt.checkpw(password, storedHash)) {
                HttpSession session = req.getSession();
                session.setAttribute("username", username);
                resp.sendRedirect("/static/chat.html");
            } else {
                resp.sendRedirect("/static/login.html?err=Invalid");
            }
        }
    }
} 