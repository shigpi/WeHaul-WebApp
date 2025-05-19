package com.wehaul.controller;

import com.wehaul.model.Customer;
import com.wehaul.model.RentalOrder;
import com.wehaul.model.TruckType;
import com.wehaul.service.CustomerService;
import com.wehaul.service.RentalOrderService;
import com.wehaul.service.TruckTypeService;
import com.wehaul.util.PasswordUtil;
import com.wehaul.util.SessionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
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
        System.out.println("CUSTOMER_CONTROLLER: showLogin called."); // LOG
        HttpSession session = request.getSession(false);
        Customer customer = (session != null) ? (Customer) session.getAttribute("customer") : null;

        if (customer != null) {
            System.out.println("CUSTOMER_CONTROLLER: Customer already logged in. isAdmin=" + customer.isAdmin()); // LOG
            if (customer.isAdmin()) {
                // If an admin is somehow sent to customer login, send them to admin dashboard
                System.out.println("CUSTOMER_CONTROLLER: Logged in user is ADMIN, redirecting to admin dashboard from customer login."); // LOG
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                return;
            } else {
                // If a regular customer is logged in, send them to customer dashboard
                System.out.println("CUSTOMER_CONTROLLER: Logged in user is CUSTOMER, redirecting to customer dashboard from customer login."); // LOG
                response.sendRedirect(request.getContextPath() + "/customer/dashboard");
                return;
            }
        }
        System.out.println("CUSTOMER_CONTROLLER: No logged-in customer, showing login page."); // LOG
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
            throws ServletException, IOException, SQLException {
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

        String truckTypeIdStr = request.getParameter("truckTypeId"); // This is the ID of the TRUCK TYPE selected
        String pickupDateStr = request.getParameter("pickupDate");
        String pickupProvince = request.getParameter("pickupProvince");
        String pickupCity = request.getParameter("pickupCity");
        String pickupLocationDetail = request.getParameter("pickupLocation");
        String returnProvince = request.getParameter("returnProvince");
        String returnCity = request.getParameter("returnCity");
        String returnLocationDetail = request.getParameter("returnLocation");
        String nepaliPickupLocation = request.getParameter("nepaliPickupLocation");
        String nepaliReturnLocation = request.getParameter("nepaliReturnLocation");

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
                throw new IllegalArgumentException("Missing required booking information. Please fill all fields.");
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

            // 4. Estimate Return Date (Simplified to 1 day / 24 hours for initial estimate)
            LocalDateTime estimatedReturnLDT = pickupLDT.plusDays(1); // Simplified: 1 day later
            Timestamp estimatedReturnTimestamp = Timestamp.valueOf(estimatedReturnLDT);

            // 5. Calculate Minimum Total Cost (for 1 day) using the existing calculateTotalCost method
            BigDecimal minimumTotalCost = calculateTotalCost(selectedType, pickupTimestamp, estimatedReturnTimestamp);
            if (minimumTotalCost == null || minimumTotalCost.compareTo(BigDecimal.ZERO) < 0) {
                minimumTotalCost = BigDecimal.ZERO; // Default if calculation fails for some reason
                System.err.println("Warning: Could not calculate a valid minimum cost, defaulting to 0.");
            }

            // 6. Create and Save the RentalOrder Request
            RentalOrder newOrder = new RentalOrder();
            newOrder.setCustomerId(customer.getCustomerId());
            newOrder.setTruckId(requestedTruckTypeId);
            newOrder.setAssignedTruckId(null);
            newOrder.setPickupDate(pickupTimestamp);
            newOrder.setReturnDate(estimatedReturnTimestamp);
            String fullPickupLocation = String.format("%s, %s, %s", pickupLocationDetail.trim(), pickupCity, pickupProvince);
            String fullReturnLocation = String.format("%s, %s, %s", returnLocationDetail.trim(), returnCity, returnProvince);
            newOrder.setPickupLocation(fullPickupLocation);
            newOrder.setReturnLocation(fullReturnLocation);
            newOrder.setNepaliPickupLocation(nepaliPickupLocation != null ? nepaliPickupLocation.trim() : "");
            newOrder.setNepaliReturnLocation(nepaliReturnLocation != null ? nepaliReturnLocation.trim() : "");
            newOrder.setStatus("pending_approval");
            newOrder.setApprovalStatus("pending");
            newOrder.setTotalCost(minimumTotalCost); 
            newOrder.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            newOrder.setAssignedAdminId(null); 

            rentalOrderService.createRentalOrder(newOrder); 

            // 7. Handle Success
            String successMsg = String.format(
                "Booking request submitted successfully! Minimum estimated cost for 1 day: Rs. %.2f. Your request is awaiting admin approval.",
                minimumTotalCost
            );
            request.getSession().setAttribute("successMessage", successMsg);
            response.sendRedirect(request.getContextPath() + "/customer/rentals"); // Or dashboard

         } catch (NumberFormatException e) {
             throw new ServletException("Invalid Truck Type ID format in booking request.", e);
         } catch (IllegalArgumentException e) {
             throw new ServletException("Booking data validation failed: " + e.getMessage(), e);
         } catch (Exception e) { 
             e.printStackTrace();
             
             throw new ServletException("Failed to process booking request: " + e.getMessage(), e);
         }
    }
    
    private BigDecimal calculateTotalCost(TruckType truckType, Timestamp pickupDate, Timestamp estimatedReturnDate) {
        if (truckType == null || truckType.getDailyRate() == null || pickupDate == null || estimatedReturnDate == null) {
            System.err.println("Cannot calculate cost: TruckType, its daily rate, or dates are null.");
            return null;
        }
        BigDecimal dailyRate = truckType.getDailyRate();
        if (dailyRate.compareTo(BigDecimal.ZERO) <= 0) {
             System.err.println("Warning: Daily rate for truck type " + truckType.getName() + " is zero or negative.");
             return BigDecimal.ZERO; 
        }
        
        if (!estimatedReturnDate.after(pickupDate)) {
            
            System.err.println("Warning: Return date is not after pickup date for cost calculation. Defaulting to 1 day rate.");
            return dailyRate;
        }
        try {
            long durationMillis = estimatedReturnDate.getTime() - pickupDate.getTime();
            long millisPerDay = 1000 * 60 * 60 * 24; // Milliseconds in a day
            long durationDays = (long) Math.ceil((double) durationMillis / millisPerDay); // Round up to the nearest whole day

            if (durationDays <= 0) { 
                durationDays = 1; 
            }
            return dailyRate.multiply(BigDecimal.valueOf(durationDays));
        } catch (Exception e) {
             e.printStackTrace();
             System.err.println("Error during cost calculation: " + e.getMessage());
             return null;
        }
    }
    
    private void showRentals(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("customer") == null) {
            SessionUtil.setErrorMessage(request, "Please login to view your rentals.");
            response.sendRedirect(request.getContextPath() + "/customer/login");
            return;
        }
        Customer customer = (Customer) session.getAttribute("customer");

        try {
            List<RentalOrder> rentalHistory = rentalOrderService.getRentalOrdersByCustomerId(customer.getCustomerId());
            request.setAttribute("rentalHistory", rentalHistory);
            System.out.println("Fetched " + (rentalHistory != null ? rentalHistory.size() : "null") + " rental orders for customer ID: " + customer.getCustomerId()); // DEBUG Line
            request.getRequestDispatcher("/WEB-INF/views/customer/rentals.jsp").forward(request, response);
        } catch (Exception e) {
             e.printStackTrace();
             request.setAttribute("errorMessage", "Could not load your rental history: " + e.getMessage());
             request.getRequestDispatcher("/WEB-INF/views/customer/dashboard.jsp").forward(request, response);
        }
    }   
    
}