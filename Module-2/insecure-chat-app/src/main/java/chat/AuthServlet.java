package chat;

import org.mindrot.jbcrypt.BCrypt;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/auth")
public class AuthServlet extends HttpServlet {
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