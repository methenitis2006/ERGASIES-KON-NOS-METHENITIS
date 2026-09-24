package hua.dit.web.project;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/menu")
public class MainMenuServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("userId") == null) {
			response.sendRedirect(request.getContextPath() + "/login.html");
			return;
		}

		RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/menu.jsp");
		try {
			rd.forward(request, response);
		} catch (Exception e) {
			throw new IOException("Could not forward to menu.jsp", e);
		}
	}
}
