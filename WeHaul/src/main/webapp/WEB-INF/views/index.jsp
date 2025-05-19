<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WeHaul - Your Kathmandu Moving Partner</title>
    <link href="${pageContext.request.contextPath}/resources/css/style.css" rel="stylesheet">
</head>
<body> 
    <header>
        <div class="container">
            <div id="branding">
                <h1><a href="${pageContext.request.contextPath}/">WE_HAUL</a></h1>
            </div>
            <nav>
                <ul>
                    <li class="${pageContext.request.servletPath == '/home' || pageContext.request.servletPath == '/' ? 'current' : ''}"><a href="${pageContext.request.contextPath}/">Home</a></li>
                    <li class="${pageContext.request.servletPath == '/about' ? 'current' : ''}"><a href="${pageContext.request.contextPath}/about">About Us</a></li>
                    <li class="${pageContext.request.servletPath == '/contact' ? 'current' : ''}"><a href="${pageContext.request.contextPath}/contact">Contact Us</a></li>
                    
                    <c:choose>
                        <c:when test="${not empty sessionScope.customer}">
                            <li class="${pageContext.request.servletPath == '/customer/dashboard' ? 'current' : ''}"><a href="${pageContext.request.contextPath}/customer/dashboard">Dashboard</a></li>
                            <li><a href="${pageContext.request.contextPath}/customer/logout">Logout</a></li>
                        </c:when>
                        <c:otherwise>
                            <li class="${pageContext.request.servletPath == '/customer/login' ? 'current' : ''}"><a href="${pageContext.request.contextPath}/customer/login">Login</a></li>
                            <li class="${pageContext.request.servletPath == '/customer/register' ? 'current' : ''}"><a href="${pageContext.request.contextPath}/customer/register">Register</a></li>
                        </c:otherwise>
                    </c:choose>
                </ul>
            </nav>
        </div>
    </header>

    <section id="hero">
        <h1>AFFORDABLE & RELIABLE MOVING</h1>
        <p>WHETHER YOU'RE MOVING ACROSS TOWN, A STUDENT HEADING TO A DORM, OR A BUSINESS NEEDING LOGISTICS, WE_HAUL HAS YOU COVERED.</p>
        <p><a href="${pageContext.request.contextPath}/customer/register" class="btn">GET STARTED!</a></p>
    </section>

    <section id="about">
        <div class="container">
            <h2>ABOUT WE_HAUL</h2>
            <p>WE_HAUL IS DEDICATED TO PROVIDING EFFICIENT AND COST-EFFECTIVE LOGISTICS SOLUTIONS WITHIN THE KATHMANDU VALLEY. OUR GOAL IS TO MAKE MOVING SIMPLE AND STRESS-FREE. WE UNDERSTAND THE CHALLENGES OF NAVIGATING KATHMANDU, AND OUR SERVICE IS DESIGNED TO MEET YOUR SPECIFIC MOVING AND TRANSPORTATION NEEDS.</p>
        </div>
    </section>

    <section id="services">
        <div class="container">
            <h2>OUR SERVICES</h2>
            <div class="section-content">
                <div>
                    <h3>TRUCK RENTALS</h3>
                    <p>RENT THE RIGHT SIZE TRUCK FOR YOUR MOVE. FLEXIBLE OPTIONS AVAILABLE.</p>
                </div>
                <div>
                    <h3>LOCAL LOGISTICS</h3>
                    <p>EFFICIENT TRANSPORTATION FOR GOODS AND BELONGINGS WITHIN KATHMANDU.</p>
                </div>
                <div>
                    <h3>RELIABLE SUPPORT</h3>
                    <p>OUR TEAM IS HERE TO ASSIST YOU THROUGHOUT THE RENTAL PROCESS.</p>
                </div>
            </div>
        </div>
    </section>

    <section id="target-audience">
        <div class="container">
            <h2>WHO WE SERVE</h2>
            <div class="section-content">
                <div>
                    <h3>HOME MOVERS</h3>
                    <p>INDIVIDUALS AND FAMILIES MOVING TO, FROM, OR WITHIN KATHMANDU.</p>
                </div>
                <div>
                    <h3>STUDENTS</h3>
                    <p>AFFORDABLE OPTIONS FOR STUDENTS MOVING BETWEEN HOSTELS, APARTMENTS, OR CAMPUSES.</p>
                </div>
                <div>
                    <h3>BUSINESSES</h3>
                    <p>RELIABLE LOGISTICS SUPPORT FOR BUSINESSES OPERATING WITHIN THE VALLEY.</p>
                </div>
            </div>
        </div>
    </section>

    <footer>
        <p>WE_HAUL © 2025</p>
    </footer>
    
    <button id="theme-switcher">?</button> 
    <script src="${pageContext.request.contextPath}/resources/js/theme-switcher.js" defer></script> 

</body>
</html>
