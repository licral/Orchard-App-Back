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
        name = "CheckServlet",
        urlPatterns = {"/check/*"}
    )
public class CheckServlet extends HttpServlet {
    private String organisation_id;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

    	String[] params = req.getPathInfo().split("/");
        String plant_id = params[1];

        if(plant_id == null || plant_id.split("\\s+").length > 1){
            resp.setStatus(400);
            PrintWriter write = resp.getWriter();
            write.write("Plant ID is invalid.");
            write.flush();
            write.close();
        } else {
	        if(!isAuthorised(req.getHeader("Authorization"))){
	            resp.sendError(400);
	        } else {
	            Connection con = (Connection)getServletContext().getAttribute("DBConnection");
	            PreparedStatement ps = null;
	            ResultSet rs = null;
	            try {
	                ps = con.prepareStatement("select organisation_id from plant_record where plant_id=?");
	                ps.setString(1, plant_id);
	                rs = ps.executeQuery();
	                if(rs != null && rs.next()){
	                    if(rs.getString("organisation_id").equals(organisation_id)){
	                        resp.setStatus(200);
	                    } else {
	                        resp.setStatus(400);
	                        PrintWriter write = resp.getWriter();
	                        write.write("Plant ID has already been registered by another organisation.");
	                        write.flush();
	                        write.close();
	                    }
	                }else{
	                    PrintWriter write = resp.getWriter();
	                    write.write("Not Registered");
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
