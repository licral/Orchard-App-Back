package servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.PrintWriter;

import java.lang.NumberFormatException;

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
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureException;

@WebServlet(
        name = "GetServlet",
        urlPatterns = {"/get/*"}
    )
public class GetServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

    	String[] params = req.getPathInfo().split("/");
    	String service = params[1];

    	if(service == null || !isAuthorised(req.getHeader("Authorization"))){
    		resp.sendError(400);
    	} else {
    		if(service.equals("species")){
    			getSpecies(resp);
	    	} else if(service.equals("variety")){
	    		try{
	    			int species_id = Integer.parseInt(params[2]);
	    			getVariety(resp, species_id);
	    		} catch (NumberFormatException e){
	    			System.out.println(e.getMessage());
	    			resp.sendError(400);
	    		}
	    	} else if(service.equals("activities")){
	    		getActivities(resp);
	    	} else if(service.equals("plant")){
	    		if(params[2] == null){
                    resp.sendError(400);
                } else {
                    getPlantInfo(resp, params[2]);
                }
	    	} else if(service.equals("activity")){
	    		try{
	    			int activity_id = Integer.parseInt(params[2]);
	    			getActivityInfo(resp, activity_id);
	    		} catch (NumberFormatException e){
	    			System.out.println(e.getMessage());
	    			resp.sendError(400);
	    		}
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
        				return username.equals(rs.getString("organisation_id"));
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

    private void getSpecies(HttpServletResponse resp) throws ServletException, IOException {
    	Connection con = (Connection)getServletContext().getAttribute("DBConnection");
    	PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = con.prepareStatement("select * from species");
			rs = ps.executeQuery();
			String speciesArray = "{";
			if(rs != null && rs.next()){
				do{
					speciesArray += "\"" + rs.getString("species_id") + "\":\"" + rs.getString("species") + "\"";
					if(!rs.isLast()){
						speciesArray += ",";
					}

				} while(rs.next());
				speciesArray += "}";

				PrintWriter write = resp.getWriter();
				write.write(speciesArray);
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

    private void getVariety(HttpServletResponse resp, int species_id) throws ServletException, IOException {
    	Connection con = (Connection)getServletContext().getAttribute("DBConnection");
    	PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = con.prepareStatement("select * from variety where species_id=?");
			ps.setInt(1, species_id);
			rs = ps.executeQuery();
			String varietyArray = "{";
			if(rs != null && rs.next()){
				do{
					varietyArray += "\"" + rs.getString("variety_id") + "\":\"" + rs.getString("variety") + "\"";
					if(!rs.isLast()){
						varietyArray += ",";
					}

				} while(rs.next());
				varietyArray += "}";

				PrintWriter write = resp.getWriter();
				write.write(varietyArray);
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

    private void getActivities(HttpServletResponse resp) throws ServletException, IOException {
    	Connection con = (Connection)getServletContext().getAttribute("DBConnection");
    	PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = con.prepareStatement("select * from activity_types");
			rs = ps.executeQuery();
			String activityArray = "{";
			if(rs != null && rs.next()){
				do{
					activityArray += "\"" + rs.getString("type_id") + "\":\"" + rs.getString("activity_type") + "\"";
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

    private void getPlantInfo(HttpServletResponse resp, String plant_id) throws ServletException, IOException {
    	Connection con = (Connection)getServletContext().getAttribute("DBConnection");
    	PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = con.prepareStatement("select d.plant_id as plant_id, d.date as date, d.notes as notes, d.visual_tag as visual_tag, d.longitude as longitude, d.latitude as latitude, d.variety as variety, c.species as species from species c inner join (select a.plant_id as plant_id, a.date as date, a.notes as notes, a.visual_tag as visual_tag, a.longitude as longitude, a.latitude as latitude, b.variety as variety, b.species_id as species_id from plant_record a inner join variety b on a.variety_id=b.variety_id where plant_id=?) d on c.species_id=d.species_id");
			ps.setString(1, plant_id);
			rs = ps.executeQuery();
			String plantInfo = "{";
			if(rs != null && rs.next()){
				plantInfo += "\"plant_id\":\"" + rs.getString("plant_id") + "\", \"date\":\"" + rs.getDate("date") + "\", \"notes\":\"" + rs.getString("notes") + "\", \"visual_tag\":\"" + rs.getString("visual_tag") + "\", \"longitude\":\"" + rs.getDouble("longitude") + "\", \"latitude\":\"" + rs.getDouble("latitude") + "\", \"variety\":\"" + rs.getString("variety") + "\", \"species\":\"" + rs.getString("species") + "\"";
				plantInfo += "}";

				PrintWriter write = resp.getWriter();
				write.write(plantInfo);
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

    private void getActivityInfo(HttpServletResponse resp, int activity_id) throws ServletException, IOException {
    	Connection con = (Connection)getServletContext().getAttribute("DBConnection");
    	PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = con.prepareStatement("select plant_id, date, time, notes, type_id from activities where activity_id=?");
			ps.setInt(1, activity_id);
			rs = ps.executeQuery();
			String activityInfo = "{";
			if(rs != null && rs.next()){
				activityInfo += "\"plant_id\":\"" + rs.getString("plant_id") + "\", \"date\":\"" + rs.getDate("date") + "\", \"time\":\"" + rs.getTime("time") + "\", \"notes\":\"" + rs.getString("notes") + "\"";

				int type_id = rs.getInt("type_id");
				if(type_id == 1){
					activityInfo += getFertiliserInfo(activity_id);
				} else if(type_id == 2){
					activityInfo += getChemicalInfo(activity_id);
				} else if(type_id == 3){
					activityInfo += "}";
				} else if(type_id == 4){
					activityInfo += getHarvestInfo(activity_id);
				} else if(type_id == 5){
					activityInfo += "}";
				} else {
					System.out.println("Invalid type");
					resp.sendError(400);
				}

				PrintWriter write = resp.getWriter();
				write.write(activityInfo);
    	    	write.flush();
    	    	write.close();
			}else{
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

    private String getFertiliserInfo(int activity_id) throws ServletException{
    	Connection con = (Connection)getServletContext().getAttribute("DBConnection");
    	PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = con.prepareStatement("select product, rate from activity_fertiliser where activity_id=?");
			ps.setInt(1, activity_id);
			rs = ps.executeQuery();
			if(rs != null && rs.next()){
				return ", \"product\":\"" + rs.getString("product") + "\", \"rate\":\"" + rs.getInt("rate") + "\"}";
			} else {
				return "}";
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

    private String getChemicalInfo(int activity_id) throws ServletException{
    	Connection con = (Connection)getServletContext().getAttribute("DBConnection");
    	PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = con.prepareStatement("select product, rate from activity_chemical where activity_id=?");
			ps.setInt(1, activity_id);
			rs = ps.executeQuery();
			if(rs != null && rs.next()){
				return ", \"product\":\"" + rs.getString("product") + "\", \"rate\":\"" + rs.getInt("rate") + "\"}";
			} else {
				return "}";
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

    private String getHarvestInfo(int activity_id) throws ServletException{
    	Connection con = (Connection)getServletContext().getAttribute("DBConnection");
    	PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = con.prepareStatement("select product, rate from activity_harvest where activity_id=?");
			ps.setInt(1, activity_id);
			rs = ps.executeQuery();
			if(rs != null && rs.next()){
				return ", \"product\":\"" + rs.getString("product") + "\", \"rate\":\"" + rs.getInt("rate") + "\"}";
			} else {
				return "}";
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
