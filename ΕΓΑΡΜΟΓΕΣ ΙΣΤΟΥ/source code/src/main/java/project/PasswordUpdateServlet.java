package hua.dit.web.project;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/password-update")
public class PasswordUpdateServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");

		final String username = trim(request.getParameter("username"));
		final String currentPassword = trim(request.getParameter("currentPassword"));
		final String newPassword1 = trim(request.getParameter("newPassword1"));
		final String newPassword2 = trim(request.getParameter("newPassword2"));
		final String newPassword3 = trim(request.getParameter("newPassword3"));

		try (PrintWriter out = response.getWriter()) {
			out.println("<!doctype html><html><head><meta charset='UTF-8'><title>Password Update</title><link rel='stylesheet' href='" + request.getContextPath() + "/css/styles.css'></head><body>");
			out.println("<main class='panel'><h1>Password update result</h1>");

			if (isBlank(username) || isBlank(currentPassword) || isBlank(newPassword1) || isBlank(newPassword2) || isBlank(newPassword3)) {
				out.println("<p class='error'>All fields are required.</p><p><a href='" + request.getContextPath() + "/update-password.html'>Go back</a></p></main></body></html>");
				return;
			}

			if (!newPassword1.equals(newPassword2) || !newPassword1.equals(newPassword3)) {
				out.println("<p class='error'>The new password values must be identical.</p><p><a href='" + request.getContextPath() + "/update-password.html'>Go back</a></p></main></body></html>");
				return;
			}

			if (newPassword1.equals(currentPassword)) {
				out.println("<p class='error'>The new password must be different from the existing one.</p><p><a href='" + request.getContextPath() + "/update-password.html'>Go back</a></p></main></body></html>");
				return;
			}

			if (!newPassword1.matches("^[A-Za-z0-9]{7,}$")) {
				out.println("<p class='error'>The new password must contain only letters and digits and be longer than 6 characters.</p><p><a href='" + request.getContextPath() + "/update-password.html'>Go back</a></p></main></body></html>");
				return;
			}

			final String oldHash = Util.getHash256(currentPassword);
			final String newHash = Util.getHash256(newPassword1);
			final int updated = DbUtil.updatePassword(username, oldHash, newHash);

			if (updated > 0) {
				out.println("<p class='success'>Password updated successfully.</p>");
			} else {
				out.println("<p class='error'>No matching user/password pair was found.</p>");
			}
			out.println("<p><a href='" + request.getContextPath() + "/login.html'>Continue to login</a></p></main></body></html>");
		} catch (SQLException e) {
			throw new IOException("Database error while updating password", e);
		}
	}

	private static String trim(String value) {
		return value == null ? null : value.trim();
	}

	private static boolean isBlank(String value) {
		return value == null || value.isBlank();
	}
}
