package servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.PrintWriter;

import java.lang.NumberFormatException;
import java.lang.IllegalArgumentException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureException;

@WebServlet(
        name = "RegisterServlet",
        urlPatterns = {"/register"}
    )
public class RegisterServlet extends HttpServlet {
	private String organisation_id;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

    	if(!isAuthorised(req.getHeader("Authorization"))){
    		resp.sendError(400);
    	} else {
    		String plant_id = req.getParameter("plant_id");
    		String visual_tag = req.getParameter("visual_tag");
    		String notes = req.getParameter("notes");
    		try{
    			int variety_id = Integer.parseInt(req.getParameter("variety_id"));
    			int longitude = Integer.parseInt(req.getParameter("longitude"));
    			int latitude = Integer.parseInt(req.getParameter("latitude"));
    			try{
    				Date date = Date.valueOf(req.getParameter("date"));
    				System.out.println(date);
    			} catch (IllegalArgumentException e){
    				System.out.println(e.getMessage());
    				resp.sendError(400);
    			}

				// Connection con = (Connection)getServletContext().getAttribute("DBConnection");
		  //   	PreparedStatement ps = null;
				// ResultSet rs = null;
				// try {
				// 	ps = con.prepareStatement("insert into plant_record (plant_id, visual_tag, variety_id, organisation_id, longitude, latitude, date, notes) values (?, ?, ?, ?, ?, ?, ?)");
				// 	ps.setString(1, plant_id);
				// 	ps.setString(2, visual_tag);
				// 	ps.setInt(3, variety_id);
				// 	ps.setString(4, organisation_id);
				// 	ps.setInt(5, longitude);
				// 	ps.setInt(6, latitude);
				// 	ps.setDate(7, )
				// 	rs = ps.executeQuery();
				// 	String speciesArray = "{";
				// 	if(rs != null && rs.next()){
				// 		do{
				// 			speciesArray += "\"" + rs.getString("species_id") + "\":\"" + rs.getString("species") + "\"";
				// 			if(!rs.isLast()){
				// 				speciesArray += ",";
				// 			}

				// 		} while(rs.next());
				// 		speciesArray += "}";

				// 		PrintWriter write = resp.getWriter();
				// 		write.write(speciesArray);
		  //   	    	write.flush();
		  //   	    	write.close();
				// 	}else{
				// 		System.out.println("No results");
				// 		resp.sendError(400);
				// 	}
				// } catch (SQLException e) {
				// 	e.printStackTrace();
				// 	System.out.println("Database connection problem");
				// 	throw new ServletException("DB Connection problem.");
				// }finally{
				// 	try {
				// 		rs.close();
				// 		ps.close();
				// 	} catch (SQLException e) {
				// 		System.out.println("SQLException in closing PreparedStatement or ResultSet");
				// 	}

				// }
    		} catch (NumberFormatException e){
    			System.out.println(e.getMessage());
    			resp.sendError(400);
    		}
	    }
    }

    private boolean isAuthorised(String token) throws ServletException{
    	try{
    		Claims claims = Jwts.parser().setSigningKey("secret".getBytes("UTF-8")).parseClaimsJws(token).getBody();

    		String username = claims.get("username").toString();

    		if(username == null){
        		return false;
        	} else {
        		Connection con = (Connection)getServletContext().getAttribute("DBConnection");
            	PreparedStatement ps = null;
        		ResultSet rs = null;
        		try {
        			ps = con.prepareStatement("select organisation_id from users where token=?");
        			ps.setString(1, token);
        			rs = ps.executeQuery();
        			if(rs != null && rs.next()){
        				if(username.equals(rs.getString("organisation_id"))){
        					this.organisation_id = username;
        					return true;
        				} else {
        					return false;
        				}
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
        	}
    	} catch (SignatureException e) {
    		System.out.print(e.getMessage());
			return false;
    	} catch (UnsupportedEncodingException e){
    		System.out.print(e.getMessage());
    		return false;
    	}
    }
}
