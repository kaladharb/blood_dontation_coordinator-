<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, com.bloodbank.model.*" %>
<%
    List<BloodInventory> inventory = (List<BloodInventory>) request.getAttribute("inventory");
    List<BloodRequest> requests = (List<BloodRequest>) request.getAttribute("requests");
    String flash = (String) request.getSession().getAttribute("flash");
    request.getSession().removeAttribute("flash");
%>
<!DOCTYPE html>
<html lang="en"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>User Dashboard</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css"></head>
<body><main class="dashboard">
<header><div><h1>User Dashboard</h1><p>Blood availability and emergency requests</p></div><a class="button" href="${pageContext.request.contextPath}/logout">Logout</a></header>
<% if (flash != null) { %><p class="message error"><%= flash %></p><% } %>
<section class="panel"><h2>Available blood</h2><div class="cards">
<% for (BloodInventory item : inventory) { %><div class="blood-card"><strong><%= item.getBloodGroup() %></strong><span><%= item.getUnits() %> units</span></div><% } %>
</div></section>
<section class="panel"><h2>Emergency blood request</h2>
<form method="post" action="${pageContext.request.contextPath}/request-blood" class="grid-form">
<label>Blood group<select name="bloodGroup" required><option value="">Select</option><option>A+</option><option>A-</option><option>B+</option><option>B-</option><option>AB+</option><option>AB-</option><option>O+</option><option>O-</option></select></label>
<label>Units<input name="units" type="number" min="1" max="100" required></label>
<label class="wide">Reason<textarea name="reason" minlength="5" maxlength="500" required></textarea></label>
<button type="submit">Submit emergency request</button></form></section>
<section class="panel"><h2>My requests</h2><table><tr><th>Group</th><th>Units</th><th>Reason</th><th>Status</th></tr>
<% for (BloodRequest item : requests) { %><tr><td><%= item.getBloodGroup() %></td><td><%= item.getUnits() %></td><td><%= item.getReason() %></td><td><%= item.getStatus() %></td></tr><% } %></table></section>
</main></body></html>
