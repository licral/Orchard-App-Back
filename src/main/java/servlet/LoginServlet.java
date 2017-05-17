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
    	
    	String username = req.getParameter("username");
    	String password = req.getParameter("password");
    	
    	if(username == null || password == null){
    		resp.sendError(400)
    	} else {
    		if(username.equals("Bonnie") && password.equals("12345")){
    	    	String token = Jwts.builder()
    	    			.put("username", username)
    	    			.put("password", password)
    	    			.signWith(SignatureAlgorithm.HS512, "secret".getBytes("UTF-8"))
    	    			.compact();

    	    	resp.setStatus(200);
    	    	write.write("{\"data\": \"hello world\", \"id_token\": \"" + token + "\"}");
    	    	write.flush();
    	    	write.close();
    		} else {
    			resp.sendError(400);
    		}
    	}
    	
    	
    	
    }

}