package chat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/chat")
public class ChatServlet extends HttpServlet {
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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String user1 = validateSession(req, resp);
        if (user1 == null) return;

        Params params = parseAndValidateParams(req, resp);
        if (params == null) return;

        List<Message> messages = fetchMessages(user1, params.receiver, params.limit, params.offset);
        writeMessagesJson(resp, messages);
    }

    private String validateSession(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }
        return (String) session.getAttribute("username");
    }

    private static class Params {
        String receiver;
        int limit;
        int offset;
    }

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

    private List<Message> fetchMessages(String user1, String user2, int limit, int offset) {
        return DBUtil.getMessages(user1, user2, limit, offset);
    }

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