<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>WeHaul - Manage Customers</title>
    <link href="${pageContext.request.contextPath}/resources/css/retro-style.css" rel="stylesheet">
</head>
<body>
    <div class="header">
        <h1>MANAGE CUSTOMERS</h1>
    </div>
    
    <jsp:include page="_admin_nav.jsp" />

    <div class="container">
        <h2>Customer List</h2>
        <a href="#" class="btn" style="margin-bottom: 1rem;">+ ADD CUSTOMER</a>
        
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="customer" items="${customers}">
                    <tr>
                        <td>${customer.customerId}</td>
                        <td>${customer.firstName} ${customer.lastName}</td>
                        <td>${customer.email}</td>
                        <td>${customer.phone}</td>
                        <td>
                            <a href="#" class="btn">EDIT</a>
                            <a href="#" class="btn" style="background: var(--secondary-color);">DELETE</a>
                        </td>
                    </tr>
                </c:forEach>
                 <c:if test="${empty customers}">
                    <tr>
                        <td colspan="5">No customers found.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>

    <button id="theme-switcher">🌙</button>
    <script src="${pageContext.request.contextPath}/resources/js/theme-switcher.js"></script>
</body>
</html>
