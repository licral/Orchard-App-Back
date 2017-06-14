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
            } else if(activity.equals("plant")){
                if(params[2] == null){
                    resp.sendError(400);
                } else {
                    getPlantHistory(resp, params[2]);
                }
            }
            else {
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
            ps = con.prepareStatement("select h.variety, g.species, h.activity_id, h.plant_id, h.date, h.time, h.activity_type from species g inner join (select e.variety as variety, e.species_id as species_id, f.activity_id as activity_id, f.plant_id as plant_id, f.date as date, f.time as time, f.activity_type as activity_type from variety e inner join (select c.variety_id as variety_id, d.activity_id as activity_id, d.plant_id as plant_id, d.date as date, d.time as time, d.activity_type as activity_type from plant_record c inner join (select a.activity_id as activity_id, a.plant_id as plant_id, a.date as date, a.time as time, b.activity_type as activity_type from activities a inner join activity_types b on a.type_id=b.type_id where a.organisation_id=? order by a.date DESC, a.time DESC LIMIT 10) d on c.plant_id=d.plant_id) f on e.variety_id=f.variety_id) h on g.species_id=h.species_id");
            ps.setString(1, organisation_id);
            rs = ps.executeQuery();
            String activityArray = "[";
            if(rs != null && rs.next()){
                do{
                    activityArray += "{\"plant_id\":\"" + rs.getString("plant_id") + "\", \"date\":\"" + rs.getDate("date") + "\", \"time\":\"" + rs.getTime("time") + "\", \"activity_id\":\"" + rs.getInt("activity_id") + "\", \"activity_type\":\"" + rs.getString("activity_type") + "\", \"species\":\"" + rs.getString("species") + "\", \"variety\":\"" + rs.getString("variety") + "\"}";
                    if(!rs.isLast()){
                        activityArray += ",";
                    }

                } while(rs.next());
                activityArray += "]";

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

    private void getPlantHistory(HttpServletResponse resp, String plant_id) throws ServletException, IOException{
        Connection con = (Connection)getServletContext().getAttribute("DBConnection");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement("select h.variety, g.species, h.activity_id, h.plant_id, h.date, h.time, h.activity_type from species g inner join (select e.variety as variety, e.species_id as species_id, f.activity_id as activity_id, f.plant_id as plant_id, f.date as date, f.time as time, f.activity_type as activity_type from variety e inner join (select c.variety_id as variety_id, d.activity_id as activity_id, d.plant_id as plant_id, d.date as date, d.time as time, d.activity_type as activity_type from plant_record c inner join (select a.activity_id as activity_id, a.plant_id as plant_id, a.date as date, a.time as time, b.activity_type as activity_type from activities a inner join activity_types b on a.type_id=b.type_id where a.plant_id=? order by a.date DESC, a.time DESC LIMIT 10) d on c.plant_id=d.plant_id) f on e.variety_id=f.variety_id) h on g.species_id=h.species_id");
            ps.setString(1, plant_id);
            rs = ps.executeQuery();
            String activityArray = "[";
            if(rs != null && rs.next()){
                do{
                    activityArray += "{\"plant_id\":\"" + rs.getString("plant_id") + "\", \"date\":\"" + rs.getDate("date") + "\", \"time\":\"" + rs.getTime("time") + "\", \"activity_id\":\"" + rs.getInt("activity_id") + "\", \"activity_type\":\"" + rs.getString("activity_type") + "\", \"species\":\"" + rs.getString("species") + "\", \"variety\":\"" + rs.getString("variety") + "\"}";
                    if(!rs.isLast()){
                        activityArray += ",";
                    }

                } while(rs.next());
                activityArray += "]";

                PrintWriter write = resp.getWriter();
                write.write(activityArray);
                write.flush();
                write.close();
            }else{
                resp.setStatus(400);
                PrintWriter write = resp.getWriter();
                write.write("No Results");
                write.flush();
                write.close();
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
