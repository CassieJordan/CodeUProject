// To save as "<TOMCAT_HOME>\webapps\hello\WEB-INF\classes\QueryServlet.java".
import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;


import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Iterator;
 
public class QueryServlet extends HttpServlet {  // JDK 6 and above only
 
   // The doGet() runs once per HTTP GET request to this servlet.
   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response)
               throws ServletException, IOException {
      // Set the MIME type for the response message
      List<String> list = WikiSearch.doSearch(request.getParameter("term1"), (String)request.getParameter("searchType"), request.getParameter("term2"));
      response.setContentType("text/html");
      // Get a output writer to write the response message into the network socket
      response.setIntHeader("Refresh", 5);
      PrintWriter out = response.getWriter();
 
          out.println("<html><head><title>Query Response</title><link rel='stylesheet' type='text/css' href='http://localhost:9999/project/style.css'></head><body>");

         out.println("<nav class='navbar navbar-default'>");
         out.println("<div class='container-fluid'>");
         
         out.println("<div class='navbar-header'>");
         out.println("<button type='button' class='navbar-toggle collapsed' data-toggle='collapse' data-target='#bs-example-navbar-collapse-1' aria-expanded='false'>");
         out.println("<span class='sr-only'>Toggle navigation</span>");
         out.println("<span class='icon-bar'></span>");
         out.println("<span class='icon-bar'></span>");
         out.println("<span class='icon-bar'></span>");
         out.println("</button>");
         out.println("<a class='navbar-brand' href='#'>CodeU Project</a>");
         out.println("</div>");

         out.println("<div class='collapse navbar-collapse' id='bs-example-navbar-collapse-1'>");
         out.println("<ul class='nav navbar-nav'>");
         out.println("<li><a href='/project/about.html'> About </a></li>");
       
        

         out.println("</ul>");
         out.println("</div><!-- /.navbar-collapse -->");
         out.println("</div><!-- /.container-fluid -->");
         out.println("</nav>");
          

          //out.println("<p> uuuuuuuu" + list + "</p>"); // Echo for debugging
         out.println("<div class='container'>");
         out.println("<p> Your Results </p>");
         for (int i = 0; i < list.size(); i++){
            out.println("<div class='row'>");
            out.println("<p><a href='" + list.get(i) + "'>" + list.get(i) + "</a></p>");
            out.println("</div>");
         }
          out.println("</div>");

         // Print an HTML page as the output of the query
         out.println("</body></html>");
 // Echo for debugging
         
     
   }
}


// // To save as "<TOMCAT_HOME>\webapps\hello\WEB-INF\classes\QueryServlet.java".
// import java.io.*;
// //import java.sql.*;
// import javax.servlet.*;
// import javax.servlet.http.*;
 
// public class QueryServlet extends HttpServlet {  // JDK 6 and above only
 
//    // The doGet() runs once per HTTP GET request to this servlet.
//    @Override
//    public void doGet(HttpServletRequest request, HttpServletResponse response)
//                throws ServletException, IOException {
//       // Set the MIME type for the response message
//       response.setContentType("text/html");
//       // Get a output writer to write the response message into the network socket
//       PrintWriter out = response.getWriter();

//       List<Entry<String, Integer>> list = WikiSearch.dosearch("java", request.getParameter("searchType"), "programming");


//       request.setAttribute("list", list); // This will be available as ${message}
//       request.getRequestDispatcher("../../query.jsp").forward(request, response);

//       // out.println("<html><head><title>Query Response</title></head><body>");

//       // out.println("<p> uuuuuuuu" + list + "</p>"); // Echo for debugging
//       //out.println("<p>You : " +  request.getParameterNames().nextElement() + "</p>");

//       // request.setAttribute("message", message); // This will be available as ${message}
//       //   request.getRequestDispatcher("/WEB-INF/hello.jsp").forward(request, response);

//       // out.println("</body></html>");

//    }
// }