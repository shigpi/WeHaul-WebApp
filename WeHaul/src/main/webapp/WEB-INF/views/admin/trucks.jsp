<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>WeHaul - Manage Trucks</title>
    <link href="${pageContext.request.contextPath}/resources/css/retro-style.css" rel="stylesheet">
</head>
<body>
    <div class="header">
        <h1>MANAGE TRUCKS</h1>
    </div>
    
    <jsp:include page="_admin_nav.jsp" />

    <div class="container">
        <h2>Truck Fleet</h2>
        <a href="#" class="btn" style="margin-bottom: 1rem;">+ ADD TRUCK</a>
        
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>License Plate</th>
                    <th>Make/Model</th>
                    <th>Year</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="truck" items="${trucks}">
                    <tr>
                        <td>${truck.truckId}</td>
                        <td>${truck.licensePlate}</td>
                        <td>${truck.make} ${truck.model}</td>
                        <td>${truck.year}</td>
                        <td>${truck.status}</td>
                        <td>
                            <a href="#" class="btn">EDIT</a>
                            <a href="#" class="btn" style="background: var(--secondary-color);">DELETE</a>
                        </td>
                    </tr>
                </c:forEach>
                 <c:if test="${empty trucks}">
                    <tr>
                        <td colspan="6">No trucks found.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>

    <button id="theme-switcher">🌙</button>
    <script src="${pageContext.request.contextPath}/resources/js/theme-switcher.js"></script>
</body>
</html>
