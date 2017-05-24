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
import java.sql.Time;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureException;

@WebServlet(
        name = "RecordServlet",
        urlPatterns = {"/record/*"}
    )
public class RecordServlet extends HttpServlet {
	private String organisation_id;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String[] params = req.getPathInfo().split("/");
        String activity = params[1];

        if(activity == null || !isAuthorised(req.getHeader("Authorization"))){
            resp.sendError(400);
        } else {
            if(activity.equals("general")){
                recordGeneral(req, resp);
            } else if(activity.equals("fertiliser")){
                // do fertiliser
            } else if(activity.equals("chemical")){
                // do chemical
            } else if(activity.equals("pruning")){
                // do chemical
            } else if(activity.equals("harvest")){
                // do chemical
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

    private void recordGeneral(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String plant_id = req.getParameter("plant_id");
        String notes = req.getParameter("notes");
        try{
            Date date = Date.valueOf(req.getParameter("date"));
            Time time = Time.valueOf(req.getParameter("time"));
            int type_id = Integer.parseInt(req.getParameter("type_id"));

            Connection con = (Connection)getServletContext().getAttribute("DBConnection");
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ps = con.prepareStatement("insert into activities (organisation_id, plant_id, date, time, notes, type_id) values (?, ?, ?, ?, ?, ?)");
                ps.setString(1, organisation_id);
                ps.setString(2, plant_id);
                ps.setDate(3, date);
                ps.setTime(4, time);
                ps.setString(5, notes);
                ps.setInt(6, type_id);
                ps.execute();

                ps = con.prepareStatement("select last_value from activities_activity_id_seq");
                rs = ps.executeQuery();
                if(rs != null && rs.next()){
                    int lastId = rs.getInt("last_value");
                    System.out.println("Last Id: " + lastId);
                }else{
                    System.out.println("No results");
                    resp.sendError(400);
                }
                 
                // PrintWriter write = resp.getWriter();
                // write.write(speciesArray);
                // write.flush();
                // write.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Database connection problem");
                throw new ServletException("DB Connection problem.");
            }finally{
                try {
                    ps.close();
                    rs.close();
                } catch (SQLException e) {
                    System.out.println("SQLException in closing PreparedStatement or ResultSet");
                }
            }
        } catch (NumberFormatException e){
            System.out.println(e.getMessage());
            resp.sendError(400);
        } catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
            resp.sendError(400);
        }
    }
}
