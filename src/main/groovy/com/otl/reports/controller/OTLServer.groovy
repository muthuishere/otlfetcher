package com.otl.reports.controller
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.ContextHandler
import org.eclipse.jetty.servlet.*
import com.otl.reports.model.ServiceHandler
import com.otl.reports.model.ServiceServlet
import groovy.servlet.*

class OTLServer {

	Server server=null
	public init(int port,String path){
		
		 server = new Server(port)
		
		 String webDir = this.class.getClassLoader().getResource("com/otl/reports/webapps").toExternalForm();
		 
		 
		 
		 
		 ServletContextHandler context = new ServletContextHandler();
		 context.setResourceBase("./webapp/");
		 server.setHandler(context);
	 
	 
		 ServiceServlet dataServlet = new ServiceServlet();
		 DefaultServlet staticServlet = new DefaultServlet();
	 
		 context.addServlet(new ServletHolder(dataServlet), "/services/*");
		 context.addServlet(new ServletHolder(staticServlet), "/*");
		//context.setWelcomeFiles(["index.html"].toArray())
		 
		ClassLoader loader = this.getClass().getClassLoader();
		File indexLoc = new File(loader.getResource(context.getResourceBase()).getFile());
		String htmlLoc = indexLoc.getParentFile().getAbsolutePath();
		
		println(htmlLoc)
		println("---------------------")
	//	 println()
		/* ContextHandler context = new ContextHandler();
		 
		   context.setContextPath("/services");
		 
		   context.setResourceBase(".");
		 
		   context.setClassLoader(Thread.currentThread().getContextClassLoader());
		 
		   server.setHandler(context);
		   context.setHandler(new ServiceHandler());
		   
		      /*
		   def handler = new ServletContextHandler(ServletContextHandler.SESSIONS)
		   handler.contextPath = '/'
		   handler.resourceBase = webDir
		 //  
		   
		   handler.welcomeFiles = ['index.html']
		   handler.addServlet(GroovyServlet, '/scripts/*')
		   def filesHolder = handler.addServlet(DefaultServlet, '/')
		   filesHolder.setInitParameter('resourceBase', webDir)

		   
		
		   def handlerService = new ServletContextHandler(ServletContextHandler.SESSIONS)
		   handler.contextPath = '/services/'
		   handler.resourceBase = '.'
		   handler.welcomeFiles = ['index.html']
		   handler.addServlet(GroovyServlet, '/scripts/*')
		   def filesHolder = handler.addServlet(DefaultServlet, '/')
		   filesHolder.setInitParameter('resourceBase', './public')

		   */
		   
		  // server.handler = handler
		   
		   
	}
	
	public startServer(){
		
		server.start()
	}
	
	public stopServer(){
		
		server.stop()
	}
}
