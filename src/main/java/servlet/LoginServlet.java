package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
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
    	
    	Key key = MacProvider.generateKey();
    	String compactJws = Jwts.builder()
    			.setSubject("Joe")
    			.signWith(SignatureAlgorithm.HS512, key)
    			.compact();
    	
    	write.write("{\"data\": \"hello world\", \"id_token\": \"" + compactJws + "\"}");
    	write.flush();
    	write.close();
    }

}