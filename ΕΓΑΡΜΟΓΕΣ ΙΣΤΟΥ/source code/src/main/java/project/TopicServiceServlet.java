package hua.dit.web.project;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/api/topics")
public class TopicServiceServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("userId") == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			ServletUtil.sendResponseData(new ApiResponse(false, "Login required."), response);
			return;
		}

		Topic topic = ServletUtil.getRequestData(Topic.class, request);
		ApiResponse apiResponse = new ApiResponse();

		if (topic == null || isBlank(topic.getName()) || isBlank(topic.getDescription())) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			apiResponse.setSuccess(false);
			apiResponse.setMessage("Name and description are required.");
			ServletUtil.sendResponseData(apiResponse, response);
			return;
		}

		try {
			Integer topicId = DbUtil.insertTopic(topic.getName().trim(), topic.getDescription().trim());
			apiResponse.setSuccess(topicId != null);
			apiResponse.setTopicId(topicId);
			apiResponse.setMessage(topicId != null ? "Topic stored successfully." : "Topic could not be stored.");
			ServletUtil.sendResponseData(apiResponse, response);
		} catch (SQLException e) {
			throw new IOException("Database error while storing topic", e);
		}
	}

	private static boolean isBlank(String value) {
		return value == null || value.isBlank();
	}
}
