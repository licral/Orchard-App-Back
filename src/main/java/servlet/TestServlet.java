package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureException;

@WebServlet(
        name = "TestServlet",
        urlPatterns = {"/test"}
    )
public class TestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	PrintWriter write = resp.getWriter();
    	
    	String auth = req.getHeader("Authorization");
    	
    	try{
    		Claims claims = Jwts.parser().setSigningKey("secret".getBytes("UTF-8")).parseClaimsJws(auth).getBody();

    		String username = claims.get("username").toString();
    		String password = claims.get("password").toString();
    		
    		if(username == null || password == null){
        		resp.sendError(400);
        	} else {
        		if(username.equals("Bonnie") && password.equals("12345")){
        			write.write("Got to test route!");
        	    	write.flush();
        	    	write.close();
        		} else {
        			resp.sendError(400);
        		}
        	}
    	} catch (SignatureException e) {
			resp.sendError(400);
    	}
    }

}