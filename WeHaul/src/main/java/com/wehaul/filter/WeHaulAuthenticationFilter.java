package com.wehaul.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // Optional: direct session access if SessionUtil doesn't cover all

import java.io.IOException;

import com.wehaul.model.Customer; // Your Customer model
import com.wehaul.util.SessionUtil; // Your Session Utility

@WebFilter(asyncSupported = true, urlPatterns = "/*") // Apply to all requests
public class WeHaulAuthenticationFilter implements Filter {

    private static final String CUSTOMER_LOGIN_URI = "/customer/login";
    private static final String CUSTOMER_REGISTER_URI = "/customer/register";
    private static final String CUSTOMER_DASHBOARD_URI = "/customer/dashboard";
    private static final String ADMIN_DASHBOARD_URI = "/admin/dashboard";
    private static final String HOME_URI = "/"; // Assuming index.jsp is at the root

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization logic, if any
        System.out.println("WeHaulAuthenticationFilter initialized.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String contextPath = req.getContextPath();
        String requestURI = req.getRequestURI();
        String path = requestURI.substring(contextPath.length()); // Get path relative to context root

        System.out.println("FILTER: Request for Path: " + path + " (Full URI: " + requestURI + ")"); // LOG
        
        // --- 1. Allow Static Resources and Public Pages Unconditionally ---
        if (path.startsWith("/resources/") || 
            path.equals(HOME_URI) ||          
            path.equals("") ||                
            path.equals(CUSTOMER_LOGIN_URI) ||
            path.equals(CUSTOMER_REGISTER_URI)) {
        	System.out.println("FILTER: Allowing public/static resource: " + path); // LOG
            chain.doFilter(request, response);
            return;
        }

        // --- 2. Get Logged-in User Information ---
        Customer loggedInCustomer = SessionUtil.getLoggedInCustomer(req); // Assumes this returns Customer or null
        boolean isCustomerLoggedIn = (loggedInCustomer != null);
        boolean isAdminLoggedIn = (loggedInCustomer != null && loggedInCustomer.isAdmin());
        System.out.println("FILTER: isCustomerLoggedIn=" + isCustomerLoggedIn + ", isAdminLoggedIn=" + isAdminLoggedIn); // LOG

        // --- 3. Handle Admin Area Access (/admin/*) ---
        if (path.startsWith("/admin/")) {
        	System.out.println("FILTER: Path is an admin path: " + path); // LOG
        	if (isAdminLoggedIn) {
                chain.doFilter(request, response); // Admin is logged in, allow access
            } else {
            	System.out.println("FILTER: Admin NOT logged in. Redirecting to customer login from: " + path); // LOG
                // Not an admin (or not logged in at all)
                SessionUtil.setErrorMessage(req, "You are not authorized to access the admin area.");
                res.sendRedirect(contextPath + CUSTOMER_LOGIN_URI); // Redirect to customer login
            }
            return;
        }

        // --- 4. Handle Customer Area Access (/customer/*) ---
        if (path.startsWith("/customer/")) {
             System.out.println("FILTER: Path is a customer path (not login/register): " + path); // LOG
            if (isCustomerLoggedIn) {
                System.out.println("FILTER: Customer IS logged in. Chaining to servlet for: " + path); // LOG
                chain.doFilter(request, response);
            } else {
                System.out.println("FILTER: Customer NOT logged in. Redirecting to customer login from: " + path); // LOG
                SessionUtil.setErrorMessage(req, "Please login to access this page.");
                res.sendRedirect(contextPath + CUSTOMER_LOGIN_URI);
            }
            return;
        }
        
        System.out.println("FILTER: Path not explicitly handled by auth rules, chaining: " + path); // LOG
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup logic, if any
        System.out.println("WeHaulAuthenticationFilter destroyed.");
    }
}