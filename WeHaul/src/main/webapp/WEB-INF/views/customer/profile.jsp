<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>WeHaul - Profile</title>
    <link href="${pageContext.request.contextPath}/resources/css/retro-style.css" rel="stylesheet">
</head>
<body>
    <div class="header">
        <h1>PLAYER PROFILE</h1>
    </div>
    
    <jsp:include page="_customer_nav.jsp" />

    <div class="container">
        <h2>Player Stats</h2>
        
        <div id="profile-view">
            <div class="profile-details">
                <div class="detail-group">
                    <div class="detail-label">First Name:</div>
                    <div class="detail-value">${customer.firstName}</div>
                </div>
                <div class="detail-group">
                    <div class="detail-label">Last Name:</div>
                    <div class="detail-value">${customer.lastName}</div>
                </div>
                <div class="detail-group">
                    <div class="detail-label">Email:</div>
                    <div class="detail-value">${customer.email}</div>
                </div>
                <div class="detail-group">
                    <div class="detail-label">Phone:</div>
                    <div class="detail-value">${customer.phone}</div>
                </div>
                <div class="detail-group">
                    <div class="detail-label">Address:</div>
                    <div class="detail-value">${customer.address}, ${customer.city}, ${customer.state}</div>
                </div>
            </div>
            <button onclick="toggleEdit(true)" class="btn" style="margin-top: 1rem;">EDIT PROFILE</button>
        </div>

        <div id="profile-edit" style="display: none;">
            <form action="${pageContext.request.contextPath}/customer/update" method="post">
                <input type="hidden" name="customerId" value="${customer.customerId}">
                <div class="form-group">
                    <label for="firstName">First Name:</label>
                    <input type="text" id="firstName" name="firstName" value="${customer.firstName}" required>
                </div>
                <div class="form-group">
                    <label for="lastName">Last Name:</label>
                    <input type="text" id="lastName" name="lastName" value="${customer.lastName}" required>
                </div>
                <div class="form-group">
                    <label for="email">Email:</label>
                    <input type="email" id="email" name="email" value="${customer.email}" required>
                </div>
                <div class="form-group">
                    <label for="phone">Phone:</label>
                    <input type="tel" id="phone" name="phone" value="${customer.phone}" required>
                </div>
                <div class="form-group">
                    <label for="address">Address:</label>
                    <input type="text" id="address" name="address" value="${customer.address}" required>
                </div>
                <%-- Province/City dropdowns could be added here similar to register.jsp --%>
                <button type="submit" class="btn">SAVE CHANGES</button>
                <button type="button" onclick="toggleEdit(false)" class="btn" style="background: var(--secondary-color); margin-top: 0.5rem;">CANCEL</button>
            </form>
        </div>
    </div>

    <button id="theme-switcher">🌙</button>
    <script>
        function toggleEdit(isEditing) {
            document.getElementById('profile-view').style.display = isEditing ? 'none' : 'block';
            document.getElementById('profile-edit').style.display = isEditing ? 'block' : 'none';
        }
    </script>
    <script src="${pageContext.request.contextPath}/resources/js/theme-switcher.js"></script>
</body>
</html>
