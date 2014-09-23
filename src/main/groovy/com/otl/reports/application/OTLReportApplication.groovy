package com.otl.reports.application

import com.otl.reports.beans.UserInfo
import com.otl.reports.controller.FetchUserReport
import com.otl.reports.controller.OTLServer

import groovy.json.JsonSlurper


import com.otl.reports.controller.Configurator;
import com.otl.reports.model.WebBrowser

class OTLReportApplication {

	
	
	
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
		
		
		waitformore(server)
		
		
		
	}
	
	static main(args) {
	
		def configFileName
		
		
		if(args.size() != 1)
		{
			println "Usage: OTLReportApplication.groovy <configuration file>"
			System.exit(1)
		}
		
		
		configFileName=args[0]
		
		parseconfig(configFileName)
		
		//leavecodetest()
		//integrate()
		
		//browsertest();
		startAppServer()
	}

}
