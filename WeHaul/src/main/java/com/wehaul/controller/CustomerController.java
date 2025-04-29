package com.wehaul.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.wehaul.model.Customer;
import com.wehaul.service.CustomerService;
import com.wehaul.util.PasswordUtil;

@WebServlet("/customer/*")
public class CustomerController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private CustomerService customerService = new CustomerService();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getPathInfo();
        
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
                default:
                    response.sendRedirect(request.getContextPath() + "/customer/dashboard");
            }
        } catch (Exception e) {
            throw new ServletException(e);
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
        request.getRequestDispatcher("/WEB-INF/views/customer/profile.jsp").forward(request, response);
    }

    private void showRentals(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/customer/rentals.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getPathInfo() != null ? request.getPathInfo() : "";
        
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
                default:
                    response.sendRedirect(request.getContextPath() + "/customer/dashboard");
            }
        } catch (Exception e) {
            throw new ServletException(e);
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
            request.setAttribute("error", "Invalid email or password");
            request.getRequestDispatcher("/WEB-INF/views/customer/login.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Login failed: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/login.jsp").forward(request, response);
        }
    }

    private void registerCustomer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Customer customer = new Customer();
            customer.setFirstName(request.getParameter("firstName"));
            customer.setLastName(request.getParameter("lastName"));
            customer.setEmail(request.getParameter("email"));
            customer.setPhone(request.getParameter("phone"));

            String address = request.getParameter("address");
            String city = request.getParameter("city"); 
            String state = request.getParameter("state");

            if(address == null || address.isEmpty() || city == null || city.isEmpty() || state == null || state.isEmpty()) {
                throw new IllegalArgumentException("All address fields are required");
            }

            customer.setAddress(address);
            customer.setCity(city);
            customer.setState(state);

            String rawPassword = request.getParameter("password");
            String encryptedPassword = PasswordUtil.encrypt(customer.getEmail(), rawPassword);
            customer.setPasswordHash(encryptedPassword);

            customerService.addCustomer(customer);
            response.sendRedirect(request.getContextPath() + "/customer/login?registered=true");
        } catch (Exception e) {
            request.setAttribute("error", "Registration failed: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/register.jsp").forward(request, response);
        }
    }

    private void updateProfile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Customer customer = (Customer) request.getSession().getAttribute("customer");

            customer.setFirstName(request.getParameter("firstName"));
            customer.setLastName(request.getParameter("lastName"));
            customer.setEmail(request.getParameter("email"));
            customer.setPhone(request.getParameter("phone"));
            customer.setAddress(request.getParameter("address"));
            customer.setCity(request.getParameter("city"));
            customer.setState(request.getParameter("state"));

            customerService.updateCustomer(customer);
            request.getSession().setAttribute("customer", customer);
            response.sendRedirect(request.getContextPath() + "/customer/profile?updated=true");
        } catch (Exception e) {
            request.setAttribute("error", "Profile update failed: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/profile.jsp").forward(request, response);
        }
    }
}



