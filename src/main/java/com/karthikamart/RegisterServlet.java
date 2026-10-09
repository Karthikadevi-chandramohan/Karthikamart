package com.karthikamart;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        DatabaseInitializer.initialize();

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        User user = new User(
                name,
                email,
                password,
                "BUYER"
        );

        UserDAO userDAO = new UserDAO();

        boolean success = userDAO.registerUser(user);

        response.setContentType("text/html;charset=UTF-8");

        if (success) {
            response.getWriter().println("Registration successful!");
        } else {
            response.getWriter().println("Registration failed!");
        }
    }
}