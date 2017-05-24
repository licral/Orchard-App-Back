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
        name = "HistoryServlet",
        urlPatterns = {"/history/*"}
    )
public class HistoryServlet extends HttpServlet {
	private String organisation_id;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

    	if(!isAuthorised(req.getHeader("Authorization"))){
            System.out.println("Incorrect login");
    		resp.sendError(400);
    	} else {
            System.out.println("Got to history");

            // Connection con = (Connection)getServletContext().getAttribute("DBConnection");
            // PreparedStatement ps = null;
            // ResultSet rs = null;
            // try {
            //     ps = con.prepareStatement("select activities.plant_id, activities.date, activities.time, activity_types.activity_type, activities.notes from activities left join activity_types on activities.type_id=activity_types.type_id where activities.organisation_id=? order by activities.date, activities.time DESC LIMIT 10");
            //     rs = ps.executeQuery();
            //     String activityArray = "{";
            //     if(rs != null && rs.next()){
            //         do{
            //             activityArray += "\"" + rs.getString("type_id") + "\":\"" + rs.getString("activity_type") + "\"";
            //             if(!rs.isLast()){
            //                 activityArray += ",";
            //             }

            //         } while(rs.next());
            //         activityArray += "}";

            //         PrintWriter write = resp.getWriter();
            //         write.write(activityArray);
            //         write.flush();
            //         write.close();
            //     }else{
            //         System.out.println("No results");
            //         resp.sendError(400);
            //     }
            // } catch (SQLException e) {
            //     e.printStackTrace();
            //     System.out.println("Database connection problem");
            //     throw new ServletException("DB Connection problem.");
            // }finally{
            //     try {
            //         rs.close();
            //         ps.close();
            //     } catch (SQLException e) {
            //         System.out.println("SQLException in closing PreparedStatement or ResultSet");
            //     }

            // }
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
