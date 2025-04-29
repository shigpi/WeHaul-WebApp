<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>WeHaul - Dashboard</title>
    <link href="${pageContext.request.contextPath}/resources/css/retro-style.css" rel="stylesheet">
</head>
<body>
    <div class="header">
        <h1>DASHBOARD</h1>
    </div>
    
    <jsp:include page="_customer_nav.jsp" />

    <div class="container">
        <h2>Welcome, ${customer.firstName}!</h2>
        <p>Ready to haul some pixels?</p>
        
        <div class="pixel-image">
             <img src="${pageContext.request.contextPath}/resources/images/pixel-fleet.png" alt="Pixelated Truck Fleet" style="image-rendering: pixelated; width: 100%; max-width: 400px; height: auto;">
        </div>

        <h3>Quick Actions:</h3>
        <ul>
            <li><a href="#" class="btn">BOOK TRUCK</a></li>
            <li><a href="${pageContext.request.contextPath}/customer/rentals" class="btn">MY RENTALS</a></li>
        </ul>
    </div>

    <button id="theme-switcher">🌙</button>
    <script src="${pageContext.request.contextPath}/resources/js/theme-switcher.js"></script>
</body>
</html>
