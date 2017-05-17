package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import java.security.Key;

@WebServlet(
        name = "LoginServlet",
        urlPatterns = {"/login"}
    )
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	PrintWriter write = resp.getWriter();
    	
    	String compactJws = Jwts.builder()
    			.setSubject("Joe")
    			.signWith(SignatureAlgorithm.HS512, "secret".getBytes("UTF-8"))
    			.compact();
    	
    	try{
    		Jws<Claims> jws = Jwts.parser().setSigningKey("secret".getBytes("UTF-8")).parseClaimsJws(token);
    		System.out.println("Got header: " + jws);
    	} catch (MalformedJwtException e) {
    		System.out.println(e)
    	}
    	
    	write.write("{\"data\": \"hello world\", \"id_token\": \"" + compactJws + "\"}");
    	write.flush();
    	write.close();
    }

}