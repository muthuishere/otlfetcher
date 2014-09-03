package com.otl.reports.model

import com.otl.reports.controller.Responder
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
 
	Responder responder=null
	
   public ServiceServlet(){
	    responder=new Responder()
		
	   }
	protected void doGet(HttpServletRequest request,
		HttpServletResponse response)
throws ServletException, IOException
{
// Set response content type
response.setContentType("text/xml");

String path=request.getPathInfo()
String[] servicedata=path.split("/")

println servicedata.length

for ( e in servicedata ) {
	println e
}


String actionname=servicedata.getAt(1)


def result = ""

switch ( actionname ) {
	case "updateuser":
		result = responder.updateuser(request.getParameterMap(),request.getLocalAddr())
		
		break;

	default:
		result = "<reply><status code='1' error='true' description='Invalid Service specified'/></reply>"
}

// Actual logic goes here.
PrintWriter out = response.getWriter();
out.println(result);
}



    /***************************************************
     * URL: /jsonservlet
     * doPost(): receives JSON data, parse it, map it and send back as JSON
     ****************************************************/
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
 
       doGet( request, response);
    }
}