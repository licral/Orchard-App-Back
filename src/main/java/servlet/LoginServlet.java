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
    	
    	
//    	
//    	String compactJws = Jwts.builder()
//    			.setSubject("Joe")
//    			.signWith(SignatureAlgorithm.HS512, "secret".getBytes("UTF-8"))
//    			.compact();
    	
    	String username = req.getParameter("username");
    	String password = req.getParameter("password");
    	
    	resp.setStatus(200);
    	write.write("{\"data\": \"hello world\", \"id_token\": \"" + password + "\"}");
    	write.flush();
    	write.close();
//    	resp.sendError(400);
    	
    	
    	
    }

}