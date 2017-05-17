package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.jsonwebtoken.Jwts;

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
    		String subject = Jwts.parser().setSigningKey("secret".getBytes("UTF-8")).parseClaimsJws(auth).getBody().getSubject();
    		write.write("Got header: " + subject);
    	} catch (SignatureException e) {
    		write.write("Error: " + e)
    	}
    	
    	
    	
    	write.flush();
    	write.close();
    }

}