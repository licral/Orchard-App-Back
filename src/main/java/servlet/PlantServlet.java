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
        name = "PlantServlet",
        urlPatterns = {"/plant/*"}
    )
public class PlantServlet extends HttpServlet {
	private String organisation_id;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String[] params = req.getPathInfo().split("/");
        String option = params[1];
        System.out.println(option);
        System.out.println(params);
        System.out.println("Testing print statement");

    	if(option == null || !isAuthorised(req.getHeader("Authorization"))){
    		resp.sendError(400);
    	} else {
            if(option.equals("all")){
                getallPlants(resp);
            }
            else {
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

    private void getallPlants(HttpServletResponse resp) throws ServletException, IOException{
        Connection con = (Connection)getServletContext().getAttribute("DBConnection");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement("select a.activity_id, a.date, a.time, a.plant_id, c.variety, d.species, e.activity_type from activities a inner join plant_record b on a.plant_id=b.plant_id inner join variety c on b.variety_id=c.variety_id inner join species d on c.species_id=d.species_id inner join activity_types e on a.type_id=e.type_id where a.organisation_id=? order by a.date desc, a.time desc limit 10");
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

                System.out.println(activityArray);

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
