package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import java.security.Key;

@WebServlet(
        name = "LogoutServlet",
        urlPatterns = {"/logout"}
    )
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

    	String token = req.getParameter("token");

    	if(token == null){
    		resp.sendError(400);
    	} else {
    		Connection con = (Connection)getServletContext().getAttribute("DBConnection");
        	PreparedStatement ps = null;
    		try {
    			ps = con.prepareStatement("update users set token=NULL where token=?");
    			ps.setString(1, token);
    			ps.execute();
    			resp.setStatus(200);
    		} catch (SQLException e) {
    			e.printStackTrace();
    			System.out.println("Database connection problem");
    			throw new ServletException("DB Connection problem.");
    		}finally{
    			try {
    				ps.close();
    			} catch (SQLException e) {
    				System.out.println("SQLException in closing PreparedStatement");
    			}

    		}
    	}
    }

}
