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
        name = "LoginServlet",
        urlPatterns = {"/login"}
    )
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

    	String username = req.getParameter("username");
    	String password = req.getParameter("password");

    	System.out.println(authenticate(username, password));

    	if(username == null || password == null){
    		resp.sendError(400);
    	} else {
    		if(username.equals("Bonnie") && password.equals("12345")){
    	    	PrintWriter write = resp.getWriter();

    	    	String token = Jwts.builder()
    	    			.claim("username", username)
    	    			.claim("password", password)
    	    			.signWith(SignatureAlgorithm.HS512, "secret".getBytes("UTF-8"))
    	    			.compact();

    	    	resp.setStatus(200);
    	    	write.write("{\"data\": \"hello world\", \"id_token\": \"" + token + "\"}");
    	    	write.flush();
    	    	write.close();
    		} else {
    			resp.sendError(400);
    		}
    	}



    }

    private boolean authenticate(String username, String password) throws ServletException{
    	Connection con = (Connection)getServletContext().getAttribute("DBConnection");
    	PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = con.prepareStatement("select password from users where organisation_id='" + username + "'");
			rs = ps.executeQuery();
			if(rs != null && rs.next()){
				return password.equals(rs.getString("password"));
			}else{
				System.out.println("No results");
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Database connection problem");
			throw new ServletException("DB Connection problem.");
		}finally{
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				System.out.println("SQLException in closing PreparedStatement or ResultSet");
			}

		}
		return false;
    }

}
