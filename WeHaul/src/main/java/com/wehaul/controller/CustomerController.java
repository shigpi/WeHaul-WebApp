package com.wehaul.controller;

import com.wehaul.model.Customer;
// ... (all existing imports) ...
import com.wehaul.model.RentalOrder; // Ensure this model is defined
import com.wehaul.model.TruckType;
import com.wehaul.service.CustomerService;
import com.wehaul.service.RentalOrderService;
import com.wehaul.service.TruckTypeService;
import com.wehaul.util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection; // For potential transaction management
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@WebServlet("/customer/*")
public class CustomerController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private CustomerService customerService = new CustomerService();
    private TruckTypeService truckTypeService = new TruckTypeService();
    private RentalOrderService rentalOrderService = new RentalOrderService();

    private static final long DEFAULT_ESTIMATED_DURATION_HOURS = 24; // Or make this dynamic

    // --- doGet and other GET handlers (showDashboard, showRegister, etc.) remain the same ---
    // ...
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getPathInfo();
        if (action == null) {
            action = "/dashboard";
        }

        HttpSession session = request.getSession(false);
        boolean isLoggedIn = (session != null && session.getAttribute("customer") != null);

        if ("/register".equals(action) || "/login".equals(action)) {
        } else if (!isLoggedIn) {
            System.out.println("User not logged in, redirecting to login for GET action: " + action);
            response.sendRedirect(request.getContextPath() + "/customer/login");
            return;
        }

        try {
            switch (action) {
                case "/dashboard":
                    showDashboard(request, response);
                    break;
                case "/register":
                    showRegister(request, response);
                    break;
                case "/login":
                    showLogin(request, response);
                    break;
                case "/profile":
                    showProfile(request, response);
                    break;
                case "/rentals":
                    showRentals(request, response);
                    break;
                case "/book-truck":
                    showBookingForm(request, response);
                    break;
                case "/logout":
                    logoutCustomer(request, response);
                    break;
                default:
                     System.out.println("Unknown GET action: " + action + ", redirecting...");
                     if (isLoggedIn) {
                         response.sendRedirect(request.getContextPath() + "/customer/dashboard");
                     } else {
                         response.sendRedirect(request.getContextPath() + "/customer/login");
                     }
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
             if (isLoggedIn) {
                request.getRequestDispatcher("/WEB-INF/views/customer/dashboard.jsp").forward(request, response);
             } else {
                 request.getRequestDispatcher("/WEB-INF/views/customer/login.jsp").forward(request, response);
             }
        }
    }

    private void showDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/customer/dashboard.jsp").forward(request, response);
    }

    private void showRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/customer/register.jsp").forward(request, response);
    }

    private void showLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/customer/login.jsp").forward(request, response);
    }

    private void showProfile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
         HttpSession session = request.getSession(false);
         if(session != null && session.getAttribute("customer") != null) {
             Customer sessionCustomer = (Customer) session.getAttribute("customer");
             try {
                 Customer freshCustomer = customerService.getCustomerByEmail(sessionCustomer.getEmail());
                 if(freshCustomer != null) {
                    session.setAttribute("customer", freshCustomer);
                 } else {
                     session.invalidate();
                     response.sendRedirect(request.getContextPath() + "/customer/login?error=profileNotFound");
                     return;
                 }
             } catch (Exception e) {
                  System.err.println("Error refreshing customer data for profile: " + e.getMessage());
                  request.setAttribute("errorMessage", "Could not refresh profile data.");
             }
        }
        request.getRequestDispatcher("/WEB-INF/views/customer/profile.jsp").forward(request, response);
    }

    private void showRentals(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Customer customer = (Customer) session.getAttribute("customer");

        try {
             request.getRequestDispatcher("/WEB-INF/views/customer/rentals.jsp").forward(request, response);
        } catch (Exception e) {
             e.printStackTrace();
             request.setAttribute("errorMessage", "Could not load rental history: " + e.getMessage());
             request.getRequestDispatcher("/WEB-INF/views/customer/dashboard.jsp").forward(request, response);
        }
    }

    private void showBookingForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<TruckType> truckTypes = truckTypeService.getAllTruckTypes();
            request.setAttribute("truckTypes", truckTypes);
            request.getRequestDispatcher("/WEB-INF/views/customer/book-truck.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Could not load booking form data: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/dashboard.jsp").forward(request, response);
        }
    }

     private void logoutCustomer(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/customer/login?logout=true");
    }

    // --- doPost and other POST handlers (processLogin, registerCustomer, updateProfile) ---
    // ...
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getPathInfo() != null ? request.getPathInfo() : "";

        HttpSession session = request.getSession(false);
        boolean isLoggedIn = (session != null && session.getAttribute("customer") != null);

        if ("/register".equals(action) || "/login".equals(action)) {
        } else if (!isLoggedIn) {
             System.out.println("User not logged in, blocking POST action: " + action);
             response.sendRedirect(request.getContextPath() + "/customer/login");
            return;
        }

        try {
            switch (action) {
                case "/register":
                    registerCustomer(request, response);
                    break;
                case "/login":
                    processLogin(request, response);
                    break;
                case "/update":
                    updateProfile(request, response);
                    break;
                case "/submit-booking":
                    processBooking(request, response);
                    break;
                default:
                    System.out.println("Unknown POST action: " + action + ", redirecting to dashboard.");
                    response.sendRedirect(request.getContextPath() + "/customer/dashboard");
            }
        } catch (Exception e) {
             e.printStackTrace();
             String errorMessage = "An error occurred: " + e.getMessage();
             request.setAttribute("errorMessage", errorMessage);
             if ("/update".equals(action)) {
                 request.getRequestDispatcher("/WEB-INF/views/customer/profile.jsp").forward(request, response);
             } else if ("/submit-booking".equals(action)) {
                 try {
                    List<TruckType> truckTypes = truckTypeService.getAllTruckTypes();
                    request.setAttribute("truckTypes", truckTypes);
                 } catch (Exception serviceEx) {
                     System.err.println("Error reloading truck types after booking failure: " + serviceEx.getMessage());
                     e.addSuppressed(serviceEx);
                 }
                 request.getRequestDispatcher("/WEB-INF/views/customer/book-truck.jsp").forward(request, response);
             } else if ("/register".equals(action)) {
                  request.getRequestDispatcher("/WEB-INF/views/customer/register.jsp").forward(request, response);
             } else if ("/login".equals(action)) {
                 request.getRequestDispatcher("/WEB-INF/views/customer/login.jsp").forward(request, response);
             }
             else {
                request.getRequestDispatcher("/WEB-INF/views/customer/dashboard.jsp").forward(request, response);
             }
        }
    }

    private void processLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            Customer customer = customerService.getCustomerByEmail(email);

             if (customer != null) {
                 String decryptedPassword = PasswordUtil.decrypt(customer.getPasswordHash(), customer.getEmail());
                 if (decryptedPassword != null && decryptedPassword.equals(password)) {
                    HttpSession session = request.getSession();
                    session.setAttribute("customer", customer);
                    response.sendRedirect(request.getContextPath() + "/customer/dashboard");
                    return;
                }
            }
            request.setAttribute("errorMessage", "Invalid email or password.");
            request.getRequestDispatcher("/WEB-INF/views/customer/login.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Login failed due to an unexpected error.");
            request.getRequestDispatcher("/WEB-INF/views/customer/login.jsp").forward(request, response);
        }
    }

     private void registerCustomer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String firstName = request.getParameter("firstName") != null ? request.getParameter("firstName").trim() : "";
        String lastName = request.getParameter("lastName") != null ? request.getParameter("lastName").trim() : "";
        String email = request.getParameter("email") != null ? request.getParameter("email").trim() : "";
        String phone = request.getParameter("phone") != null ? request.getParameter("phone").trim() : "";
        String address = request.getParameter("address") != null ? request.getParameter("address").trim() : "";
        String city = request.getParameter("city") != null ? request.getParameter("city").trim() : "";
        String state = request.getParameter("state") != null ? request.getParameter("state").trim() : "";
        String rawPassword = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
            phone.isEmpty() || address.isEmpty() || city.isEmpty() || state.isEmpty() ||
            rawPassword == null || rawPassword.isEmpty()) {

            request.setAttribute("errorMessage", "All fields are required.");
            request.getRequestDispatcher("/WEB-INF/views/customer/register.jsp").forward(request, response);
            return;
        }

        if (!rawPassword.equals(confirmPassword)) {
             request.setAttribute("errorMessage", "Passwords do not match.");
             request.getRequestDispatcher("/WEB-INF/views/customer/register.jsp").forward(request, response);
             return;
        }

        try {
             if (customerService.getCustomerByEmail(email) != null) {
                 request.setAttribute("errorMessage", "Email address is already registered.");
                 request.getRequestDispatcher("/WEB-INF/views/customer/register.jsp").forward(request, response);
                 return;
             }

            Customer customer = new Customer();
            customer.setFirstName(firstName);
            customer.setLastName(lastName);
            customer.setEmail(email);
            customer.setPhone(phone);
            customer.setAddress(address);
            customer.setCity(city);
            customer.setState(state);

            String encryptedPassword = PasswordUtil.encrypt(email, rawPassword);
            customer.setPasswordHash(encryptedPassword);

            customer.setAdmin(false);

            customerService.addCustomer(customer);
            response.sendRedirect(request.getContextPath() + "/customer/login?registered=true");

        } catch (Exception e) {
             e.printStackTrace();
            request.setAttribute("errorMessage", "Registration failed: An internal error occurred.");
            request.getRequestDispatcher("/WEB-INF/views/customer/register.jsp").forward(request, response);
        }
    }

    private void updateProfile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Customer currentCustomer = (Customer) session.getAttribute("customer");

        String firstName = request.getParameter("firstName") != null ? request.getParameter("firstName").trim() : "";
        String lastName = request.getParameter("lastName") != null ? request.getParameter("lastName").trim() : "";
        String email = request.getParameter("email") != null ? request.getParameter("email").trim() : "";
        String phone = request.getParameter("phone") != null ? request.getParameter("phone").trim() : "";
        String address = request.getParameter("address") != null ? request.getParameter("address").trim() : "";
        String city = request.getParameter("city") != null ? request.getParameter("city").trim() : "";
        String state = request.getParameter("state") != null ? request.getParameter("state").trim() : "";

         if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
            phone.isEmpty() || address.isEmpty() || city.isEmpty() || state.isEmpty()) {

            request.setAttribute("errorMessage", "All profile fields are required.");
            request.getRequestDispatcher("/WEB-INF/views/customer/profile.jsp").forward(request, response);
            return;
        }
        try {
             if (!email.equalsIgnoreCase(currentCustomer.getEmail())) {
                 Customer existingCustomerWithNewEmail = customerService.getCustomerByEmail(email);
                 if (existingCustomerWithNewEmail != null && existingCustomerWithNewEmail.getCustomerId() != currentCustomer.getCustomerId()) {
                     request.setAttribute("errorMessage", "The new email address is already in use.");
                     request.getRequestDispatcher("/WEB-INF/views/customer/profile.jsp").forward(request, response);
                     return;
                 }
             }
             currentCustomer.setFirstName(firstName);
            currentCustomer.setLastName(lastName);
            currentCustomer.setEmail(email);
            currentCustomer.setPhone(phone);
            currentCustomer.setAddress(address);
            currentCustomer.setCity(city);
            currentCustomer.setState(state);
            customerService.updateCustomer(currentCustomer);
            session.setAttribute("customer", currentCustomer);
            response.sendRedirect(request.getContextPath() + "/customer/profile?updated=true");
        } catch (Exception e) {
             e.printStackTrace();
            request.setAttribute("errorMessage", "Profile update failed: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/profile.jsp").forward(request, response);
        }
    }


    private void processBooking(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
         HttpSession session = request.getSession(false);
         Customer customer = (Customer) session.getAttribute("customer");

         String truckTypeIdStr = request.getParameter("truckTypeId");
         String pickupDateStr = request.getParameter("pickupDate");
         String pickupProvince = request.getParameter("pickupProvince");
         String pickupCity = request.getParameter("pickupCity");
         String pickupLocationDetail = request.getParameter("pickupLocation");
         String returnProvince = request.getParameter("returnProvince");
         String returnCity = request.getParameter("returnCity");
         String returnLocationDetail = request.getParameter("returnLocation");
         String nepaliPickupLocation = request.getParameter("nepaliPickupLocation");
         String nepaliReturnLocation = request.getParameter("nepaliReturnLocation");

         Connection conn = null; // For transaction management

         try {
            // 1. Basic Validation
            if (truckTypeIdStr == null || truckTypeIdStr.isEmpty() ||
                pickupDateStr == null || pickupDateStr.isEmpty() ||
                pickupProvince == null || pickupProvince.isEmpty() ||
                pickupCity == null || pickupCity.isEmpty() ||
                pickupLocationDetail == null || pickupLocationDetail.trim().isEmpty() ||
                returnProvince == null || returnProvince.isEmpty() ||
                returnCity == null || returnCity.isEmpty() ||
                returnLocationDetail == null || returnLocationDetail.trim().isEmpty())
             {
                throw new IllegalArgumentException("Missing required booking information.");
            }

            // 2. Convert and validate data types
             int requestedTruckTypeId = Integer.parseInt(truckTypeIdStr);
             Timestamp pickupTimestamp;
             LocalDateTime pickupLDT;
             LocalDateTime now = LocalDateTime.now();
             try {
                 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                 pickupLDT = LocalDateTime.parse(pickupDateStr, formatter);
                 if (pickupLDT.isBefore(now)) { throw new IllegalArgumentException("Pickup date cannot be in the past."); }
                 pickupTimestamp = Timestamp.valueOf(pickupLDT);
             } catch (DateTimeParseException e) { throw new IllegalArgumentException("Invalid pickup date/time format.", e); }

            // 3. Fetch Truck Type for cost calculation
            TruckType selectedType = truckTypeService.getTruckTypeById(requestedTruckTypeId);
            if (selectedType == null) { throw new IllegalArgumentException("Invalid truck type selected."); }

            // 4. Estimate Return Date
             LocalDateTime estimatedReturnLDT = pickupLDT.plusHours(DEFAULT_ESTIMATED_DURATION_HOURS);
             Timestamp estimatedReturnTimestamp = Timestamp.valueOf(estimatedReturnLDT);

            // --- TRANSACTION START ---
            conn = com.wehaul.config.DbConfig.getDbConnection(); // Get connection
            conn.setAutoCommit(false); // Start transaction

            // 5. Find and "Reserve" (conceptually) an available TRUCK ID
            Integer availableTruckId = rentalOrderService.findAndReserveAvailableTruckForBooking(requestedTruckTypeId, pickupTimestamp, estimatedReturnTimestamp);

            if (availableTruckId == null) {
                conn.rollback(); // Rollback if no truck found
                String errorMsg = String.format(
                   "SORRY! No specific truck of type (%s) is available for the selected dates. Please try again later or choose a different type/date.",
                   selectedType.getName()
                );
                throw new Exception(errorMsg);
            }

            // 6. Calculate Estimated Total Cost
             BigDecimal estimatedTotalCost = calculateTotalCost(selectedType, pickupTimestamp, estimatedReturnTimestamp);
             if (estimatedTotalCost == null || estimatedTotalCost.compareTo(BigDecimal.ZERO) < 0) {
                 conn.rollback();
                 estimatedTotalCost = BigDecimal.ZERO; // Default for message
                 throw new Exception("Could not calculate a valid cost for the booking.");
             }

            // 7. Create and Save the RentalOrder Request
            RentalOrder newOrder = new RentalOrder();
            newOrder.setCustomerId(customer.getCustomerId());
            // newOrder.setTruckTypeId(requestedTruckTypeId); // If you have a requested_truck_type_id column
            newOrder.setAssignedTruckId(availableTruckId); // *** SET THE ASSIGNED TRUCK ID ***
            newOrder.setPickupDate(pickupTimestamp);
            newOrder.setReturnDate(estimatedReturnTimestamp);

            String fullPickupLocation = String.format("%s (%s, %s)", pickupLocationDetail.trim(), pickupCity, pickupProvince);
            String fullReturnLocation = String.format("%s (%s, %s)", returnLocationDetail.trim(), returnCity, returnProvince);
            newOrder.setPickupLocation(fullPickupLocation);
            newOrder.setReturnLocation(fullReturnLocation);
            newOrder.setNepaliPickupLocation(nepaliPickupLocation != null ? nepaliPickupLocation.trim() : "");
            newOrder.setNepaliReturnLocation(nepaliReturnLocation != null ? nepaliReturnLocation.trim() : "");

            newOrder.setStatus("confirmed"); // Booking is now confirmed as a truck is assigned
            newOrder.setApprovalStatus("approved"); // Auto-approved if truck is found
            newOrder.setTotalCost(estimatedTotalCost);
            newOrder.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            rentalOrderService.createRentalOrder(newOrder); // This uses the shared connection by default if not passed

            // 8. Optionally, update the assigned truck's status to 'rented' or 'reserved'
            // This assumes updateTruckStatus can take a connection for transaction control
            // boolean statusUpdated = rentalOrderService.updateTruckStatus(availableTruckId, "rented", conn); // 'rented' or 'reserved_for_booking'
            // if (!statusUpdated) {
            //     conn.rollback();
            //     throw new SQLException("Failed to update truck status after booking.");
            // }

            conn.commit(); // --- TRANSACTION COMMIT ---

            // 9. Handle Success
            String successMsg = String.format(
                "Booking CONFIRMED! Truck ID: %d. Estimated cost: Rs. %.2f for approx. %d hour(s).",
                availableTruckId,
                estimatedTotalCost,
                DEFAULT_ESTIMATED_DURATION_HOURS
            );
            request.getSession().setAttribute("successMessage", successMsg);
            response.sendRedirect(request.getContextPath() + "/customer/rentals");


         } catch (NumberFormatException e) {
             if (conn != null) try { conn.rollback(); } catch (SQLException ex) { e.addSuppressed(ex); }
             throw new ServletException("Invalid Truck Type ID format.", e);
         } catch (IllegalArgumentException e) {
             if (conn != null) try { conn.rollback(); } catch (SQLException ex) { e.addSuppressed(ex); }
             throw new ServletException(e.getMessage(), e);
         } catch (Exception e) { // Catch other potential errors
             if (conn != null) try { conn.rollback(); } catch (SQLException ex) { e.addSuppressed(ex); }
             e.printStackTrace();
             throw new ServletException("Failed to process booking request: " + e.getMessage(), e);
         } finally {
             if (conn != null) {
                 try {
                     conn.setAutoCommit(true); // Reset auto-commit
                     conn.close(); // Close connection
                 } catch (SQLException ex) {
                     ex.printStackTrace();
                 }
             }
         }
    }

    private BigDecimal calculateTotalCost(TruckType truckType, Timestamp pickupDate, Timestamp estimatedReturnDate) {
        if (truckType == null || truckType.getDailyRate() == null || pickupDate == null || estimatedReturnDate == null) {
            return null;
        }
        BigDecimal dailyRate = truckType.getDailyRate();
        if (dailyRate.compareTo(BigDecimal.ZERO) <= 0) {
             return BigDecimal.ZERO;
        }
        if (!estimatedReturnDate.after(pickupDate)) {
            return dailyRate;
        }
        try {
            long durationMillis = estimatedReturnDate.getTime() - pickupDate.getTime();
            long millisPerDay = 1000 * 60 * 60 * 24;
            long durationDays = (long) Math.ceil((double) durationMillis / millisPerDay);
            if (durationDays <= 0) {
                durationDays = 1;
            }
            return dailyRate.multiply(BigDecimal.valueOf(durationDays));
        } catch (Exception e) {
             e.printStackTrace();
             return null;
        }
    }
}