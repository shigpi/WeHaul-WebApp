<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>WeHaul - Request A Booking</title>
    <link href="${pageContext.request.contextPath}/resources/css/retro-style.css" rel="stylesheet">
    <style>
        /* Simple styling for grouping location fields */
        .location-group {
            border: 1px dashed var(--border-color);
            padding: 1rem;
            margin-bottom: 1rem;
        }
        .location-group label { display: block; margin-bottom: 0.3rem; }
        .location-group select, .location-group input { margin-bottom: 0.7rem; }
    </style>
</head>
<body>
    <div class="header">
        <h1>REQUEST A BOOKING</h1>
    </div>

    <jsp:include page="_customer_nav.jsp" />

    <div class="container">
        <h2>Plan Your Haul!</h2>
        <p style="text-align:center; margin-bottom: 1.5rem;">
            Select your truck, pickup details, and destination. We'll estimate the duration and cost.
            <br><strong>NOTE:</strong> Return date and final cost are estimates subject to confirmation.
        </p>

        <form action="${pageContext.request.contextPath}/customer/submit-booking" method="POST"> 

            <div class="form-group">
                <label for="truckType">TRUCK TYPE:</label>
                <select id="truckType" name="truckTypeId" required>
                    <option value="">-- SELECT TRUCK TYPE --</option>
                    <c:forEach var="type" items="${truckTypes}"> 
                        <option value="${type.typeId}">${type.name} - Capacity: ${type.capacity}, Rate: Rs. <fmt:formatNumber value="${type.dailyRate}" type="number" minFractionDigits="2" maxFractionDigits="2"/>/day</option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label for="pickupDate">PICKUP DATE & TIME:</label>
                <input type="datetime-local" id="pickupDate" name="pickupDate" required
                       min="<fmt:formatDate value="<%=new java.util.Date()%>" pattern="yyyy-MM-dd'T'HH:mm"/>">
            </div>

            <fieldset class="location-group">
                <legend>PICKUP LOCATION</legend>
                <label for="pickupProvince">PROVINCE:</label>
                <select id="pickupProvince" name="pickupProvince" required>
                    <option value="">Select Province</option>
                    <option value="Province 1">Province 1</option>
                    <option value="Madhesh">Madhesh</option>
                    <option value="Bagmati Province">Bagmati Province</option>
                    <option value="Gandaki Province">Gandaki Province</option>
                    <option value="Lumbini Province">Lumbini Province</option>
                    <option value="Karnali Province">Karnali Province</option>
                    <option value="Sudurpashchim Province">Sudurpashchim Province</option>
                </select>

                <label for="pickupCity">CITY:</label>
                <select id="pickupCity" name="pickupCity" required>
                    <option value="">Select Province First</option>
                </select>

                <label for="pickupLocation">SPECIFIC ADDRESS/LANDMARK (ENGLISH):</label>
                <input type="text" id="pickupLocation" name="pickupLocation" placeholder="E.g., Thamel, Kathmandu (Near Garden of Dreams)" required>

                <label for="nepaliPickupLocation">SPECIFIC ADDRESS/LANDMARK (नेपालीमा):</label>
                <input type="text" id="nepaliPickupLocation" name="nepaliPickupLocation" placeholder="जस्तै, ठमेल, काठमाडौं (स्वप्न बगैंचा नजिक)">
            </fieldset>

             <fieldset class="location-group">
                <legend>RETURN/DESTINATION LOCATION</legend>
                <label for="returnProvince">PROVINCE:</label>
                 <select id="returnProvince" name="returnProvince" required>
                     <option value="">Select Province</option>
                     <option value="Province 1">Province 1</option>
                     <option value="Madhesh">Madhesh</option>
                     <option value="Bagmati Province">Bagmati Province</option>
                     <option value="Gandaki Province">Gandaki Province</option>
                     <option value="Lumbini Province">Lumbini Province</option>
                     <option value="Karnali Province">Karnali Province</option>
                     <option value="Sudurpashchim Province">Sudurpashchim Province</option>
                 </select>

                 <label for="returnCity">CITY:</label>
                 <select id="returnCity" name="returnCity" required>
                     <option value="">Select Province First</option>
                 </select>

                <label for="returnLocation">SPECIFIC ADDRESS/LANDMARK (ENGLISH):</label>
                <input type="text" id="returnLocation" name="returnLocation" placeholder="E.g., Lakeside, Pokhara (Near Fewa Lake)" required>

                <label for="nepaliReturnLocation">SPECIFIC ADDRESS/LANDMARK (नेपालीमा):</label>
                <input type="text" id="nepaliReturnLocation" name="nepaliReturnLocation" placeholder="जस्तै, लेकसाइड, पोखरा (फेवा ताल नजिक)">
            </fieldset>

            <%-- Hidden field for customer ID if needed --%>
            <c:if test="${not empty sessionScope.customer}">
                <input type="hidden" name="customerId" value="${sessionScope.customer.customerId}">
            </c:if>

            <div style="text-align: center; margin-top: 1.5rem;">
                <button type="submit" class="btn">REQUEST BOOKING (ESTIMATE)</button>
            </div>
        </form>
    </div>

    <button id="theme-switcher">?</button> <%-- Ensure JS file is linked --%>

    <%-- JavaScript for Province/City Dropdowns --%>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const citiesByProvince = {
                'Province 1': ['Biratnagar', 'Dharan', 'Itahari', 'Bhadrapur', 'Damak'],
                'Madhesh': ['Janakpur', 'Birgunj', 'Kalaiya', 'Jaleshwar', 'Malangwa'],
                'Bagmati Province': ['Kathmandu', 'Lalitpur', 'Bhaktapur', 'Hetauda', 'Banepa'],
                'Gandaki Province': ['Pokhara', 'Baglung', 'Gorkha', 'Besisahar', 'Kusma'],
                'Lumbini Province': ['Butwal', 'Nepalgunj', 'Tansen', 'Bhairahawa', 'Gulariya'],
                'Karnali Province': ['Birendranagar', 'Manma', 'Jumla', 'Dunai', 'Chandannath'],
                'Sudurpashchim Province': ['Dhangadhi', 'Mahendranagar', 'Tikapur', 'Dipayal', 'Baitadi']
            };

            function updateCities(provinceSelectId, citySelectId) {
                const provinceSelect = document.getElementById(provinceSelectId);
                const citySelect = document.getElementById(citySelectId);

                if (!provinceSelect || !citySelect) {
                    console.error('Missing select elements:', provinceSelectId, citySelectId);
                    return; // Prevent errors if elements are missing
                }

                const selectedProvince = provinceSelect.value;
                const currentCityValue = citySelect.value; // Store current value if needed

                citySelect.innerHTML = '<option value="">Select City</option>'; // Reset cities

                if (citiesByProvince[selectedProvince]) {
                    citiesByProvince[selectedProvince].forEach(function(city) {
                        const option = document.createElement('option');
                        option.value = city;
                        option.textContent = city;
                        citySelect.appendChild(option);
                    });
                    // Optional: Try to reselect previous city if it exists in new list
                    // if (citiesByProvince[selectedProvince].includes(currentCityValue)) {
                    //     citySelect.value = currentCityValue;
                    // }
                }
            }

            // Setup for Pickup Location
            const pickupProvinceSelect = document.getElementById('pickupProvince');
            if (pickupProvinceSelect) {
                pickupProvinceSelect.addEventListener('change', function() {
                    updateCities('pickupProvince', 'pickupCity');
                });
                // Initial population if needed (e.g., on page load if province is pre-selected)
                // updateCities('pickupProvince', 'pickupCity');
            } else {
                console.error("Element with ID 'pickupProvince' not found.");
            }

            // Setup for Return Location
            const returnProvinceSelect = document.getElementById('returnProvince');
            if (returnProvinceSelect) {
                returnProvinceSelect.addEventListener('change', function() {
                    updateCities('returnProvince', 'returnCity');
                });
                 // Initial population if needed
                // updateCities('returnProvince', 'returnCity');
            } else {
                 console.error("Element with ID 'returnProvince' not found.");
            }

            // --- Removed password confirmation logic ---
        });
    </script>
    <script src="${pageContext.request.contextPath}/resources/js/theme-switcher.js" defer></script> <%-- Make sure this exists --%>

</body>
</html>