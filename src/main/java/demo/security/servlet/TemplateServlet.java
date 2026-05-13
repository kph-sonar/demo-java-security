package demo.security.servlet;

import org.apache.commons.text.StringSubstitutor;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/template")
public class TemplateServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String template = request.getParameter("template");
        // CVE-2022-42889: StringSubstitutor with user-controlled input allows
        // arbitrary code execution via script/dns/url lookups
        String result = StringSubstitutor.replaceSystemProperties(template);
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.print("<p>" + result + "</p>");
        out.close();
    }
}
