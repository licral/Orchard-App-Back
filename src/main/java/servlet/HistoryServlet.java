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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String[] params = req.getPathInfo().split("/");
        String activity = params[1];

    	if(activity == null || !isAuthorised(req.getHeader("Authorization"))){
    		resp.sendError(400);
    	} else {
            if(activity.equals("all")){
                getAllActivities(resp);
            } else {
                //handle individual activities
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

    private void getAllActivities(HttpServletResponse resp) throws ServletException, IOException{
        Connection con = (Connection)getServletContext().getAttribute("DBConnection");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement("select activities.activity_id, activities.plant_id, activities.date, activities.time, activity_types.activity_type from activities left join activity_types on activities.type_id=activity_types.type_id where activities.organisation_id=? order by activities.date, activities.time DESC LIMIT 10");
            ps.setString(1, organisation_id);
            rs = ps.executeQuery();
            String activityArray = "{";
            if(rs != null && rs.next()){
                do{
                    activityArray += "\"" + rs.getInt("activity_id") + "\":{\"plant_id\":\"" + rs.getString("plant_id") + "\", \"date\":\"" + rs.getDate("date") + "\"}";
                    if(!rs.isLast()){
                        activityArray += ",";
                    }

                } while(rs.next());
                activityArray += "}";

                PrintWriter write = resp.getWriter();
                write.write(activityArray);
                write.flush();
                write.close();
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
