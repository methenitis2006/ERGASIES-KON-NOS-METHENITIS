package hua.dit.web.project;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/messages")
public class MessageServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("userId") == null) {
			response.sendRedirect(request.getContextPath() + "/login.html");
			return;
		}

		try {
			List<Topic> topics = DbUtil.getTopics();
			request.setAttribute("topics", topics);
			RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/messages.jsp");
			rd.forward(request, response);
		} catch (SQLException e) {
			throw new IOException("Database error while loading topics", e);
		} catch (Exception e) {
			throw new IOException("Could not forward to messages.jsp", e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("userId") == null) {
			response.sendRedirect(request.getContextPath() + "/login.html");
			return;
		}

		final String topicIdValue = request.getParameter("topicId");
		final String message = trim(request.getParameter("message"));
		final Integer userId = (Integer) session.getAttribute("userId");

		try {
			List<Topic> topics = DbUtil.getTopics();
			request.setAttribute("topics", topics);

			if (isBlank(topicIdValue) || isBlank(message)) {
				request.setAttribute("notice", "Topic and message are required.");
				RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/messages.jsp");
				rd.forward(request, response);
				return;
			}

			final int topicId = Integer.parseInt(topicIdValue);
			DbUtil.insertMessage(topicId, userId, message);
			final int count = DbUtil.countMessagesForTopic(topicId);
			request.setAttribute("notice", "Message saved successfully. Total messages for this topic: " + count);
			request.setAttribute("selectedTopicId", topicId);
			request.setAttribute("messageCount", count);
			RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/messages.jsp");
			rd.forward(request, response);
		} catch (NumberFormatException e) {
			throw new IOException("Invalid topic id", e);
		} catch (SQLException e) {
			throw new IOException("Database error while storing message", e);
		} catch (Exception e) {
			throw new IOException("Could not forward to messages.jsp", e);
		}
	}

	private static String trim(String value) {
		return value == null ? null : value.trim();
	}

	private static boolean isBlank(String value) {
		return value == null || value.isBlank();
	}
}
