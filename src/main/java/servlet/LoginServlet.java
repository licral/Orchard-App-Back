package servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(
        name = "LoginServlet",
        urlPatterns = {"/login"}
    )
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	PrintWriter write = resp.getWriter();
    	write.write("{id_token: '12345', data: 'You got to login route'}");
    	write.flush();
    	write.close();

//        ServletOutputStream out = resp.getOutputStream();
//        out.write("{id_token: '12345', data: 'You got to login route'}".getBytes());
//        out.flush();
//        out.close();
    }

}