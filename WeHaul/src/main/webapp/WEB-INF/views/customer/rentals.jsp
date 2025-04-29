<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>WeHaul - My Rentals</title>
    <link href="${pageContext.request.contextPath}/resources/css/retro-style.css" rel="stylesheet">
</head>
<body>
    <div class="header">
        <h1>MY RENTALS</h1>
    </div>
    
    <jsp:include page="_customer_nav.jsp" />

    <div class="container">
        <h2>Rental History</h2>
        
        <c:if test="${empty rentalHistory}">
            <p>No rental history found. Time to rent a truck!</p>
        </c:if>
        
        <c:if test="${not empty rentalHistory}">
            <table>
                <thead>
                    <tr>
                        <th>Order ID</th>
                        <th>Truck</th>
                        <th>Pickup</th>
                        <th>Return</th>
                        <th>Status</th>
                        <th>Cost</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="rental" items="${rentalHistory}">
                        <tr>
                            <td>#${rental.orderId}</td>
                            <td>Truck #${rental.truckId}</td> <%-- Consider joining to get truck details --%>
                            <td>${rental.pickupDate}</td>
                            <td>${rental.returnDate}</td>
                            <td>${rental.status}</td>
                            <td>$${rental.totalCost}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>
    </div>

    <button id="theme-switcher">🌙</button>
    <script src="${pageContext.request.contextPath}/resources/js/theme-switcher.js"></script>
</body>
</html>
