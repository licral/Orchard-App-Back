package servlet;

import java.io.IOException;
import java.io.PrintWriter;
//import javax.json.Json;
//import javax.json.JsonObject;

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
//    	JsonObject o = Json.createObjectBuilder()
//    			.add("id_token", 124243)
//    			.add("data", "You got to the login route! :D")
//    			.build();
//    	write.print(o.toString());
    	write.write('{"id_token" : 12345, "data": "Hello world"}');
    	write.flush();
    	write.close();
    }

}