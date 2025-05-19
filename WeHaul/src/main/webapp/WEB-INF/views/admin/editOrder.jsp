<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="com.wehaul.util.SessionUtil" %>

<%
    String pageErrorMessage = SessionUtil.getAndClearErrorMessage(request);
    String pageSuccessMessage = SessionUtil.getAndClearSuccessMessage(request);
    // Also check for any request-scoped error message if forwarded from a catch block
    String requestErrorMessage = (String) request.getAttribute("errorMessage");
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>WeHaul - Edit Rental Order</title>
    <link href="${pageContext.request.contextPath}/resources/css/retro-style.css" rel="stylesheet">
    <style>
        .error-message { color: var(--secondary-color); border: 1px solid var(--secondary-color); padding: 0.5rem; margin-bottom:1rem; }
        .success-message { color: var(--primary-color); border: 1px solid var(--primary-color); padding: 0.5rem; margin-bottom:1rem; }
    </style>
</head>
<body>
    <div class="header">
        <h1>EDIT ORDER #${order.orderId}</h1> <%-- This EL will fail if 'order' is null --%>
    </div>
    <jsp:include page="_admin_nav.jsp" />

    <%-- Display any direct request error attribute first (from a forward in a catch block) --%>
    <c:if test="${not empty requestScope.errorMessage}">
        <div class="error-message" style="background-color: lightpink;"> <!-- Differentiate style -->
            REQUEST ERROR: <c:out value="${requestScope.errorMessage}" />
        </div>
    </c:if>

    <%-- Then display any session error attribute (from a redirect) --%>
    <c:if test="${not empty pageErrorMessage}">
        <div class="error-message" style="background-color: lightcoral;"> <!-- Differentiate style -->
            SESSION ERROR: <c:out value="${pageErrorMessage}" />
        </div>
    </c:if>
    <c:if test="${not empty pageSuccessMessage}">
        <div class="success-message">
            <c:out value="${pageSuccessMessage}" />
        </div>
    </c:if>


    <div class="container">
        <c:if test="${not empty order}">
            <h2>Order Details for Order ID: <c:out value="${order.orderId}" /></h2>
            <form action="${pageContext.request.contextPath}/admin/updateOrder" method="POST">
	            <input type="hidden" name="orderId" value="${order.orderId}">
	
	            <div class="form-group">
	                <label for="customerId">CUSTOMER:</label>
	                <select id="customerId" name="customerId" required>
	                    <c:forEach var="cust" items="${allCustomers}">
	                        <option value="${cust.customerId}" ${cust.customerId == order.customerId ? 'selected' : ''}>
	                            <c:out value="${cust.firstName} ${cust.lastName} (ID: ${cust.customerId})" />
	                        </option>
	                    </c:forEach>
	                </select>
	            </div>
	
	            <div class="form-group">
				    <label>ASSIGNED TRUCK TYPE:</label> 
				    <c:choose>
				        <c:when test="${not empty order.assignedTruckId && not empty order.truckTypeInfo}">
				            <input type="text" value="${order.truckTypeInfo.name} (ID: ${order.truckTypeInfo.typeId})" readonly disabled>
				        </c:when>
				        <c:otherwise>
				            <input type="text" value="N/A (No truck assigned or type info missing)" readonly disabled>
				        </c:otherwise>
				    </c:choose>
				</div>
	
	
	            <div class="form-group">
	                <label for="pickupDate">PICKUP DATE & TIME:</label>
	                <input type="datetime-local" id="pickupDate" name="pickupDate" required
	                       value="<fmt:formatDate value="${order.pickupDate}" pattern="yyyy-MM-dd'T'HH:mm"/>">
	            </div>
	
	            <div class="form-group">
	                <label for="returnDate">RETURN DATE & TIME:</label>
	                <input type="datetime-local" id="returnDate" name="returnDate" required
	                       value="<fmt:formatDate value="${order.returnDate}" pattern="yyyy-MM-dd'T'HH:mm"/>">
	            </div>
	
	            <div class="form-group">
	                <label for="pickupLocation">PICKUP LOCATION (ENGLISH):</label>
	                <input type="text" id="pickupLocation" name="pickupLocation" value="${order.pickupLocation}" required>
	            </div>
	            <div class="form-group">
	                <label for="returnLocation">RETURN LOCATION (ENGLISH):</label>
	                <input type="text" id="returnLocation" name="returnLocation" value="${order.returnLocation}" required>
	            </div>
	            <div class="form-group">
	                <label for="nepaliPickupLocation">PICKUP LOCATION (नेपालीमा):</label>
	                <input type="text" id="nepaliPickupLocation" name="nepaliPickupLocation" value="${order.nepaliPickupLocation}">
	            </div>
	            <div class="form-group">
	                <label for="nepaliReturnLocation">RETURN LOCATION (नेपालीमा):</label>
	                <input type="text" id="nepaliReturnLocation" name="nepaliReturnLocation" value="${order.nepaliReturnLocation}">
	            </div>
	
	            <div class="form-group">
	                <label for="status">ORDER STATUS:</label>
	                <select id="status" name="status" required>
	                    <option value="pending_approval" ${order.status == 'pending_approval' ? 'selected' : ''}>Pending Approval</option>
	                    <option value="approved" ${order.status == 'approved' ? 'selected' : ''}>Approved</option>
	                    <option value="confirmed" ${order.status == 'confirmed' ? 'selected' : ''}>Confirmed by Customer</option>
	                    <option value="in_progress" ${order.status == 'in_progress' ? 'selected' : ''}>In Progress</option>
	                    <option value="completed" ${order.status == 'completed' ? 'selected' : ''}>Completed</option>
	                    <option value="cancelled_by_customer" ${order.status == 'cancelled_by_customer' ? 'selected' : ''}>Cancelled by Customer</option>
	                    <option value="cancelled_by_admin" ${order.status == 'cancelled_by_admin' ? 'selected' : ''}>Cancelled by Admin</option>
	                </select>
	            </div>
	            <div class="form-group">
	                <label for="approvalStatus">APPROVAL STATUS:</label>
	                 <select id="approvalStatus" name="approvalStatus" required>
	                    <option value="pending" ${order.approvalStatus == 'pending' ? 'selected' : ''}>Pending</option>
	                    <option value="approved" ${order.approvalStatus == 'approved' ? 'selected' : ''}>Approved</option>
	                    <option value="rejected" ${order.approvalStatus == 'rejected' ? 'selected' : ''}>Rejected</option>
	                </select>
	            </div>
	
	            <div class="form-group">
	                <label for="assignedTruckId">ASSIGNED TRUCK:</label>
	                <select id="assignedTruckId" name="assignedTruckId">
	                    <option value="">-- Not Yet Assigned --</option>
	                    <c:forEach var="truck" items="${allTrucks}">
	                        <option value="${truck.truckId}" ${truck.truckId == order.assignedTruckId ? 'selected' : ''}>
	                           ID: ${truck.truckId} - ${truck.licensePlate} (${truck.typeName}) - Status: ${truck.status}
	                        </option>
	                    </c:forEach>
	                </select>
	            </div>
	
	            <div class="form-group">
	                <label for="totalCost">TOTAL COST (RS.):</label>
	                <input type="number" step="0.01" id="totalCost" name="totalCost" value="${order.totalCost}" required>
	            </div>
	
	            <div style="text-align: center; margin-top: 1.5rem;">
	                <button type="submit" class="btn">SAVE CHANGES</button>
	                <a href="${pageContext.request.contextPath}/admin/orders" class="btn" style="background: var(--secondary-color);">CANCEL</a>
	            </div>
	        </form>
	    </c:if>
        <c:if test="${empty order && empty requestScope.errorMessage && empty pageErrorMessage}">
             <p class="error-message">Order data could not be loaded.</p>
        </c:if>
    </div>

    <button id="theme-switcher">?</button>
    <script src="${pageContext.request.contextPath}/resources/js/theme-switcher.js"></script>
</body>
</html>