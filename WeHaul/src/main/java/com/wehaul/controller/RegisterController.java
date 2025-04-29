package com.wehaul.controller;

import com.wehaul.model.Customer;
import com.wehaul.service.CustomerService;
import com.wehaul.util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/customer/register")
public class RegisterController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private CustomerService customerService;

    @Override
    public void init() throws ServletException {
        super.init();
        customerService = new CustomerService(); // Initializes service
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/customer/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Retrieve form data
            String email = request.getParameter("email");
            
            // Check if email already exists
            if (customerService.getCustomerByEmail(email) != null) {
                request.setAttribute("errorMessage", "Email already registered.");
                request.getRequestDispatcher("/WEB-INF/views/customer/register.jsp").forward(request, response);
                return;
            }

            Customer customer = new Customer();
            customer.setFirstName(request.getParameter("firstName"));
            customer.setLastName(request.getParameter("lastName"));
            customer.setEmail(email);
            customer.setPhone(request.getParameter("phone"));
            customer.setAddress(request.getParameter("address"));
            customer.setCity(request.getParameter("city"));
            customer.setState(request.getParameter("state"));

            // Encrypt password
            String password = request.getParameter("password");
            String encryptedPassword = PasswordUtil.encrypt(customer.getEmail(), password);
            customer.setPasswordHash(encryptedPassword);

            // Register the new customer
            customerService.addCustomer(customer);

            // Redirect to login page with success message
            response.sendRedirect(request.getContextPath() + "/customer/login?registered=true");

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Registration failed. Please try again.");
            request.getRequestDispatcher("/WEB-INF/views/customer/register.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An unexpected error occurred. Please try again.");
            request.getRequestDispatcher("/WEB-INF/views/customer/register.jsp").forward(request, response);
        }
    }
}

