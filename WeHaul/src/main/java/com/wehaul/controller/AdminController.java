package com.wehaul.controller;

import com.wehaul.model.Truck;
import com.wehaul.model.Customer;
import com.wehaul.model.RentalOrder;
import com.wehaul.service.AdminService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/*")
public class AdminController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private AdminService adminService;

    @Override
    public void init() throws ServletException {
        adminService = new AdminService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
    	String action = request.getPathInfo();
        if (action == null) action = "/";
        
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
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getPathInfo();
        if (action == null) action = "/";

        switch (action) {
            case "/approveOrder":
                approveOrder(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    private void showDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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
            List<Customer> customers = adminService.getAllCustomers();
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
            int adminId = (Integer) request.getSession().getAttribute("adminId");
            int truckId = Integer.parseInt(request.getParameter("truckId"));
            
            adminService.approveRentalOrder(orderId, adminId, truckId);
            response.sendRedirect(request.getContextPath() + "/admin/orders?approved=true");
        } catch (Exception e) {
            request.setAttribute("error", "Failed to approve order: " + e.getMessage());
            listOrders(request, response);
        }
    }
}

