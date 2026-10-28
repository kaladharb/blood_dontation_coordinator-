<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, com.bloodbank.model.*" %>
<% List<BloodInventory> inventory = (List<BloodInventory>) request.getAttribute("inventory"); List<BloodRequest> pending = (List<BloodRequest>) request.getAttribute("pending"); String flash = (String) request.getAttribute("flash"); %>
<!DOCTYPE html>
<html lang="en"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>Admin Dashboard</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css"></head>
<body><main class="dashboard"><header><div><h1>Admin Dashboard</h1><p>Manage blood inventory and emergency requests</p></div><nav><a class="button" href="${pageContext.request.contextPath}/admin">Dashboard</a> <a class="button" href="${pageContext.request.contextPath}/logout">Logout</a></nav></header>
<% if (flash != null) { %><p class="message error"><%= flash %></p><% } %>
<section class="panel"><h2>Update blood inventory</h2><form method="post" action="${pageContext.request.contextPath}/admin" class="inline-form"><input type="hidden" name="action" value="inventory"><select name="bloodGroup" required><option value="">Blood group</option><option>A+</option><option>A-</option><option>B+</option><option>B-</option><option>AB+</option><option>AB-</option><option>O+</option><option>O-</option></select><input name="units" type="number" min="0" max="100000" required placeholder="Units"><button type="submit">Save inventory</button></form>
<table><tr><th>Blood group</th><th>Units available</th></tr><% for (BloodInventory item : inventory) { %><tr><td><%= item.getBloodGroup() %></td><td><%= item.getUnits() %></td></tr><% } %></table></section>
<section class="panel"><h2>Pending requests</h2><table><tr><th>User</th><th>Group</th><th>Units</th><th>Reason</th><th>Action</th></tr>
<% for (BloodRequest item : pending) { %><tr><td><%= item.getRequester().getUsername() %></td><td><%= item.getBloodGroup() %></td><td><%= item.getUnits() %></td><td><%= item.getReason() %></td><td><form method="post" action="${pageContext.request.contextPath}/admin" class="action-form"><input type="hidden" name="action" value="request"><input type="hidden" name="requestId" value="<%= item.getId() %>"><button name="decision" value="APPROVED" type="submit">Approve</button><button name="decision" value="REJECTED" type="submit" class="reject">Reject</button></form></td></tr><% } %></table></section>
</main></body></html>
