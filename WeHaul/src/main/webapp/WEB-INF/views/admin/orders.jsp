<%-- ... inside your WEB-INF/views/admin/orders.jsp, within the loop displaying orders ... --%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>WeHaul - Manage Orders</title>
    <link href="${pageContext.request.contextPath}/resources/css/retro-style.css" rel="stylesheet">
    <style>
        /* Add any specific styles for this page if needed */
        table { width: 100%; border-collapse: collapse; margin-top: 1rem; }
        th, td { border: 1px solid var(--border-color); padding: 0.5rem; text-align: left; }
        th { background-color: var(--card-bg); color: var(--primary-color); }
        .action-links a { margin-right: 0.5rem; }
    </style>
</head>
<body>
    <div class="header">
        <h1>MANAGE RENTAL ORDERS</h1>
    </div>
    <jsp:include page="_admin_nav.jsp" />

    <div class="container">
        <h2>All Rental Orders</h2>

        <c:choose>
            <c:when test="${not empty orders}">
                <table>
                    <thead>
                        <tr>
                            <th>Order ID</th>
                            <th>Customer ID</th> <%-- Consider showing Customer Name --%>
                            <th>Pickup Date</th>
                            <th>Return Date</th>
                            <th>Status</th>
                            <th>Approval</th>
                            <th>Total Cost</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="order" items="${orders}">
                            <tr>
                                <td><c:out value="${order.orderId}" /></td>
                                <td><c:out value="${order.customerId}" /></td>
                                <td><fmt:formatDate value="${order.pickupDate}" pattern="MMM dd, yyyy hh:mm a" /></td>
                                <td><fmt:formatDate value="${order.returnDate}" pattern="MMM dd, yyyy hh:mm a" /></td>
                                <td><c:out value="${order.status}" /></td>
                                <td><c:out value="${order.approvalStatus}" /></td>
                                <td>Rs. <fmt:formatNumber value="${order.totalCost}" type="number" minFractionDigits="2" maxFractionDigits="2" /></td>
                                <td class="action-links">
								    <a href="${pageContext.request.contextPath}/admin/editOrder?orderId=${order.orderId}" class="btn">EDIT</a>
								    <%-- Approve button (if not already approved) --%>
								    <c:if test="${order.approvalStatus == 'pending' || order.approvalStatus == 'pending_approval'}">
								        <form action="${pageContext.request.contextPath}/admin/approveOrder" method="POST" style="display:inline;">
								            <input type="hidden" name="orderId" value="${order.orderId}">
								            <input type="hidden" name="truckId" value="${order.assignedTruckId}"> 
								            <button type="submit" class="btn">APPROVE</button>
								        </form>
								    </c:if>
								</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <p>No rental orders found.</p>
            </c:otherwise>
        </c:choose>
    </div>

    <button id="theme-switcher">?</button>
    <script src="${pageContext.request.contextPath}/resources/js/theme-switcher.js"></script>
</body>
</html>