package com.wehaul.controller;

import com.wehaul.model.Truck;
import com.wehaul.model.Customer;
import com.wehaul.model.DashboardStatsModel;
import com.wehaul.model.RentalOrder;
import com.wehaul.service.AdminService;
import com.wehaul.service.CustomerService;
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

@WebServlet("/admin/*")
public class AdminController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private AdminService adminService;
    private CustomerService customerService;

    @Override
    public void init() throws ServletException {
        adminService = new AdminService();
        customerService = new CustomerService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!SessionUtil.isAdminLoggedIn(request)) {
            SessionUtil.setErrorMessage(request, "You are not authorized to access the admin area.");
            response.sendRedirect(request.getContextPath() + "/customer/login");
            return;
        }

        String action = request.getPathInfo();
        if (action == null || action.equals("/")) {
            action = "/dashboard";
        }

        try {
            switch (action) {
                case "/dashboard":
                    showDashboard(request, response);
                    break;
                case "/trucks":
                    listTrucks(request, response);
                    break;
                case "/customers":
                    listCustomers(request, response);
                    break;
                case "/orders":
                    listOrders(request, response);
                    break;
                case "/logout":
                    logoutAdmin(request, response);
                    break;
                case "/editOrder":
                    showEditOrderForm(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log for server admin
            request.setAttribute("errorMessage", "An unexpected admin error occurred: " + e.getMessage());
            showDashboard(request, response); // Fallback
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!SessionUtil.isAdminLoggedIn(request)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You must be logged in as an admin to perform this action.");
            return;
        }

        String action = request.getPathInfo();
        if (action == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try {
            switch (action) {
                case "/approveOrder":
                    approveOrder(request, response);
                    break;
                case "/updateOrder":
                    updateOrder(request, response);
                    break;
                case "/deleteTruck":
                    deleteTruck(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "The requested admin POST action was not found.");
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log for server admin
            String errorMessage = "An error occurred: " + e.getMessage();
            SessionUtil.setErrorMessage(request, errorMessage); // For redirect
            request.setAttribute("errorMessage", errorMessage); // For forward

            if ("/approveOrder".equals(action)) {
                listOrders(request, response);
            } else if ("/updateOrder".equals(action)) {
                String orderIdParam = request.getParameter("orderId");
                if (orderIdParam != null && !orderIdParam.isEmpty()) {
                    try {
                        int orderId = Integer.parseInt(orderIdParam);
                        RentalOrder orderToEdit = adminService.getOrderById(orderId);
                        List<Customer> allCustomers = customerService.getAllCustomers();
                        List<Truck> allTrucks = adminService.getAllTrucks();

                        request.setAttribute("order", orderToEdit);
                        request.setAttribute("allCustomers", allCustomers);
                        request.setAttribute("allTrucks", allTrucks);
                        request.getRequestDispatcher("/WEB-INF/views/admin/editOrder.jsp").forward(request, response);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        SessionUtil.setErrorMessage(request, "Additionally, an error occurred trying to reload the edit form: " + ex.getMessage());
                        listOrders(request, response);
                    }
                } else {
                    SessionUtil.setErrorMessage(request, "Order ID was missing during error handling for update.");
                    listOrders(request, response);
                }
            } else {
                showDashboard(request, response);
            }
        }
    }

    private void showDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            DashboardStatsModel stats = adminService.getCurrentDashboardStats();
            request.setAttribute("dashboardStats", stats);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            request.setAttribute("statsError", "Could not load dashboard statistics.");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("statsError", "An unexpected error occurred while fetching statistics.");
        }
        request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
    }

    private void listTrucks(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Truck> trucks = adminService.getAllTrucks();
            request.setAttribute("trucks", trucks);
            request.getRequestDispatcher("/WEB-INF/views/admin/trucks.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException("Error listing trucks", e);
        }
    }

    private void listCustomers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Customer> customers = customerService.getAllCustomers();
            request.setAttribute("customers", customers);
            request.getRequestDispatcher("/WEB-INF/views/admin/customers.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException("Error listing customers", e);
        }
    }

    private void listOrders(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<RentalOrder> orders = adminService.getAllOrders();
            request.setAttribute("orders", orders);
            request.getRequestDispatcher("/WEB-INF/views/admin/orders.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException("Error listing orders", e);
        }
    }

    private void approveOrder(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int orderId = Integer.parseInt(request.getParameter("orderId"));
            Customer adminUser = SessionUtil.getLoggedInCustomer(request);

            if (adminUser == null || !adminUser.isAdmin()) {
                SessionUtil.setErrorMessage(request, "Authorization failed for approval.");
                response.sendRedirect(request.getContextPath() + "/customer/login");
                return;
            }
            int adminId = adminUser.getCustomerId();
            int truckId = Integer.parseInt(request.getParameter("truckId"));

            System.out.println("Sending request to service");
            
            adminService.approveRentalOrder(orderId, adminId, truckId);
            SessionUtil.setSuccessMessage(request, "Order #" + orderId + " approved successfully.");
            response.sendRedirect(request.getContextPath() + "/admin/orders");
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid ID format for order or truck.");
            listOrders(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Failed to approve order: " + e.getMessage());
            listOrders(request, response);
        }
    }

    private void updateOrder(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String orderIdStr = request.getParameter("orderId");
            if (orderIdStr == null || orderIdStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Order ID is missing for the update operation.");
            }
            int orderId = Integer.parseInt(orderIdStr);

            RentalOrder order = adminService.getOrderById(orderId);
            if (order == null) {
                throw new ServletException("Order not found for update with ID: " + orderId);
            }

            Customer adminUser = SessionUtil.getLoggedInCustomer(request);
            if (adminUser == null || !adminUser.isAdmin()) {
                throw new ServletException("Admin not properly logged in for this action.");
            }

            String customerIdStr = request.getParameter("customerId");
            if (customerIdStr == null || customerIdStr.trim().isEmpty()) throw new IllegalArgumentException("Customer ID is required.");
            order.setCustomerId(Integer.parseInt(customerIdStr));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            String pickupDateParam = request.getParameter("pickupDate");
            String returnDateParam = request.getParameter("returnDate");
            if (pickupDateParam == null || pickupDateParam.trim().isEmpty()) throw new IllegalArgumentException("Pickup date is required.");
            if (returnDateParam == null || returnDateParam.trim().isEmpty()) throw new IllegalArgumentException("Return date is required.");

            order.setPickupDate(Timestamp.valueOf(LocalDateTime.parse(pickupDateParam, formatter)));
            order.setReturnDate(Timestamp.valueOf(LocalDateTime.parse(returnDateParam, formatter)));

            if (!order.getReturnDate().after(order.getPickupDate())) {
                throw new IllegalArgumentException("Return date must be after the pickup date.");
            }

            order.setPickupLocation(request.getParameter("pickupLocation"));
            order.setReturnLocation(request.getParameter("returnLocation"));
            order.setNepaliPickupLocation(request.getParameter("nepaliPickupLocation"));
            order.setNepaliReturnLocation(request.getParameter("nepaliReturnLocation"));

            String oldStatus = order.getStatus();
            Integer oldAssignedTruckId = order.getAssignedTruckId();

            String newStatus = request.getParameter("status");
            if (newStatus == null || newStatus.trim().isEmpty()) throw new IllegalArgumentException("Order status is required.");
            order.setStatus(newStatus);

            String approvalStatus = request.getParameter("approvalStatus");
            if (approvalStatus == null || approvalStatus.trim().isEmpty()) throw new IllegalArgumentException("Approval status is required.");
            order.setApprovalStatus(approvalStatus);

            String assignedTruckIdStr = request.getParameter("assignedTruckId");
            Integer newAssignedTruckId = null;
            if (assignedTruckIdStr != null && !assignedTruckIdStr.trim().isEmpty()) {
                newAssignedTruckId = Integer.parseInt(assignedTruckIdStr);
            }
            order.setAssignedTruckId(newAssignedTruckId);
            // order.setTruckId(newAssignedTruckId); // If truck_id in model should mirror assigned_truck_id

            String totalCostStr = request.getParameter("totalCost");
            if (totalCostStr == null || totalCostStr.trim().isEmpty()) throw new IllegalArgumentException("Total cost is required.");
            order.setTotalCost(new BigDecimal(totalCostStr));

            adminService.updateRentalOrder(order);

            // Truck Status Update Logic
            if (oldAssignedTruckId != null && (newAssignedTruckId == null || !oldAssignedTruckId.equals(newAssignedTruckId))) {
                 if ("approved".equals(oldStatus) || "in_progress".equals(oldStatus) || "confirmed".equals(oldStatus)) {
                    adminService.updateTruckStatus(oldAssignedTruckId, "available");
                 }
            }
            if (newAssignedTruckId != null) {
                if ("approved".equals(newStatus) || "in_progress".equals(newStatus) || "confirmed".equals(newStatus)) {
                    Truck assignedTruck = adminService.getTruckById(newAssignedTruckId);
                    if (assignedTruck != null && "available".equalsIgnoreCase(assignedTruck.getStatus())) {
                        adminService.updateTruckStatus(newAssignedTruckId, "rented");
                    } else if (assignedTruck != null && !newAssignedTruckId.equals(oldAssignedTruckId)) {
                        System.err.println("Warning: Newly Assigned Truck ID " + newAssignedTruckId + " for order " + orderId + " is not 'available'. Status: " + assignedTruck.getStatus());
                        SessionUtil.setErrorMessage(request, "Warning: Newly Assigned Truck ID " + newAssignedTruckId + " is not 'available'. Its status was not changed to 'rented'.");
                    }
                }
            }
            if (order.getAssignedTruckId() != null &&
               (newStatus.equals("completed") || newStatus.startsWith("cancelled_")) &&
               (oldStatus.equals("approved") || oldStatus.equals("in_progress") || oldStatus.equals("confirmed"))) {
                adminService.updateTruckStatus(order.getAssignedTruckId(), "available");
            }

            SessionUtil.setSuccessMessage(request, "Order #" + orderId + " updated successfully.");
            response.sendRedirect(request.getContextPath() + "/admin/orders");

        } catch (NumberFormatException e) { // Catches parsing errors for orderId, customerId, assignedTruckId, totalCost
            e.printStackTrace();
            request.setAttribute("errorMessage", "Invalid number format in form data: " + e.getMessage());
            reShowEditFormOnError(request, response, request.getParameter("orderId"));
        } catch (DateTimeParseException e) { // Catches date parsing errors
            e.printStackTrace();
            request.setAttribute("errorMessage", "Invalid date/time format: " + e.getMessage());
            reShowEditFormOnError(request, response, request.getParameter("orderId"));
        } catch (IllegalArgumentException e) { // Catches your custom validation errors
            e.printStackTrace();
            request.setAttribute("errorMessage", "Update failed: " + e.getMessage());
            reShowEditFormOnError(request, response, request.getParameter("orderId"));
        } catch (Exception e) { // Catches SQLException, ClassNotFoundException, or other service errors
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error updating order: " + e.getMessage());
            reShowEditFormOnError(request, response, request.getParameter("orderId"));
        }
    }

    private void reShowEditFormOnError(HttpServletRequest request, HttpServletResponse response, String orderIdParam)
            throws ServletException, IOException {
        if (orderIdParam != null && !orderIdParam.isEmpty()) {
            try {
                int orderIdForForm = Integer.parseInt(orderIdParam);
                RentalOrder orderToEdit = adminService.getOrderById(orderIdForForm);
                List<Customer> allCustomers = customerService.getAllCustomers();
                List<Truck> allTrucks = adminService.getAllTrucks();

                request.setAttribute("order", orderToEdit);
                request.setAttribute("allCustomers", allCustomers);
                request.setAttribute("allTrucks", allTrucks);
                // The "errorMessage" attribute should already be set by the calling catch block
                request.getRequestDispatcher("/WEB-INF/views/admin/editOrder.jsp").forward(request, response);
            } catch (Exception ex) {
                ex.printStackTrace();
                SessionUtil.setErrorMessage(request, "Error updating order. Additionally, failed to reload edit form: " + ex.getMessage());
                response.sendRedirect(request.getContextPath() + "/admin/orders");
            }
        } else {
            SessionUtil.setErrorMessage(request, "Error updating order: Order ID was missing during error handling.");
            response.sendRedirect(request.getContextPath() + "/admin/orders");
        }
    }

    private void showEditOrderForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String orderIdParam = request.getParameter("orderId");
            if (orderIdParam == null || orderIdParam.isEmpty()) {
                SessionUtil.setErrorMessage(request, "Order ID is required to edit an order.");
                response.sendRedirect(request.getContextPath() + "/admin/orders");
                return;
            }
            int orderId = Integer.parseInt(orderIdParam);
            RentalOrder order = adminService.getOrderById(orderId);

            if (order == null) {
                SessionUtil.setErrorMessage(request, "Order with ID " + orderId + " not found.");
                response.sendRedirect(request.getContextPath() + "/admin/orders");
                return;
            }
            List<Customer> allCustomers = customerService.getAllCustomers();
            List<Truck> allTrucks = adminService.getAllTrucks();

            request.setAttribute("order", order);
            request.setAttribute("allCustomers", allCustomers);
            request.setAttribute("allTrucks", allTrucks);

            request.getRequestDispatcher("/WEB-INF/views/admin/editOrder.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            SessionUtil.setErrorMessage(request, "Invalid Order ID format: " + request.getParameter("orderId"));
            response.sendRedirect(request.getContextPath() + "/admin/orders");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            SessionUtil.setErrorMessage(request, "Database error fetching data for edit order form: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/orders");
        } catch (Exception e) {
            e.printStackTrace();
            SessionUtil.setErrorMessage(request, "An unexpected error occurred preparing the edit form: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/orders");
        }
    }

    private void deleteTruck(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String truckIdStr = request.getParameter("truckId");
        if (truckIdStr == null || truckIdStr.isEmpty()) {
            SessionUtil.setErrorMessage(request, "Truck ID was missing for delete operation.");
            response.sendRedirect(request.getContextPath() + "/admin/trucks");
            return;
        }

        try {
            int truckId = Integer.parseInt(truckIdStr);
            boolean deleted = adminService.deleteTruckById(truckId);

            if (deleted) {
                SessionUtil.setSuccessMessage(request, "Truck ID #" + truckId + " deleted successfully.");
            } else {
                // This case might not be hit if deleteTruckById throws an exception on failure/no rows
                SessionUtil.setErrorMessage(request, "Truck ID #" + truckId + " could not be deleted or was not found.");
            }
        } catch (NumberFormatException e) {
            SessionUtil.setErrorMessage(request, "Invalid Truck ID format for deletion: '" + truckIdStr + "'.");
        } catch (SQLException | ClassNotFoundException e) {
            SessionUtil.setErrorMessage(request, "Error deleting truck: " + e.getMessage());
            e.printStackTrace(); // Log for server admin
        } catch (Exception e) { // Catch any other unexpected errors
            SessionUtil.setErrorMessage(request, "An unexpected error occurred during truck deletion: " + e.getMessage());
            e.printStackTrace();
        }
        response.sendRedirect(request.getContextPath() + "/admin/trucks");
    }

    private void logoutAdmin(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/customer/login?logout=true&ref=admin");
    }
}