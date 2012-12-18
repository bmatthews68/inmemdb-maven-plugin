package com.btmatthews.testapp;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ListUsersServlet extends HttpServlet {

    @Override
    public void doGet(final HttpServletRequest request,
                      final HttpServletResponse response)
            throws ServletException, IOException {

        final List<User> users = new ArrayList<User>();

        try {
            final Context ctx = new InitialContext();
            final DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/test");
            final Connection con = ds.getConnection();
            try {
                final Statement stmt = con.createStatement();
                final ResultSet rs = stmt.executeQuery(
                        "SELECT USERNAME_TXT,PASSWORD_TXT, NAME_TXT "
                                + "FROM USERS "
                                + "ORDER BY USERNAME_TXT");
                try {
                    while (rs.next()) {
                        final String username = rs.getString("USERNAME_TXT");
                        final String password = rs.getString("PASSWORD_TXT");
                        final String name = rs.getString("NAME_TXT");
                        final User user = new User(username, password, name);
                        users.add(user);
                    }
                } finally {
                    rs.close();
                }
            } finally {
                con.close();
            }
        } catch (final SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        } catch (final NamingException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        request.setAttribute("users", users);

        final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/users.jsp");
        dispatcher.forward(request, response);
    }
}