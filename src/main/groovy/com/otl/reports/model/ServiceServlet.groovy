package com.otl.reports.model

import javax.servlet.http.HttpServlet
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class ServiceServlet extends HttpServlet {
 
    private static final long serialVersionUID = 1L;
 
   
	protected void doGet(HttpServletRequest request,
		HttpServletResponse response)
throws ServletException, IOException
{
// Set response content type
response.setContentType("text/html");

// Actual logic goes here.
PrintWriter out = response.getWriter();
out.println("<h1>" + message + "</h1>");
}


 
    /***************************************************
     * URL: /jsonservlet
     * doPost(): receives JSON data, parse it, map it and send back as JSON
     ****************************************************/
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
 
        // 1. get received JSON data from request
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String json = "";
        if(br != null){
            json = br.readLine();
        }
 
 
        // 4. Set response type to JSON
        response.setContentType("application/json");            
 
        // 5. Add article to List<Article>
 //       articles.add(article);
 
        // 6. Send List<Article> as JSON to client
   //     mapper.writeValue(response.getOutputStream(), articles);
    }
}
