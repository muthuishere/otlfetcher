package com.otl.reports.controller

import java.util.concurrent.LinkedBlockingQueue

public class Configurator {

	//public static String dbname="C:\\muthu\\gitworkspace\\otlfetcher\\lat.db"
//	public static String leavecodes="'101','102'"
	public static def globalconfig=null
	public static boolean isUpdating=false
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
	
	public static def mailer_jobs_worker_lbq = new LinkedBlockingQueue()
	public static def adhoc_jobs_worker_lbq = new LinkedBlockingQueue()
	public static def hourly_jobs_worker_lbq = new LinkedBlockingQueue()
	public static def daily_jobs_worker_lbq = new LinkedBlockingQueue()
	public static def weekly_jobs_worker_lbq = new LinkedBlockingQueue()
	
	
	public static def worker_lbq = new LinkedBlockingQueue()
	public static def import_worker_lbq = new LinkedBlockingQueue()
	
}
