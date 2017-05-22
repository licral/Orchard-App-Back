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

    	Connection con = (Connection)getServletContext().getAttribute("DBConnection");
    	PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = con.prepareStatement("select * from users");
			rs = ps.executeQuery();

			if(rs != null && rs.next()){
				System.out.println(rs.getString("organisation_id"));

//				User user = new User(rs.getString("name"), rs.getString("email"), rs.getString("country"), rs.getInt("id"));
//				logger.info("User found with details="+user);
//				HttpSession session = request.getSession();
//				session.setAttribute("User", user);
//				response.sendRedirect("home.jsp");;
			}else{
				System.out.println("Not valid");
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

    	PrintWriter write = resp.getWriter();

    	String username = req.getParameter("username");
    	String password = req.getParameter("password");

    	if(username == null || password == null){
    		resp.sendError(400);
    	} else {
    		if(username.equals("Bonnie") && password.equals("12345")){
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

}
