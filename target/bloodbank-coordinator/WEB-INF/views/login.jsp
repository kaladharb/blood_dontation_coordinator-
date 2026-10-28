<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Blood Bank Coordinator - Access</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<main class="login-card">
    <h1>Blood Bank Coordinator</h1>
    <p class="subtitle">Emergency Medical Supply Coordinator</p>

    <% if (request.getAttribute("error") != null) { %>
        <p class="message error"><%= request.getAttribute("error") %></p>
    <% } %>
    <% if (request.getAttribute("message") != null) { %>
        <p class="message success"><%= request.getAttribute("message") %></p>
    <% } %>

    <form method="post" action="${pageContext.request.contextPath}/login">
        <label for="username">Username</label>
        <input id="username" name="username" type="text" required autocomplete="username">

        <label for="password">Password</label>
        <input id="password" name="password" type="password" required autocomplete="current-password">

        <button type="submit">Continue</button>
    </form>
    <hr>
    <h2>New user registration</h2>
    <form method="post" action="${pageContext.request.contextPath}/register">
        <label for="register-username">Username</label>
        <input id="register-username" name="username" type="text" required pattern="[A-Za-z0-9_]{3,50}">
        <label for="register-password">Password</label>
        <input id="register-password" name="password" type="password" required minlength="6">
        <button type="submit">Register as User</button>
    </form>
</main>
</body>
</html>
