package com.wehaul.util;

import com.wehaul.model.Customer; 
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class SessionUtil {

    private static final String CUSTOMER_SESSION_KEY = "customer";
    private static final String ERROR_MESSAGE_KEY = "errorMessage";
    private static final String SUCCESS_MESSAGE_KEY = "successMessage";

    // User Session Management

    public static void createCustomerSession(HttpServletRequest request, Customer customer) {
        HttpSession session = request.getSession(true); 
        session.setAttribute(CUSTOMER_SESSION_KEY, customer);
    }

    public static Customer getLoggedInCustomer(HttpServletRequest request) {
        HttpSession session = request.getSession(false); 
        if (session != null) {
            return (Customer) session.getAttribute(CUSTOMER_SESSION_KEY);
        }
        return null;
    }

    public static boolean isCustomerLoggedIn(HttpServletRequest request) {
        return getLoggedInCustomer(request) != null;
    }

    public static boolean isAdminLoggedIn(HttpServletRequest request) {
        Customer customer = getLoggedInCustomer(request);
        return customer != null && customer.isAdmin();
    }

    public static void invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    // Error Message Management

    public static void setErrorMessage(HttpServletRequest request, String message) {
        HttpSession session = request.getSession(true);
        session.setAttribute(ERROR_MESSAGE_KEY, message);
    }

    public static String getAndClearErrorMessage(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String message = (String) session.getAttribute(ERROR_MESSAGE_KEY);
            if (message != null) {
                session.removeAttribute(ERROR_MESSAGE_KEY); 
                return message;
            }
        }
        return null;
    }

    public static void setSuccessMessage(HttpServletRequest request, String message) {
        HttpSession session = request.getSession(true);
        session.setAttribute(SUCCESS_MESSAGE_KEY, message);
    }

    public static String getAndClearSuccessMessage(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String message = (String) session.getAttribute(SUCCESS_MESSAGE_KEY);
            if (message != null) {
                session.removeAttribute(SUCCESS_MESSAGE_KEY); 
                return message;
            }
        }
        return null;
    }

    // Session Attribute Management

    public static void setSessionAttribute(HttpServletRequest request, String key, Object value) {
        HttpSession session = request.getSession(true);
        session.setAttribute(key, value);
    }

    public static Object getSessionAttribute(HttpServletRequest request, String key) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return session.getAttribute(key);
        }
        return null;
    }

    public static void removeSessionAttribute(HttpServletRequest request, String key) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(key);
        }
    }
}
