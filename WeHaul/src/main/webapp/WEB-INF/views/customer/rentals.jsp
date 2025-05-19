<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="com.wehaul.util.SessionUtil" %>

<%
    String pageErrorMessage = SessionUtil.getAndClearErrorMessage(request);
    String pageSuccessMessage = SessionUtil.getAndClearSuccessMessage(request);
    String requestErrorMessage = (String) request.getAttribute("errorMessage");
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>WeHaul - My Rentals</title>
    <link href="${pageContext.request.contextPath}/resources/css/retro-style.css" rel="stylesheet">
    <style>
        table { width: 100%; border-collapse: collapse; margin-top: 1.5rem; font-size:10px; }
        th, td { border: 1px solid var(--border-color); padding: 0.6rem; text-align: left; vertical-align: top;}
        th { background-color: var(--nav-bg); color: var(--primary-color); text-transform: uppercase; }
        .message-display { text-align: center; border: 1px solid; padding: 0.5rem; margin-bottom:1rem; }
        .error-message { color: var(--secondary-color); border-color: var(--secondary-color); }
        .success-message { color: var(--primary-color); border-color: var(--primary-color); }
        .status-pending-approval, .status-pending_approval, .status-pending { color: orange; font-weight: bold; }
        .status-approved { color: #007bff; font-weight: bold; }
        .status-confirmed { color: #28a745; font-weight: bold; }
        .status-in-progress, .status-in_progress { color: darkgreen; font-weight: bold; }
        .status-completed { color: grey; }
        .status-rejected, 
        .status-cancelled-by-customer, .status-cancelled_by_customer,
        .status-cancelled-by-admin, .status-cancelled_by_admin,
        .status-cancelled {
            color: #dc3545; 
            text-decoration: line-through;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>MY RENTALS & REQUESTS</h1>
    </div>

    <jsp:include page="_customer_nav.jsp" />

    <div class="container">
        <h2>My Rental History & Requests</h2>

        <c:if test="${not empty pageSuccessMessage}">
            <p class="message-display success-message"><c:out value="${pageSuccessMessage}" /></p>
        </c:if>
        <c:if test="${not empty pageErrorMessage}">
            <p class="message-display error-message"><c:out value="${pageErrorMessage}" /></p>
        </c:if>
        <c:if test="${not empty requestErrorMessage && empty pageErrorMessage}">
            <p class="message-display error-message"><c:out value="${requestErrorMessage}" /></p>
        </c:if>


        <c:if test="${empty rentalHistory}">
            <p style="text-align:center;">You have no rental history or pending requests.
                <a href="${pageContext.request.contextPath}/customer/book-truck" class="btn">Book a Truck!</a>
            </p>
        </c:if>

        <c:if test="${not empty rentalHistory}">
            <table>
                <thead>
                    <tr>
                        <th>Order ID</th>
                        <th>Truck Details</th>
                        <th>Pickup</th>
                        <th>Return (Est.)</th>
                        <th>Main Status</th>
                        <th>Approval</th>
                        <th>Cost (Est.)</th>
                        <th>Requested On</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="rental" items="${rentalHistory}">
                        <tr>
                            <td>#<c:out value="${rental.orderId}" /></td>
                            <td>
                                Requested Type ID: <c:out value="${rental.truckId != null ? rental.truckId : 'N/A'}" />
                                <br/>
                                <c:choose>
                                    <c:when test="${not empty rental.assignedTruckId && not empty rental.assignedTruckInfo}">
                                        Assigned: <c:out value="${rental.assignedTruckInfo.licensePlate}" />
                                        (<c:out value="${rental.truckTypeInfo.name != null ? rental.truckTypeInfo.name : 'Type N/A'}" />)
                                        (ID: <c:out value="${rental.assignedTruckId}" />)
                                    </c:when>
                                    <c:when test="${not empty rental.assignedTruckId && empty rental.assignedTruckInfo}">
                                        Assigned Truck ID: <c:out value="${rental.assignedTruckId}"/> (Details pending)
                                    </c:when>
                                    <c:otherwise>
                                        Assigned Truck: Awaiting Admin
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <fmt:formatDate value="${rental.pickupDate}" pattern="MMM dd, yyyy"/><br/>
                                <fmt:formatDate value="${rental.pickupDate}" pattern="hh:mm a"/>
                            </td>
                            <td>
                                <fmt:formatDate value="${rental.returnDate}" pattern="MMM dd, yyyy"/><br/>
                                <fmt:formatDate value="${rental.returnDate}" pattern="hh:mm a"/>
                            </td>
                            <td class="status-${fn:toLowerCase(fn:replace(rental.status, '_', '-'))}">
                                <c:out value="${rental.status}" />
                            </td>
                             <td class="status-${fn:toLowerCase(fn:replace(rental.approvalStatus, '_', '-'))}">
                                <c:out value="${rental.approvalStatus}" />
                            </td>
                            <td>Rs. <fmt:formatNumber value="${rental.totalCost}" type="number" minFractionDigits="2" maxFractionDigits="2"/></td>
                            <td><fmt:formatDate value="${rental.createdAt}" pattern="MMM dd, yyyy"/></td>
                            <td>
                                <c:if test="${rental.status == 'pending_approval' || rental.status == 'pending' || rental.approvalStatus == 'pending'}">
                                    Request Pending
                                </c:if>
                                <c:if test="${rental.status == 'confirmed' || rental.status == 'approved'}">
                                    Confirmed
                                </c:if>
                                 <c:if test="${rental.status == 'in_progress'}">
                                    In Progress
                                 </c:if>
                                  <c:if test="${rental.status == 'completed'}">
                                    Completed
                                 </c:if>
                                  <c:if test="${rental.status == 'cancelled_by_customer' || rental.status == 'cancelled_by_admin' || rental.status == 'cancelled' || rental.approvalStatus == 'rejected'}">
                                    Cancelled/Rejected
                                 </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>
    </div>

    <button id="theme-switcher">?</button>
    <script src="${pageContext.request.contextPath}/resources/js/theme-switcher.js" defer></script>
</body>
</html>