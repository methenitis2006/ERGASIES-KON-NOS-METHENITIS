<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%
  HttpSession httpSession = request.getSession(false);
  String username = httpSession == null ? null : (String) httpSession.getAttribute("username");
  if (username == null) {
    response.sendRedirect(request.getContextPath() + "/login.html");
    return;
  }
%>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Main Menu</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css?v=4">
</head>
<body>
  <main class="panel form-shell">
    <p class="eyebrow">Authorized area</p>
    <h1>Main Menu</h1>
    <p class="lead">Welcome, <strong><%= username %></strong>.</p>
    <div class="actions">
      <a class="button primary" href="<%= request.getContextPath() %>/messages">Send Message</a>
      <a class="button secondary" href="<%= request.getContextPath() %>/new-topic.html">Create Topic</a>
      <a class="button secondary" href="<%= request.getContextPath() %>/logout">Logout</a>
    </div>
  </main>
</body>
</html>
