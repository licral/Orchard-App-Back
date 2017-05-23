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
        name = "GetServlet",
        urlPatterns = {"/get/*"}
    )
public class GetServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	String service = req.getPathInfo().substring(1);

    	if(service == null){
    		resp.sendError(400);
    	} else if(service.equals("species")){
    		getSpecies(resp);
    	} else {
    		resp.sendError(400);
    	}
    }

    private void getSpecies(HttpServletResponse resp) throws ServletException, IOException {
    	Connection con = (Connection)getServletContext().getAttribute("DBConnection");
    	PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = con.prepareStatement("select species from species");
			rs = ps.executeQuery();
			if(rs != null && rs.next()){
				while(!rs.isLast()){
					System.out.println(rs.getString("species"));
					rs.next();
				}
			}else{
				System.out.println("No results");
				resp.sendError(400);
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
    }

}
