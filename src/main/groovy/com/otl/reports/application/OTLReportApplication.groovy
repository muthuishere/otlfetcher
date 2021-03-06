package com.otl.reports.application

import com.otl.reports.beans.UserInfo
import com.otl.reports.controller.FetchUserReport
import com.otl.reports.controller.JobScheduler
import com.otl.reports.controller.OTLServer

import groovy.json.JsonSlurper


import com.otl.reports.controller.Configurator;
import com.otl.reports.model.WebBrowser
import com.otl.scheduler.batch.BatchReportGenerator

class OTLReportApplication {

	
	
	
	static void browserAuthenticatetest(){
		
				WebBrowser webBrowser=new WebBrowser()
				
				def webconfig=[:]
				
				webconfig.proxy=Configurator.globalconfig.proxy
				webconfig.authsites=Configurator.globalconfig.authsites
				webconfig.otlcredentials=[user: 'mnavaneethakrishnan@corpuk.net', pwd: 'June#2015']
				
				webBrowser.init(webconfig)
				webBrowser.login("https://ebiz.three.com/OA_HTML/AppsLogin")
				
				//https://ebiz.three.com/OA_HTML/OA.jsp?OAFunc=OAHOMEPAGE
				
				//https://idmssop02.three.com/oam/CredCollectServlet/WNA?spnegotoken=string&OverrideRetryLimit=2&challenge_url=%2Foam%2FCredCollectServlet%2FWNAauthn_try_count=0&locale=en_US&resource_url=https%253A%252F%252Febiz.three.com%252Faccessgate%252Fdossologin
				//https://ebiz.three.com/OA_HTML/OA.jsp?OAFunc=OAHOMEPAGE
				//webBrowser.printAll()
			}
	
	
	static parseconfig(def configFileName){
		
		// Read the configuration file into a map called "global".
		// This map is shared with all other threads in order to provide
		// a centralised configuration store.
		
		
		
		
		try
		{
			Configurator.globalconfig = new JsonSlurper().parse(new FileReader(configFileName))
		
			Configurator.globalconfig.configuration_file = configFileName
		
			println "Configuration: ${Configurator.globalconfig}"
		}
		catch(Exception e)
		{
			println "Error: Unable to load configuration"
			e.printStackTrace()
			System.exit(1)
		}
		
	}
	
	static void waitformore(def server){
		
		def CLEANUP_REQUIRED = true
		Runtime.runtime.addShutdownHook {
		  println "Shutting down..."
		  if(null != server)
			  server.stopServer()
		  if( CLEANUP_REQUIRED ) {
		
		  }
		}
		(1..10).each {
		  sleep( 1000 )
		}
		CLEANUP_REQUIRED = false
		
	}
	static void  startAppServer(){
		
		OTLServer server=new OTLServer()
		server.init()
		server.startServer()
		BatchReportGenerator batchReporter = new BatchReportGenerator()

		JobScheduler jc = new JobScheduler()
		
		
		waitformore(server)
		
		
		
	}
	
	static main(args) {
	
		def configFileName
		
		
		if(args.size() != 1)
		{
			println "Usage: OTLReportApplication.groovy <configuration file>"
			System.exit(1)
		}
		
		System.setProperty( "sun.security.ssl.allowUnsafeRenegotiation", "true" );
		
		configFileName=args[0]
		
		parseconfig(configFileName)
		
		//leavecodetest()
		//integrate()
		
		//browsertest();
		browserAuthenticatetest()
		
		//startAppServer()
		
	}

}
