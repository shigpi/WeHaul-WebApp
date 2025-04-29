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

@WebServlet("/customer/login")
public class LoginController extends HttpServlet {
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
        request.getRequestDispatcher("/WEB-INF/views/customer/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            Customer customer = customerService.getCustomerByEmail(email);

            if (customer != null && PasswordUtil.decrypt(customer.getPasswordHash(), email).equals(password)) {
            	request.getSession().setAttribute("customer", customer);
                if (customer.isAdmin()) {
                    response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                } else {
                    response.sendRedirect(request.getContextPath() + "/customer/dashboard");
                }
            } else {
                request.setAttribute("error", "Invalid email or password");
                request.getRequestDispatcher("/WEB-INF/views/customer/login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", "Login failed: ");
            request.getRequestDispatcher("/WEB-INF/views/customer/login.jsp").forward(request, response);
        }
    }
}

