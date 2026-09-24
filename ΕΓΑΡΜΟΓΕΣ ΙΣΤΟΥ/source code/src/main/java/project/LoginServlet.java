package hua.dit.web.project;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");

		final String username = trim(request.getParameter("username"));
		final String password = trim(request.getParameter("password"));

		if (isBlank(username) || isBlank(password)) {
			response.sendRedirect("login.html?error=missing");
			return;
		}

		try {
			final Integer userId = DbUtil.authenticateUser(username, Util.getHash256(password));
			if (userId == null) {
				response.sendRedirect("login.html?error=invalid");
				return;
			}

			HttpSession session = request.getSession(true);
			session.setAttribute("userId", userId);
			session.setAttribute("username", username);
			response.sendRedirect(request.getContextPath() + "/menu");
		} catch (SQLException e) {
			throw new IOException("Database error while authenticating user", e);
		}
	}

	private static String trim(String value) {
		return value == null ? null : value.trim();
	}

	private static boolean isBlank(String value) {
		return value == null || value.isBlank();
	}
}
