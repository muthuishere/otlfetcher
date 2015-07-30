package com.otl.reports.controller

import java.util.concurrent.LinkedBlockingQueue

import com.otl.reports.model.PollerSocket;

public class Configurator {

	//public static String dbname="C:\\muthu\\gitworkspace\\otlfetcher\\lat.db"
//	public static String leavecodes="'101','102'"
	public static def globalconfig=null
	public static boolean isUpdating=false
	public static def logMsgSockets=[:];
	public static customadmins=[]
	
	
	public static def fetchDbInfo=[
		"status":"",
		"description":"",
		"lastupdated":""
		]
	
	public static void resetupdatestatus(){
		Configurator.fetchDbInfo=[
			"status":"",
			"description":"",
			"lastupdated":""
			]
		
	}
	
	public static void setupdatestatus(def status,def description,def lastupdated){
		Configurator.fetchDbInfo=[
			"status":status,
			"description":description,
			"lastupdated":lastupdated
			]
		
	}

	public static void log(def msg){
		
		println "${new Date()} [${Thread.currentThread().getName()}]: ${msg}"
	}
	
	public static def addSocket(PollerSocket logMsgSocket){
		
		logMsgSockets.put(logMsgSocket.sessionid, logMsgSocket)
		
	}
	
	public static def sendmsgtosocket(String sessionid,String data){
		
		if(null != logMsgSockets.getAt(sessionid) ){
			logMsgSockets.getAt(sessionid).sendMessagetoClient(data)
			
		}
		else
			println "Session closed not found session $sessionid"
		
	}
	
	public static def killsocket(String sessionid){
		
		if(null != logMsgSockets.getAt(sessionid)){
			logMsgSockets.getAt(sessionid).close()
			logMsgSockets.remove(sessionid)
			
			println "Session  killed ${sessionid} "
		}else
			println "no session to killed ${sessionid} "
		
	}
	
	public static def killsocket(PollerSocket pollerSocket){
		
		if(null != logMsgSockets.getAt(pollerSocket.sessionid)){
			logMsgSockets.getAt(pollerSocket.sessionid).close()
			logMsgSockets.remove(pollerSocket.sessionid)
			println "Session killed ${pollerSocket.sessionid} "
		}else
			println "No Session to kill ${pollerSocket.sessionid} "
		
	}
	
	
	public static def mailer_jobs_worker_lbq = new LinkedBlockingQueue()
	public static def adhoc_jobs_worker_lbq = new LinkedBlockingQueue()
	public static def hourly_jobs_worker_lbq = new LinkedBlockingQueue()
	public static def daily_jobs_worker_lbq = new LinkedBlockingQueue()
	public static def weekly_jobs_worker_lbq = new LinkedBlockingQueue()
	
	
	public static def worker_lbq = new LinkedBlockingQueue()
	public static def import_worker_lbq = new LinkedBlockingQueue()
	
}
