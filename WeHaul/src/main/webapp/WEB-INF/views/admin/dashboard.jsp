<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>WeHaul - Admin Dashboard</title>
    <link href="${pageContext.request.contextPath}/resources/css/retro-style.css" rel="stylesheet">
    <style>
        /* Additional styles specific to admin dashboard */
        .stats {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
            gap: 1rem;
            margin-bottom: 2rem;
        }
        .stat-card {
            background: var(--card-bg);
            padding: 1rem;
            border: 2px solid var(--border-color);
            box-shadow: 2px 2px 0px var(--border-color);
            text-align: center;
        }
        .stat-card h3 {
            margin-top: 0;
            font-size: 0.9rem;
            color: var(--secondary-color);
        }
        .stat-card p {
            font-size: 1.5rem;
            margin: 0.5rem 0 0;
            font-weight: normal;
            color: var(--text-color);
        }
        .recent-activity {
             background: var(--card-bg);
            padding: 1rem;
            border: 2px solid var(--border-color);
            box-shadow: 2px 2px 0px var(--border-color);
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>ADMIN DASHBOARD</h1>
    </div>
    
    <jsp:include page="_admin_nav.jsp" />
    
    <div class="container">
    
        <h2>System Stats</h2>
        
        <div class="stats">
		    <div class="stat-card">
		        <h3>Total Customers</h3>
		        <p id="totalCustomersStat">
		            <%-- Accesses dashboardStats.totalCustomers --%>
		            <c:out value="${not empty dashboardStats ? dashboardStats.totalCustomers : 'N/A'}" />
		        </p>
		    </div>
		    <div class="stat-card">
		        <h3>Available Trucks</h3>
		        <p id="availableTrucksStat">
		            <%-- Accesses dashboardStats.availableTrucks --%>
		            <c:out value="${not empty dashboardStats ? dashboardStats.availableTrucks : 'N/A'}" />
		        </p>
		    </div>
		    <div class="stat-card">
		        <h3>Active Rentals</h3>
		        <p id="activeRentalsStat">
		            <%-- Accesses dashboardStats.activeRentals --%>
		            <c:out value="${not empty dashboardStats ? dashboardStats.activeRentals : 'N/A'}" />
		        </p>
		    </div>
		    <div class="stat-card">
		        <h3>Pending Orders</h3>
		        <p id="pendingOrdersStat">
		            <%-- Accesses dashboardStats.pendingOrders --%>
		            <c:out value="${not empty dashboardStats ? dashboardStats.pendingOrders : 'N/A'}" />
		        </p>
		    </div>
		</div>
		                
        <div class="recent-activity">
            <h2>Recent Activity</h2>
            <p>No recent activity</p> <%-- Placeholder --%>
        </div>
    </div>

    <button id="theme-switcher">🌙</button>
    <script src="${pageContext.request.contextPath}/resources/js/theme-switcher.js"></script>
</body>
</html>
