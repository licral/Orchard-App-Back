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
    	
//    	String subject = Jwts.parser().setSigningKey(key)
    	
    	write.write("Got header: " + auth);
    	
    	write.flush();
    	write.close();
    }

}