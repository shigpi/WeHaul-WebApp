<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>WeHaul - Retro Rentals</title>
    <link href="${pageContext.request.contextPath}/resources/css/retro-style.css" rel="stylesheet">
</head>
<body>
    <div class="header">
        <h1>WE_HAUL</h1>
    </div>
    
    <div class="container">
        <h2>Rent-A-Truck!</h2>
        <div class="pixel-image">
            <img src="${pageContext.request.contextPath}/resources/images/pixel-truck.png" alt="Pixelated Truck" style="image-rendering: pixelated; width: 100%; max-width: 300px; height: auto;">
        </div>
        <p>Need to move stuff? We got the pixels! Rent a retro truck today!</p>
        
        <div style="text-align: center; margin-top: 1.5rem;">
            <a href="${pageContext.request.contextPath}/customer/login" class="btn">LOGIN</a>
            <a href="${pageContext.request.contextPath}/customer/register" class="btn">REGISTER</a>
        </div>
    </div>

    <button id="theme-switcher">🌙</button>
    <script src="${pageContext.request.contextPath}/resources/js/theme-switcher.js"></script>
</body>
</html>
