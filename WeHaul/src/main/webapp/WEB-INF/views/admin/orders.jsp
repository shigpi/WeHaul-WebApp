<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>WeHaul - Manage Orders</title>
    <link href="${pageContext.request.contextPath}/resources/css/retro-style.css" rel="stylesheet">
    <style>
        /* Specific styles for order status */
        .status-pending, .approval-pending { color: var(--accent-color); } /* Use theme variable */
        .status-confirmed, .approval-approved { color: var(--success); } /* Use theme variable */
        .status-in_progress { color: var(--primary-color); } /* Use theme variable */
        .status-completed { color: #7f8c8d; } /* Gray */
        .status-cancelled, .approval-rejected { color: var(--secondary-color); } /* Use theme variable */
        
        .truck-select {
             width: auto; /* Adjust width as needed */
             margin-right: 0.5rem;
             padding: 0.3rem; /* Smaller padding */
             font-size: 9px; /* Smaller font */
        }
        .action-btn { /* Make buttons smaller */
             padding: 0.3rem 0.6rem;
             font-size: 9px;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>MANAGE ORDERS</h1>
    </div>
    
    <jsp:include page="_admin_nav.jsp" />

    <div class="container">
        <h2>Rental Orders</h2>
        <a href="#" class="btn" style="margin-bottom: 1rem;">+ NEW ORDER</a>
        
        <table>
            <thead>
                <tr>
                    <th>Order ID</th>
                    <th>Customer</th>
                    <th>Truck</th>
                    <th>Pickup</th>
                    <th>Return</th>
                    <th>Status</th>
                    <th>Total</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="order" items="${orders}">
                    <tr>
                        <td>#${order.orderId}</td>
                        <td>Cust #${order.customerId}</td> <%-- Consider joining for name --%>
                        <td>Truck #${order.assignedTruckId != null ? order.assignedTruckId : order.truckId}</td> <%-- Show assigned or requested --%>
                        <td>${order.pickupDate}</td>
                        <td>${order.returnDate}</td>
                        <td>
                            <span class="status-${order.status.toLowerCase()}">${order.status}</span>
                            <br>
                            <span class="approval-${order.approvalStatus.toLowerCase()}">(${order.approvalStatus})</span>
                        </td>
                        <td>$${order.totalCost}</td>
                        <td>
                            <c:choose>
                                <c:when test="${order.approvalStatus == 'pending'}">
                                    <form method="post" action="${pageContext.request.contextPath}/admin/approve-order" style="display:inline-block; margin-bottom: 0.2rem;">
                                        <input type="hidden" name="orderId" value="${order.orderId}">
                                        <select name="truckId" required class="truck-select">
                                            <option value="">Assign Truck</option>
                                            <c:forEach items="${availableTrucks}" var="truck">
                                                <option value="${truck.truckId}">${truck.licensePlate}</option> <%-- Simplified display --%>
                                            </c:forEach>
                                        </select>
                                        <button type="submit" name="approve" value="true" class="btn action-btn">APPROVE</button>
                                    </form>
                                    <button type="button" class="btn action-btn" style="background: var(--secondary-color);"
                                        onclick="rejectOrder('${order.orderId}')">REJECT</button>
                                </c:when>
                                <c:otherwise>
                                    <a href="#" class="btn action-btn">EDIT</a>
                                    <c:if test="${order.status != 'completed' && order.status != 'cancelled'}">
                                        <a href="#" class="btn action-btn" style="background: var(--secondary-color);">CANCEL</a>
                                    </c:if>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
                 <c:if test="${empty orders}">
                    <tr>
                        <td colspan="8">No orders found.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>

    <button id="theme-switcher">🌙</button>
    <script>
        function rejectOrder(orderId) {
            if (confirm('Are you sure you want to reject this order?')) {
                var xhr = new XMLHttpRequest();
                xhr.open('POST', '${pageContext.request.contextPath}/admin/approve-order', true); // Added async flag
                xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
                xhr.onload = function() { // Use onload for better compatibility
                    if (xhr.status >= 200 && xhr.status < 300) { // Check for success status range
                        location.reload();
                    } else {
                        console.error('Request failed. Status:', xhr.status);
                        alert('Failed to reject order. Status: ' + xhr.status);
                    }
                };
                 xhr.onerror = function() { // Handle network errors
                    console.error('Request failed due to network error.');
                    alert('Failed to reject order due to network error.');
                };
                xhr.send('orderId=' + orderId + '&approve=false'); // Send approve=false for rejection
            }
        }
    </script>
    <script src="${pageContext.request.contextPath}/resources/js/theme-switcher.js"></script>
</body>
</html>
