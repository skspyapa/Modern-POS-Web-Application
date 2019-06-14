package lk.ijse.dep.servlet;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@WebFilter("/*")
public class CorsFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        response.setHeader("Access-Control-Allow-Origin","http://localhost:63342");
        response.setHeader("Access-Control-Allow-Methods","OPTIONS,GET,POST,PUT,DELETE");
        response.setHeader("Access-Control-Allow-Headers","Content-Type");
        chain.doFilter(request,response);
    }


}
