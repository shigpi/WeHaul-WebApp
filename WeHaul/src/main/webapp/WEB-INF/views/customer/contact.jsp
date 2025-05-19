<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Contact WeHaul - Kathmandu Moving Partner</title>
    <link href="${pageContext.request.contextPath}/resources/css/style.css" rel="stylesheet">
    <style>
        .contact-info p { margin-bottom: 0.5rem; }
        .contact-info strong { color: var(--primary-color); }
        .contact-form .form-group { margin-bottom: 1rem; }
    </style>
</head>
<body>

    <header>
        <div class="container">
            <div id="branding">
                <h1><a href="${pageContext.request.contextPath}/">WE_HAUL</a></h1>
            </div>
            <nav>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/">Home</a></li>
                    <li><a href="${pageContext.request.contextPath}/about">About Us</a></li>
                    <li class="current"><a href="${pageContext.request.contextPath}/contact">Contact Us</a></li>
                    <c:choose>
                        <c:when test="${not empty sessionScope.customer}">
                            <li><a href="${pageContext.request.contextPath}/customer/dashboard">Dashboard</a></li>
                            <li><a href="${pageContext.request.contextPath}/customer/logout">Logout</a></li>
                        </c:when>
                        <c:otherwise>
                            <li><a href="${pageContext.request.contextPath}/customer/login">Login</a></li>
                            <li><a href="${pageContext.request.contextPath}/customer/register">Register</a></li>
                        </c:otherwise>
                    </c:choose>
                </ul>
            </nav>
        </div>
    </header>

    <section id="page-title-hero" style="background: var(--primary-color); color: white; text-align:center; padding: 20px 0; border-bottom: 4px solid var(--border-color);">
         <div class="container" style="background: none; border: none; box-shadow: none;">
            <h1>GET IN TOUCH</h1>
        </div>
    </section>

    <div class="container">
        <article id="main-col">
            <p style="text-align:center; margin-bottom: 2rem;">
                HAVE QUESTIONS? NEED A QUOTE? OR JUST WANT TO SAY HI? WE'D LOVE TO HEAR FROM YOU!
            </p>

            <div class="contact-info" style="text-align:center; margin-bottom: 2rem;">
                <h3>OUR OFFICE</h3>
                <p><strong>ADDRESS:</strong> [YOUR COMPANY ADDRESS], KATHMANDU, NEPAL</p>
                <p><strong>PHONE:</strong> <a href="tel:+97798XXXXXXXX">+977-98XXXXXXXX</a> / <a href="tel:+9771XXXXXX">+977-1-XXXXXX</a></p>
                <p><strong>EMAIL:</strong> <a href="mailto:info@wehaul.com.np">INFO@WEHAUL.COM.NP</a></p>
                <p><strong>HOURS:</strong> SUNDAY - FRIDAY, 9:00 AM - 6:00 PM</p>
            </div>

            <h3>SEND US A MESSAGE</h3>
            <form class="contact-form" action="${pageContext.request.contextPath}/contact-submit" method="POST"> 
                <div class="form-group">
                    <label for="name">NAME:</label>
                    <input type="text" id="name" name="name" required>
                </div>
                <div class="form-group">
                    <label for="email">EMAIL:</label>
                    <input type="email" id="email" name="email" required>
                </div>
                <div class="form-group">
                    <label for="subject">SUBJECT:</label>
                    <input type="text" id="subject" name="subject" required>
                </div>
                <div class="form-group">
                    <label for="message">MESSAGE:</label>
                    <textarea id="message" name="message" rows="6" required></textarea>
                </div>
                <div style="text-align:center;">
                    <button type="submit" class="btn">SEND MESSAGE</button>
                </div>
            </form>
        </article>
    </div>


    <footer>
        <p>WE_HAUL © <fmt:formatDate value="<%=new java.util.Date()%>" pattern="yyyy"/> </p>
    </footer>

    <button id="theme-switcher">?</button>
    <script src="${pageContext.request.contextPath}/resources/js/theme-switcher.js" defer></script>

</body>
</html>