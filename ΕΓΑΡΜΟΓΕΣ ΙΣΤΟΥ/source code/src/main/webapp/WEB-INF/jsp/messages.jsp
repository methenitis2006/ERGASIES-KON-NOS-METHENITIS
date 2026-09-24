<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="hua.dit.web.project.Topic" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%
  HttpSession httpSession = request.getSession(false);
  Integer userId = httpSession == null ? null : (Integer) httpSession.getAttribute("userId");
  String username = httpSession == null ? null : (String) httpSession.getAttribute("username");
  if (userId == null) {
    response.sendRedirect(request.getContextPath() + "/login.html");
    return;
  }
  List<Topic> topics = (List<Topic>) request.getAttribute("topics");
  String notice = (String) request.getAttribute("notice");
  Integer selectedTopicId = (Integer) request.getAttribute("selectedTopicId");
%>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Send Message</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css?v=4">
</head>
<body>
  <main class="panel form-shell">
    <p class="eyebrow">Authorized area</p>
    <h1>Send a Message</h1>
    <p class="meta">Logged in as <strong><%= username %></strong> (user id <%= userId %>).</p>
    <% if (notice != null) { %>
      <div class="notice"><%= notice %></div>
    <% } %>
    <form action="<%= request.getContextPath() %>/messages" method="post">
      <label>Topic
        <select name="topicId" required>
          <option value="">Choose a topic</option>
          <% if (topics != null) { for (Topic topic : topics) { %>
            <option value="<%= topic.getId() %>" <%= (selectedTopicId != null && selectedTopicId.intValue() == topic.getId()) ? "selected" : "" %>><%= topic.getName() %></option>
          <% } } %>
        </select>
      </label>
      <label>Message
        <textarea name="message" rows="5" maxlength="256" required></textarea>
      </label>
      <button class="button primary" type="submit">Submit Message</button>
    </form>
    <p class="small-links"><a href="<%= request.getContextPath() %>/menu">Main menu</a> · <a href="<%= request.getContextPath() %>/logout">Logout</a></p>
  </main>
</body>
</html>
