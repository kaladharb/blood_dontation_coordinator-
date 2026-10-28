<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Initial Admin Setup</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<main class="login-card">
    <h1>Initial Admin Setup</h1>
    <p class="subtitle">This local-only setup is available once, until the first ADMIN account is created.</p>
    <% if (request.getAttribute("error") != null) { %>
        <p class="message error"><%= request.getAttribute("error") %></p>
    <% } %>
    <form method="post" action="${pageContext.request.contextPath}/admin-setup">
        <label for="username">Admin username</label>
        <input id="username" name="username" type="text" required pattern="[A-Za-z0-9_]{3,50}" autocomplete="username">
        <label for="password">Admin password</label>
        <input id="password" name="password" type="password" required minlength="8" autocomplete="new-password">
        <button type="submit">Create Admin Account</button>
    </form>
</main>
</body>
</html>
