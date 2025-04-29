<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>WeHaul - Register</title>
    <link href="${pageContext.request.contextPath}/resources/css/retro-style.css" rel="stylesheet">
</head>
<body>
    <div class="header">
        <h1>REGISTER</h1>
    </div>
    
    <div class="container">
         <div class="pixel-image" style="text-align: center; margin-bottom: 1rem;">
             <img src="${pageContext.request.contextPath}/resources/images/pixel-new-player.png" alt="Pixelated New Player Icon" style="image-rendering: pixelated; width: 64px; height: 64px;">
        </div>
        <h2>Register New Account</h2>
        <form action="${pageContext.request.contextPath}/customer/register" method="post">
            
            <div class="form-group">
                <label for="firstName">First Name:</label>
                <input type="text" id="firstName" name="firstName" required>
            </div>
            <div class="form-group">
                <label for="lastName">Last Name:</label>
                <input type="text" id="lastName" name="lastName" required>
            </div>
            
            <div class="form-group">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" required>
            </div>
            
            <div class="form-group">
                <label for="phone">Phone:</label>
                <input type="tel" id="phone" name="phone" required>
            </div>

            <div class="form-group">
                <label for="address">Address:</label>
                <input type="text" id="address" name="address" required>
            </div>

            <div class="form-group">
                <label for="province">Province:</label>
                <select id="province" name="state" required onchange="updateCities()">
                    <option value="">Select Province</option>
                    <option value="Province 1">Province 1</option>
                    <option value="Province 2">Province 2</option>
                    <option value="Bagmati">Bagmati</option>
                    <option value="Gandaki">Gandaki</option>
                    <option value="Lumbini">Lumbini</option>
                    <option value="Karnali">Karnali</option>
                    <option value="Sudurpashchim">Sudurpashchim</option>
                </select>
            </div>

            <div class="form-group">
                <label for="city">City:</label>
                <select id="city" name="city" required>
                    <option value="">Select Province First</option>
                </select>
            </div>

            <div class="form-group">
                <label for="password">Password:</label>
                <input type="password" id="password" name="password" required>
            </div>
            
            <div class="form-group">
                <label for="confirmPassword">Confirm Password:</label>
                <input type="password" id="confirmPassword" name="confirmPassword" required>
            </div>
            
            <button type="submit">REGISTER</button>
            
            <div class="login-link">
                Already a player? <a href="${pageContext.request.contextPath}/customer/login">LOGIN</a>
            </div>
        </form>
    </div>

    <button id="theme-switcher">🌙</button>
    <script src="${pageContext.request.contextPath}/resources/js/register.js"></script> 
    <script src="${pageContext.request.contextPath}/resources/js/theme-switcher.js"></script>
</body>
</html>
