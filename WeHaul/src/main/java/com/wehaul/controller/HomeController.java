package com.wehaul.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet("/")
public class HomeController extends HttpServlet {
    private static final long serialVersionUID = 1L;

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        
		String action = request.getServletPath(); 
		
        if (action == null || action.isEmpty() || action.equals("/")) {
            action = "/home"; 
        }

        try {
            switch (action) {
                case "/home":
                    request.getRequestDispatcher("/WEB-INF/views/index.jsp").forward(request, response);
                    break;
                case "/about":
                    request.getRequestDispatcher("/WEB-INF/views/customer/about.jsp").forward(request, response);
                    break;
                case "/contact":
                    request.getRequestDispatcher("/WEB-INF/views/customer/contact.jsp").forward(request, response);
                    break;
                default:
                    System.out.println("HomeController: Defaulting to index.jsp for action: " + action);
                    request.getRequestDispatcher("/WEB-INF/views/customer/index.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        }
    }
}

