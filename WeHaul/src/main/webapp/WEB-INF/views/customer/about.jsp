<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>About WeHaul - Your Kathmandu Moving Partner</title>
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
                    <li><a href="${pageContext.request.contextPath}/">Home</a></li>
                    <li class="current"><a href="${pageContext.request.contextPath}/about">About Us</a></li>
                    <li><a href="${pageContext.request.contextPath}/contact">Contact Us</a></li>
                    <%-- Logic for Login/Register or Profile/Logout links --%>
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
             <h1>ABOUT US</h1>
        </div>
    </section>

    <div class="container">
        <article id="main-col">
            <h2>OUR STORY</h2>
            <p>
                WE_HAUL STARTED WITH A SIMPLE MISSION: TO MAKE MOVING AND LOGISTICS IN KATHMANDU AS SMOOTH AND HASSLE-FREE AS POSSIBLE.
                NAVIGATING THE VIBRANT, BUSY STREETS OF THE VALLEY CAN BE A CHALLENGE, ESPECIALLY WHEN YOU HAVE GOODS TO TRANSPORT.
                WE SAW A NEED FOR A RELIABLE, AFFORDABLE, AND CUSTOMER-FOCUSED TRUCK RENTAL SERVICE THAT CATERS TO EVERYONE –
                FROM STUDENTS MOVING INTO THEIR FIRST APARTMENT TO BUSINESSES MANAGING THEIR DAILY LOGISTICS.
            </p>
            <p>
                FOUNDED IN [YEAR, e.g., 2024], OUR TEAM COMBINES LOCAL KNOWLEDGE WITH A COMMITMENT TO MODERN SERVICE STANDARDS.
                WE BELIEVE IN TRANSPARENCY, EFFICIENCY, AND PROVIDING THE RIGHT VEHICLE FOR THE RIGHT JOB.
            </p>

            <h2>OUR MISSION</h2>
            <p>
                TO BE KATHMANDU'S MOST TRUSTED PARTNER FOR LOCAL MOVING AND SMALL-SCALE LOGISTICS, OFFERING A FLEET OF WELL-MAINTAINED
                TRUCKS AND EXCEPTIONAL CUSTOMER SERVICE, ALL WHILE EMBRACING A FUN, RETRO-INSPIRED BRANDING THAT BRINGS A LITTLE JOY
                TO THE OFTEN STRESSFUL TASK OF MOVING.
            </p>

            <h2>OUR VALUES</h2>
            <ul>
                <li><strong>RELIABILITY:</strong> YOU CAN COUNT ON US AND OUR TRUCKS.</li>
                <li><strong>AFFORDABILITY:</strong> FAIR AND TRANSPARENT PRICING.</li>
                <li><strong>CUSTOMER FOCUS:</strong> YOUR NEEDS ARE OUR PRIORITY.</li>
                <li><strong>EFFICIENCY:</strong> MAKING YOUR MOVE QUICK AND EASY.</li>
                <li><strong>LOCAL EXPERTISE:</strong> WE KNOW KATHMANDU.</li>
            </ul>
        </article>
    </div>

    <footer>
        <p>WE_HAUL © <fmt:formatDate value="<%=new java.util.Date()%>" pattern="yyyy"/> </p>
    </footer>

    <button id="theme-switcher">?</button>
    <script src="${pageContext.request.contextPath}/resources/js/theme-switcher.js" defer></script>

</body>
</html>