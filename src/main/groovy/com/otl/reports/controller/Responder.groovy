package com.otl.reports.controller

import com.otl.reports.beans.UserInfo

import java.text.SimpleDateFormat
import java.util.Date;
import javax.servlet.http.HttpServletRequest

class Responder {

	DataManager dataManager=null
	
	public Responder(){
		dataManager=new DataManager()
		dataManager.init();
		
		println("Initialized")
	}
	/**
	 * Create private constructor
	 */
	
	def xml_string =
	{ s ->
	
		s.replaceAll("[\\x00-\\x1f]", "").replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("'", "&apos;").replaceAll("\"", "&quot;").replaceAll("\\\\", "\\\\\\\\").replaceAll("\\\$", "\\\\\\\$")
	}
	
	
	
	public String getAllusers(){
		
		StringBuffer response= new StringBuffer()
		
		response.append("<reply>")
		
		
		
		
		try{
		def userTimeSummaries=dataManager.getAllUserStatus( )
		// Add information as xml
		
		
		userTimeSummaries.each{val->
			
						response.append("<entry>")
						
						response.append("\n<name>${xml_string(val.user)}</name>")
						response.append("\n<userLocked>${val.userLocked}</userLocked>")
						
						
						
						
						response.append("\n</entry>")
				
					}
		
		response.append("<status code='0' error='false' description='Successfully retrieved detail  information'/>")
		}catch(Exception e){
		
		response= new StringBuffer()
		
		response.append("<reply>")
		
		response.append("<status code='1' error='true' description='${xml_string(e.getMessage())}'/>")
		}
	
	
		response.append("</reply>")
		
		return response;
		
		
	}
	
	
	public String fetchDbStatus(){
		
		StringBuffer response= new StringBuffer()
		
		response.append("<reply>")
		
		
		
		try{
		
		
			response.append("<updatestatus inprogress='${Configurator.isUpdating}' summary='${Configurator.fetchDbInfo.status}' description='${Configurator.fetchDbInfo.description}'  lastupdated='${Configurator.fetchDbInfo.lastupdated}'  />")
		
			response.append("<status code='0' error='false' description='Successfully Started Parsing'/>")
		
		}catch(Exception e){
		
		response= new StringBuffer()
		
		response.append("<reply>")
		
		response.append("<status code='1' error='true' description='${xml_string(e.getMessage())}'/>")
		}
	
		
		response.append("</reply>")
		
		return response;
		
		
	}
	
	public String fetchreportDetails(HttpServletRequest request){
		
		
	def params=[
			"user":request.getParameter("username_detail"),
			"fromdate":request.getParameter("fromdate"),
			"todate":request.getParameter("todate")
			]
		StringBuffer response= new StringBuffer()
		
		response.append("<reply>")
		
		
		
		try{
			
				Date from =null
			if(null != params.fromdate)
				from = new SimpleDateFormat("yyyy-MM-dd").parse(params.fromdate);
		
				Date to =null
				if(null != params.todate)
					to = new SimpleDateFormat("yyyy-MM-dd").parse(params.todate);
			
		
		def timesheetdetails=dataManager.getTimesheetEntries( params.user, from,to)
		// Add information as xml
		
		
		timesheetdetails.each{val->
			
			
			
					
						response.append("<entry>")
						
						response.append("\n<name>${xml_string(val.user)}</name>")
						response.append("\n<projectcode>${val.projectcode}</projectcode>")
						response.append("\n<projecttask>${xml_string(val.projecttask)}</projecttask>")
						response.append("\n<tasktype>${xml_string(val.tasktype)}</tasktype>")
						response.append("\n<hours>${val.hours}</hours>")
						response.append("\n<details>${val.details}</details>")
						response.append("\n<isLeave>${val.isLeave}</isLeave>")
						
						def entrydate=""
						if(val.entryDate){
							
							SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM dd yyyy");
							
							   // (3) create a new String using the date format we want
							   entrydate= formatter.format(val.entryDate);
						}
						response.append("\n<entryDate>${entrydate}</entryDate>")
						response.append("\n<fetchedDate>${val.fetchedDate}</fetchedDate>")
						
						
						
						response.append("\n</entry>")
				
					}
		
		response.append("<status code='0' error='false' description='Successfully retrieved detail r information'/>")
		}catch(Exception e){
		
		response= new StringBuffer()
		
		response.append("<reply>")
		
		response.append("<status code='1' error='true' description='${xml_string(e.getMessage())}'/>")
		}
	
	
		response.append("</reply>")
		
		return response;
		
		
	}
	
	public String updatefetchDb(HttpServletRequest request){
		
		
		def params=[
			"user":request.getParameter("user"),
			"fromdate":request.getParameter("fromdate"),
			"todate":request.getParameter("todate")
			]
		StringBuffer response= new StringBuffer()
		
		response.append("<reply>")
		
		if(null == params.fromdate || null == params.todate ){
			
			response.append("<status code='1' error='true' description='Invalid user inputs'/>")
			
		}else{
		
		try{
		
		
		Date from = new SimpleDateFormat("yyyy-MM-dd").parse(params.fromdate);
		Date to = new SimpleDateFormat("yyyy-MM-dd").parse(params.todate);
		
		
		
		
		Thread.start {
			
			new DbUpdater().start(from ,to)
			
			
		}
		
			response.append("<status code='0' error='false' description='Successfully Started Parsing'/>")
		
		}catch(Exception e){
		response= new StringBuffer()
		
		response.append("<reply>")
		
		e.printStackTrace();
		response.append("<status code='1' error='true' description='${xml_string(e.getMessage())}'/>")
		}
	
		}
		response.append("</reply>")
		
		return response;
		
		
	}
	public String fetchreportSummary(HttpServletRequest request){
		
		
		def params=[
			"user":request.getParameter("user"),
			"fromdate":request.getParameter("fromdate"),
			"todate":request.getParameter("todate")
			]
		StringBuffer response= new StringBuffer()
		
		response.append("<reply>")
		
		
		
		try{
			
				Date from =null
			if(null != params.fromdate)
				from = new SimpleDateFormat("yyyy-MM-dd").parse(params.fromdate);
		
				Date to =null
				if(null != params.todate)
					to = new SimpleDateFormat("yyyy-MM-dd").parse(params.todate);
			
					
		def summarylist=dataManager.getTimesheetEntriesSummary( params.user, from,to)
		// Add information as xml
		
		println(summarylist.dump())
		summarylist.each{key,val->
			
					
						response.append("<user>")
						
						response.append("\n<name>${xml_string(val.user)}</name>")
						response.append("\n<workhours>${val.workhours}</workhours>")
						response.append("\n<leavehours>${val.leavehours}</leavehours>")
						response.append("\n<workdays>${val.workdays}</workdays>")
						response.append("\n<leavedays>${val.leavedays}</leavedays>")
						
						def lastupdated=""
						if(val.lastupdated){
							
							SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM dd yyyy");
							
							   // (3) create a new String using the date format we want
							   lastupdated= formatter.format(val.lastupdated);
						}
						
						
						
						response.append("\n<lastupdated>${lastupdated}</lastupdated>")
						response.append("\n<userlocked>${val.userLocked}</userlocked>")
						
					
						
						
						response.append("\n</user>")
				
					}
		
		response.append("<status code='0' error='false' description='Successfully updated user information'/>")
		}catch(Exception e){
		
		e.printStackTrace();
		response= new StringBuffer()
		
		response.append("<reply>")
		
			
			response.append("<status code='1' error='true' description='${xml_string(e?.getMessage())}'/>")
		
		
		}
	
	
		response.append("</reply>")
		
		return response;
		
		
	}
	public String updateuser(HttpServletRequest request){
		
		
	def params=[
		"user":request.getParameter("user"),
		"pwd":request.getParameter("pwd")
		]
		String ip=request.getLocalAddr()
		StringBuffer response= new StringBuffer()
		
		response.append("<reply>")
		
		if(null == params.user || null == params.pwd ){
			
			response.append("<status code='1' error='true' description='Invalid user inputs'/>")
			
		}else{
		
		try{
		dataManager.addUserEntries(new UserInfo(
		
		user: params.user,
		password:params.pwd,
		ip: ip
		))
		
		response.append("<status code='0' error='false' description='Successfully updated user information'/>")
		}catch(Exception e){
		response= new StringBuffer()
		
		response.append("<reply>")
		
		
		response.append("<status code='1' error='true' description='${xml_string(e.getMessage())}'/>")
		}
	
		}
		response.append("</reply>")
		
		return response;
	}
	
	
}
