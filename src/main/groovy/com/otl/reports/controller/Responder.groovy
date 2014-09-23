package com.otl.reports.controller

import com.otl.reports.beans.TimeEntry;
import com.otl.reports.beans.UserInfo

import java.text.SimpleDateFormat
import java.util.ArrayList;
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

	def xml_string = { s ->

		s.replaceAll("[\\x00-\\x1f]", "").replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("'", "&apos;").replaceAll("\"", "&quot;").replaceAll("\\\\", "\\\\\\\\").replaceAll("\\\$", "\\\\\\\$")
	}



	public String getvalidusers(){

		StringBuffer response= new StringBuffer()

		response.append("<reply>")


		boolean valid=false

		try{
			def userTimeSummaries=dataManager.getAllUserStatus( )
			// Add information as xml


			userTimeSummaries.each{val->

				if(val.userLocked == false){
					response.append("<user>")
					valid=true
					response.append("\n<name>${xml_string(val.user)}</name>")





					response.append("\n</user>")
				}

			}
			if(!valid){

				throw new Exception("No Vaid users identified")
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


	public String getvalidusergroups(){

		StringBuffer response= new StringBuffer()

		response.append("<reply>")


		boolean valid=false

		try{
			def userTimeSummaries=dataManager.getAllUserStatus( )
			// Add information as xml

			def teamname=""
			userTimeSummaries.each{val->

				if(val.userLocked == false){

					StringBuffer userresponse= new StringBuffer()
					userresponse.append("<user>")
					valid=true

					userresponse.append("\n<name>${xml_string(val.user)}</name>")





					userresponse.append("\n</user>")

					if(teamname.equals(val.team) == false){
						response.append("<team name='${xml_string(val.team)}'>")
						response.append(userresponse.toString());
						response.append("</team>")
						teamname=val.team;
					}
				}

			}
			if(!valid){

				throw new Exception("No Vaid users identified")
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
				response.append("\n<team>${val.team}</team>")




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
			"team":request.getParameter("team"),
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


			def users=[]


			if(null != params.user && params.user != "")
				users.push(params.user)
			else if(null != params.team && params.team != "") {

				ArrayList<UserInfo> userlist=dataManager.getUserEntries(params.team)
				userlist.each{curuserInfo->

					users.push(curuserInfo.user);
				}

			}else{
				users.push("")
			}
			ArrayList<TimeEntry> timesheetdetails=new ArrayList<TimeEntry>();


			users.each{curuser->
				println("Fetching for $curuser for $from to $to")
				def res=dataManager.getTimesheetEntries( curuser, from,to)
				if(null != res)
					timesheetdetails.addAll(res)
			}


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

			response.append("<status code='0' error='false' description='Successfully retrieved detail  information'/>")
		}catch(Exception e){

			response= new StringBuffer()

			response.append("<reply>")

			response.append("<status code='1' error='true' description='${xml_string(e.getMessage())}'/>")
		}


		response.append("</reply>")

		return response;


	}


	public String executesql(HttpServletRequest request){
		def params=[
			"db":request.getParameter("db"),
			"sql":request.getParameter("sql")
		]

		StringBuffer response= new StringBuffer()

		response.append("<reply>")
		try{
			def result=	dataManager.executeSQL(params.db,params.sql)
			if(result)
				response.append("<status code='0' error='false' description='Executed Successfully'/>")
			else
				response.append("<status code='1' error='true' description='Unable to execute sql'/>")

		}catch(Exception e){


			e.printStackTrace();
			response.append("<status code='1' error='true' description='${xml_string(e.getMessage())}'/>")
		}


		response.append("</reply>")

		return response;
	}
	public String updatefetchDb(HttpServletRequest request){


		def params=[
			"users":request.getParameter("users"),
			"fromdate":request.getParameter("fromdate"),
			"todate":request.getParameter("todate"),
			"team":request.getParameter("team"),
		]
		StringBuffer response= new StringBuffer()

		response.append("<reply>")

		if(null == params.fromdate || null == params.todate ){

			response.append("<status code='1' error='true' description='Invalid user inputs'/>")

		}else if(Configurator.isUpdating ){

			response.append("<status code='1' error='true' description='Already update in progress'/>")

		}else{

			try{


				Date from = new SimpleDateFormat("yyyy-MM-dd").parse(params.fromdate);
				Date to = new SimpleDateFormat("yyyy-MM-dd").parse(params.todate);

				String userlist=""
				if(null != params.users && params.users != "") {

					userlist=params.users;
				}
				else if(null != params.team && params.team != "") {

					ArrayList<UserInfo> curuserlist=dataManager.getUserEntries(params.team)
					int i=0;
					curuserlist.each{curuserInfo->
						if(i != 0)
							userlist= "," + userlist

						userlist=userlist+ curuserInfo.user
						i++
					}

				}



				Thread.start {

					new DbUpdater().start(userlist,from ,to)


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
			"team":request.getParameter("team"),
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


			def users=[]


			if(null != params.user && params.user != "")
				users.push(params.user)
			else if(null != params.team && params.team != "") {

				ArrayList<UserInfo> userlist=dataManager.getUserEntries(params.team)
				userlist.each{curuserInfo->

					users.push(curuserInfo.user);
				}

			}else{
				users.push("")
			}
			ArrayList hashmaplist=new ArrayList();


			users.each{curuser->
				println("Fetching summary for $curuser for $from to $to")

				def res=dataManager.getTimesheetEntriesSummary( curuser, from,to)
				if(null != res)
					hashmaplist.add(res)
			}




			// Add information as xml

			//println(summarylist.dump())
			if(hashmaplist.size() >0 ){
				hashmaplist.each {summarylist ->

					summarylist.each{key,val->


						response.append("<user>")

						response.append("\n<name>${xml_string(val.user)}</name>")
						response.append("\n<workhours>${val.workhours}</workhours>")
						response.append("\n<leavehours>${val.leavehours}</leavehours>")
						response.append("\n<workdays>${val.workdays}</workdays>")
						response.append("\n<leavedays>${val.leavedays}</leavedays>")
						response.append("\n<defaulter>${val.defaulter}</defaulter>")


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

				}
				response.append("<status code='0' error='false' description='Successfully updated user information'/>")
			}else{
				throw new Exception("No Timesheet Entries found")
			}



		}catch(Exception e){

			e.printStackTrace();
			response= new StringBuffer()

			response.append("<reply>")


			response.append("<status code='1' error='true' description='${xml_string(e?.getMessage())}'/>")


		}


		response.append("</reply>")

		return response;


	}

	public String getAdmins(){
		String admins=""
		int i=0
		for(def bfr:Configurator.globalconfig.admins){



			if(i > 0)
				admins=admins +","



			admins=admins +"$bfr"

			i++
		}
		return admins

	}

	def getIP(HttpServletRequest request){

		if(request.getRemoteAddr()){

			return request.getRemoteAddr()
		}else{
			return request.getHeader("X-Forwarded-For")
		}

	}
	public String getuserStatus(HttpServletRequest request){



		String ip=getIP(request)

		StringBuffer response= new StringBuffer()

		response.append("<reply>")

		println(ip)
		println(getAdmins())

		if( getAdmins().contains(ip))
			response.append("<status code='0' admin='true' description='Admin User'/>")
		else
			response.append("<status code='0' admin='false' description='Regular User'/>")

		response.append("</reply>")

		return response.toString();
	}


	public boolean isAdmin(HttpServletRequest request){

		String ip=getIP(request)
		return getAdmins().contains(ip)

	}


	public String deleteuser(HttpServletRequest request){


		def params=[
			"user":request.getParameter("user")
		]

		StringBuffer response= new StringBuffer()

		response.append("<reply>")

		if(null == params.user  ){

			response.append("<status code='1' error='true' description='Invalid user inputs'/>")

		}else{

			try{
				if(dataManager.deleteUser(params.user.toString()))
					response.append("<status code='0' error='false' description='Successfully updated user information'/>")
				else
					response.append("<status code='0' error='true' description='Invalid Username ,Could not delete'/>")

			}catch(Exception e){
				response= new StringBuffer()

				response.append("<reply>")


				response.append("<status code='1' error='true' description='${xml_string(e.getMessage())}'/>")
			}

		}
		response.append("</reply>")

		return response;
	}

	public String updateuser(HttpServletRequest request){

		String team=(null ==request.getParameter("team") )?"":request.getParameter("team");

		def params=[
			"user":request.getParameter("user"),
			"pwd":request.getParameter("pwd"),
			"team":team,
		]
		String ip=getIP(request)
		StringBuffer response= new StringBuffer()

		response.append("<reply>")

		if(null == params.user || null == params.pwd ){

			response.append("<status code='1' error='true' description='Invalid user inputs'/>")

		}else{

			try{
				dataManager.addUserEntries(new UserInfo(

						user: params.user,
						password:params.pwd,
						ip: ip,
						team:params.team
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
