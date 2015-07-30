package com.otl.reports.controller
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.ContextHandler
import org.eclipse.jetty.servlet.*

import com.otl.reports.model.PollerSocketServlet
import com.otl.reports.model.ServiceServlet
import groovy.servlet.*

class OTLServer {

	Server server=null
	

	
	public init(){
		
		
		
		
		 server = new Server(Configurator.globalconfig.server_port)
		
		// String webDir = this.class.getClassLoader().getResource("com/otl/reports/webapps").toExternalForm();
		 
		 
		 
		 
		 ServletContextHandler context = new ServletContextHandler();
		 
		// String webDir = this.class.getClassLoader().getResource(".").toExternalForm();
		 
		// ClassLoader loader = this.getClass().getClassLoader();
	//	 File indexLoc = new File(loader.getResource("."+File.separator).getFile());
		// String webDir = indexLoc.getParentFile().getParentFile().getParentFile().absolutePath + "\\resources\\main\\webapp";
		 
		 
		// println(webDir)
		 
		 context.setResourceBase(Configurator.globalconfig.webapp_path);
			 
		 server.setHandler(context);
	 
	 
		 ServiceServlet dataServlet = new ServiceServlet();
		 DefaultServlet staticServlet = new DefaultServlet();
	 
		 context.addServlet(new ServletHolder(dataServlet), "/services/*");
		 context.addServlet(new ServletHolder(staticServlet), "/*");
		
		 PollerSocketServlet webSockServlet = new PollerSocketServlet();
		  context.addServlet(new ServletHolder(webSockServlet), "/OtlHelperSocket/*");
			 
		   
		   
	}
	
	public startServer(){
		
		server.start()
	}
	
	public stopServer(){
		
		server.stop()
	}
}
