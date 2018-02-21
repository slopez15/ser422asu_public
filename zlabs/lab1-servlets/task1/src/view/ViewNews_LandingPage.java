//package edu.asupoly.ser422.dateservlet;
package view;
//modified by: Saul Lopez

import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.*;

@SuppressWarnings("serial")
public class ViewNews_LandingPage extends javax.servlet.http.HttpServlet
{
    public void doGet (HttpServletRequest req, HttpServletResponse res)
    					throws ServletException, IOException

	{
		res.setContentType("text/html");

		PrintWriter out = res.getWriter();

		out.println("<html>");
		out.println("<head><title>ViewNews Landing Page</title></head>");
    out.println("<body><H2>Welcome (user-type)</H2>");
    out.println("<body><a href=\"./login.html\">Login (user-type)</a>");
    // out.println("<body><H2>Current time is</H2>");
		// out.println( (new Date()).toString() );
		out.println("</body></html>");
	}
}
