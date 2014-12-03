package com.otl.reports.controller

import com.otl.reports.beans.TimeEntry;
import com.otl.reports.beans.TimesheetStatusReport;
import com.otl.reports.beans.UserInfo
import com.otl.reports.helpers.Log

import java.text.SimpleDateFormat
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.UploadContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest

class Responder {

	DataManager dataManager=null

	public Responder(){
		dataManager=new DataManager()
		dataManager.init();

		Log.info("Initialized")
	}
	/**
	 * Create private constructor
	 */

	def xml_string = { s ->


		if(s instanceof String)
			s?.replaceAll("[\\x00-\\x1f]", "").replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("'", "&apos;").replaceAll("\"", "&quot;").replaceAll("\\\\", "\\\\\\\\").replaceAll("\\\$", "\\\\\\\$")
		else
			return s
	}







	public String getAllProjects(){

		StringBuffer response= new StringBuffer()

		response.append("<reply>")


		boolean valid=false

		try{
			def userTimeSummaries=dataManager.getAllProjects();
			// Add information as xml

			//	ArrayList<TimeEntry>
			userTimeSummaries.each{val->


				response.append("\n<project>")
				valid=true
				response.append("\n\t<code>${val.projectcode}</code>")

				def projname=""
				def projectid=""
				if(null != val.projectInfo){

					projname=xml_string(val.projectInfo.name)
					projectid=xml_string(val.projectInfo.projectid)
				}
				response.append("\n\t<name>${projname}</name>")
				response.append("\n\t<projectid>${projectid}</projectid>")



				response.append("\n</project>")


			}
			if(!valid){

				throw new Exception("No Vaid Projects identified")
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

			Log.debug(userTimeSummaries.dump())
			def teamname=""
			def lastteamname=""
			StringBuffer userresponse= new StringBuffer()
			def teamlist=[:]


			userTimeSummaries.each{val->

				if(val.userLocked == false){

					valid=true
					userresponse= new StringBuffer()
					userresponse.append("<user>")
					userresponse.append("\n<name>${xml_string(val.user)}</name>")
					userresponse.append("\n</user>")

					def curuser=userresponse.toString();
					if(teamlist.containsKey(val.team))
						teamlist.put(val.team, teamlist.get(val.team) + curuser);
					else
						teamlist.put(val.team, curuser)




				}

			}
			teamlist.each{key,val->

				response.append("<team name='${xml_string(key)}'>")
				response.append(val);
				response.append("</team>")
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
				from =getParsedDate(params.fromdate);

			Date to =null
			if(null != params.todate)
				to = getParsedDate(params.todate);


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
				//println("Fetching for $curuser for $from to $to")
				def res=dataManager.getTimesheetEntries( curuser, from,to)
				if(null != res)
					timesheetdetails.addAll(res)
			}


			// Add information as xml


			timesheetdetails.each{val->




				response.append("<entry>")

				response.append("\n<name>${xml_string(val.user)}</name>")
				response.append("\n<projectcode>${val.projectcode}</projectcode>")

				def projectname=""
				def projectid=""
				if(null != val.projectInfo){
					projectname=val.projectInfo.name
					projectid=val.projectInfo.projectid
				}
				response.append("\n<projectname>${xml_string(projectname)}</projectname>")
				response.append("\n<projectid>${xml_string(projectid)}</projectid>")

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

	public def exportdb(HttpServletRequest request){


		String dbname = request.getParameter("dbtype")
		Log.debug( "Entering into exportdb function: " + dbname)
		String fileName;
		StringBuffer response= new StringBuffer()

		if(dbname != null && dbname != "")
		{
			if(dbname == 'userdb') {fileName = Configurator.globalconfig.userdb}
			if(dbname == 'fetchdb') {fileName = Configurator.globalconfig.fetcherdb}
			Log.debug( "Value of the file name to be exported is " + fileName)

			return (new FileInputStream(fileName)).getBytes()
		}
		else
		{
			response.append("<reply>")
			response.append("<status code='1' error='true' description='Invalid Database Selected'/>")
			response.append("</reply>")
			return response
		}



	}

	
	
	public String updateImportedDb(def importdb_location, def override){


		
		StringBuffer response= new StringBuffer()

		response.append("<reply>")

		if(Configurator.isUpdating ){

			response.append("<status code='1' error='true' description='Already import in progress'/>")

		}else{

			try{


				



				Thread.start {

					
					//Send information to thread
					new DbImporter().start(importdb_location,override)


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


	public String importdb(HttpServletRequest request){
		/*
		 String dbname = request.getParameter("dbtype")
		 println (dbname)
		 def file1 = request.getParameter("myfile")
		 println file1
		 println file1.getBytes()
		 */
		//SQL imported_db
		StringBuffer response= new StringBuffer()

		response.append("<reply>")
		def importdb_location
		def UPLOAD_DIRECTORY = "C:/uk"
		UPLOAD_DIRECTORY = Configurator.globalconfig.tmpupload_path

		Log.debug("Value of the upload directory : " + UPLOAD_DIRECTORY)
		String override = null

		if(ServletFileUpload.isMultipartContent(request)){
			try {
				List<FileItem> multiparts = new ServletFileUpload(
						new DiskFileItemFactory()).parseRequest(request);
				int i = 0

				for(FileItem item : multiparts){
					i++
					if(!item.isFormField()){
						String name = new File(item.getName()).getName();
						Log.debug( "Name of the file being uploaded is : " + name)
						importdb_location = new File(UPLOAD_DIRECTORY + File.separator + name)
						item.write(importdb_location);
					}
					else if(item.isFormField()){
						if(item.getFieldName().equals("override")){
							override = item.inputStream.getText()
							Log.debug("Value of the override parameter selected is :" + override)

						}
					}
				}


				Log.debug( "Size of the request parameters is : " + i)
				//File uploaded successfully
				request.setAttribute("message", "File Uploaded Successfully");
				return updateImportedDb(importdb_location, override)
				
			} catch (Exception ex) {
				request.setAttribute("message", "File Upload Failed due to " + ex);
				Log.error( "Exception while parsing the parameters : " + ex)
				response.append("<status code='1' error='true' description='File Upload failed. Retry again Please'/>")

			}
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


				Date from =getParsedDate(params.fromdate);
				Date to = getParsedDate(params.todate);

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



	public String generateReport(HttpServletRequest request,String reportname){

		StringBuffer response= new StringBuffer()

		response.append("<reply>")
		response.append("<status code='1' error='true' description='Invalid request'/>")
		response.append("</reply>")

		def result=response.toString();

		switch ( reportname ) {



			case "projectemployeereport":
				result = generateProjectEmployeeReport(request)

				break;
			case "projecthoursreport":
				result = generateProjectHoursReport(request)

				break;



			case "employeeprojectreport":
				result = generateEmployeeProjectReport(request)

				break;

			case "weeklystatusreport":
				result = generateWeeklyStatusReport(request)

				break;

			case "projectmonthlyreport":
				result = generateProjectMonthlyReport(request)

				break;
		}

		return result;
	}


	public Date getParsedDate(def str){

		Date d=null
		try{
			d = new SimpleDateFormat("yyyy-MM-dd").parse(str);
		}catch(Exception e){
			Log.error(e.toString())
		}
		return d;

	}
	public String generateEmployeeProjectReport(HttpServletRequest request){


		def params=[
			"users":request.getParameter("users"),
			"fromdate":request.getParameter("fromdate"),
			"todate":request.getParameter("todate")
		]
		StringBuffer response= new StringBuffer()

		response.append("<reply>")



		try{

			Date from =null
			if(null != params.fromdate)
				from =getParsedDate(params.fromdate);

			Date to =null
			if(null != params.todate)
				to = getParsedDate(params.todate);


			def users=[]


			if(null != params.users && params.users != ""){

				for(String user:params.users.split(",")){
					users.push(user)
				}

			}else{
				users.push("")
			}
			ArrayList hashmaplist=new ArrayList();


			users.each{curuser->
				//println("Fetching generateEmployeeProjectReport  for $curuser for $from to $to")
				//ArrayList<TimeEntry> getTimesheetEntries
				def res=dataManager.getTimesheetEntries( curuser, from,to)
				if(null != res)
					hashmaplist.add(res)
			}


			ArrayList<UserInfo>  userEntryList=dataManager.getUserEntries( )
			def userteams=[:]
			userEntryList.each{userInfo ->

				userteams.put(userInfo.user, userInfo.team)
			}

			// Add information as xml

			//println(summarylist.dump())
			if(hashmaplist.size() >0 ){
				hashmaplist.each {summarylist ->

					summarylist.each{val->


						def team="unknown"

						if(userteams.containsKey(val.user))
							team=userteams.get(val.user)

						response.append("<user>")

						response.append("\n<name>${xml_string(val.user)}</name>")
						response.append("\n<code>${val.projectcode}</code>")

						def projectname=""
						def projectid=""
						if(null != val.projectInfo){
							projectname=val.projectInfo.name
							projectid=val.projectInfo.projectid
						}
						response.append("\n<projectname>${xml_string(projectname)}</projectname>")
						response.append("\n<projectid>${xml_string(projectid)}</projectid>")



						response.append("\n<task>${xml_string(val.projecttask)}</task>")
						response.append("\n<type>${xml_string(val.tasktype)}</type>")
						response.append("\n<hours>${val.hours}</hours>")
						response.append("\n<details>${val.details}</details>")
						response.append("\n<team>${team}</team>")

						def entrydate=""
						if(val.entryDate){

							SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM dd yyyy");

							// (3) create a new String using the date format we want
							entrydate= formatter.format(val.entryDate);
						}
						response.append("\n<date>${entrydate}</date>")
						response.append("\n<fetchedDate>${val.fetchedDate}</fetchedDate>")



						response.append("\n</user>")
						/*
						 response.append("<user>")
						 response.append("\n<name>${xml_string(val.)}</name>")
						 response.append("\n<user>${xml_string(val.user)}</user>")
						 response.append("\n<total>${val.totalhrs}</total>")
						 response.append("\n</project>")
						 */

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



	public String generateProjectHoursReport(HttpServletRequest request){


		def params=[
			"projects":request.getParameter("projects"),
			"fromdate":request.getParameter("fromdate"),
			"todate":request.getParameter("todate")
		]
		StringBuffer response= new StringBuffer()

		response.append("<reply>")



		try{

			Date from =null
			if(null != params.fromdate)
				from =getParsedDate(params.fromdate);

			Date to =null
			if(null != params.todate)
				to = getParsedDate(params.todate);


			def projects=[]


			if(null != params.projects && params.projects != ""){

				for(String project:params.projects.split(",")){
					projects.push(project)
				}

			}else{
				projects.push("")
			}
			ArrayList hashmaplist=new ArrayList();


			projects.each{curuser->


				def res=dataManager.getProjectHoursReport( curuser, from,to)
				if(null != res)
					hashmaplist.add(res)
			}




			// Add information as xml

			//println(summarylist.dump())
			if(hashmaplist.size() >0 ){
				hashmaplist.each {summarylist ->

					summarylist.each{val->


						response.append("<project>")

						response.append("\n<name>${val.projectcode}</name>")

						def projectname=""
						def projectid=""
						if(null != val.projectInfo){
							projectname=val.projectInfo.name
							projectid=val.projectInfo.projectid
						}
						response.append("\n<projectname>${xml_string(projectname)}</projectname>")
						response.append("\n<projectid>${xml_string(projectid)}</projectid>")



						response.append("\n<user>${xml_string(val.user)}</user>")
						response.append("\n<team>${val.team}</team>")

						def str="0"

						if(val.totalhrs >0)
							str= val.totalhrs.intValue() +""

						response.append("\n<total>${str}</total>")


						response.append("\n</project>")

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





	public String generateWeeklyStatusReport(HttpServletRequest request){


		def params=[
			"users":request.getParameter("users"),
			"fromdate":request.getParameter("fromdate"),
			"todate":request.getParameter("todate")
		]
		StringBuffer response= new StringBuffer()

		response.append("<reply>")



		try{

			Date from =null
			if(null != params.fromdate)
				from =getParsedDate(params.fromdate);

			Date to =null
			if(null != params.todate)
				to = getParsedDate(params.todate);


			def users=[]



			if(null != params.users && params.users != ""){

				for(String curuser:params.users.split(",")){
					users.push(curuser)
				}

			}else{
				users.push("")
			}
			ArrayList hashmaplist=new ArrayList();

			ArrayList<TimesheetStatusReport>  summarylist=dataManager.getWeeklystatus( users, from,to)




			// Add information as xml

			//println(summarylist.dump())
			if(null != summarylist && summarylist.size() > 0 ){

				summarylist.each{val->



					response.append("<timesheetstatus>")


					response.append("\n<user>${xml_string(val.user)}</user>")
					response.append("\n<team>${val.team}</team>")


					response.append("\n<total>${val.totalhrs}</total>")
					response.append("\n<status>${val.status}</status>")



					def projdate=""
					if(val.startdate){

						SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM dd yyyy");

						// (3) create a new String using the date format we want
						projdate= formatter.format(val.startdate);
					}



					response.append("\n<startdate>${projdate}</startdate>")


					projdate=""
					if(val.enddate){

						SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM dd yyyy");

						// (3) create a new String using the date format we want
						projdate= formatter.format(val.enddate);
					}

					response.append("\n<enddate>${projdate}</enddate>")

					response.append("\n</timesheetstatus>")



				}
				response.append("<status code='0' error='false' description='retrived weekly status'/>")
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



	public String generateProjectMonthlyReport(HttpServletRequest request)
	{
		//Defining the parameters & retriving from the http request
		def params=[
			"projects":request.getParameter("projects"),
			"users":request.getParameter("users"),
			"fromdate":request.getParameter("fromdate"),
			"todate":request.getParameter("todate")]
		Log.debug( "Prams retrieved from request :" + params)

		StringBuffer response = new StringBuffer()

		response.append("<reply>")

		try{
			Date from = null
			if(null != params.fromdate)
				from = getParsedDate(params.fromdate);

			Date to = null
			if(null != params.todate)
				to = getParsedDate(params.todate);

			def users = []
			def projects = []

			if(null != params.projects && params.projects != "")
			{
				for(String project:params.projects.split(","))
				{
					projects.push(project)
				}

			}
			else
			{
				projects.push("")
			}

			if(null != params.users && params.users !=  "")
			{
				for(String user :params.users.split(","))
				{
					users.push(user)
				}
			}
			else
			{
				users.push("")
			}
			ArrayList hashmaplist
			def res
			projects.each{project ->
				hashmaplist=new ArrayList()
				res = null
				def nextFromDate
				def currStartDate
				def currEndDate
				def startDate = from
				def endDate = to
				def projectname
				Calendar c1 = GregorianCalendar.getInstance();
				c1.setTime(startDate);
				int w1 = c1.get(Calendar.DAY_OF_MONTH);
				//Testing the new logic, based on number of days in a month.
				String month
				nextFromDate = startDate
				def monthCounter = 0
				response.append("<project>")
				response.append("\n<projectcode>${project}</projectcode>")
				println "Value of the project code : " + project
				//println "Value of the projectname : " + projectname
				projectname = dataManager.getProjectName(project)
				println "Project Name in the responder : " + projectname
				response.append("\n<month>")
				while ( nextFromDate <= endDate)
				{


					monthCounter = monthCounter + 1
					c1.setTime(nextFromDate)
					w1 = c1.get(Calendar.DAY_OF_MONTH)
					int daysLeftInCurrentMonth=c1.getActualMaximum(GregorianCalendar.DAY_OF_MONTH) - w1
					//println "Number of days left in the month :" + daysLeftInCurrentMonth
					currStartDate = c1.getTime()
					currEndDate = c1.getTime() + daysLeftInCurrentMonth
					//println "Current Start Date in the month :" + currStartDate
					//println "Current End Date in the month :" + currEndDate
					nextFromDate = currEndDate + 1
					//println "nextFromDate computed is: " + nextFromDate
					if(currEndDate > endDate) { currEndDate = endDate}
					res=dataManager.getProjectMonthlyReport( project,params.users, currStartDate,currEndDate)
					Log.debug( res )
					if(res == null)
					{
						def str="0"
						response.append("\n<m$monthCounter>${str}</m$monthCounter>")
					}
					else
					{
						if( res.size > 0)
						{

							res.each {summarylist ->
								summarylist.each{val->
									def str="0"
									if(val.totalhrs >0)
									{
										str= val.totalhrs.intValue()/8 +""
									}
									response.append("\n<m$monthCounter>${str}</m$monthCounter>")
								}

							}
						}
						else
						{
							Log.debug( "No rows found for the combination ProjCode: " + project + " for the month " + c1.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()))
							response.append("\n<m$monthCounter>0</m$monthCounter>")
						}


					}
				}
				response.append("\n</month>")
				response.append("\n<projectname>${xml_string(projectname)}</projectname>")
				response.append("\n</project>")
			}
		}
		catch(Exception e){
			e.printStackTrace();
			response= new StringBuffer()
			response.append("<reply>")
			response.append("<status code='1' error='true' description='${xml_string(e?.getMessage())}'/>")
		}




		Date from = null
		if(null != params.fromdate)
			from = getParsedDate(params.fromdate);

		Date to = null
		if(null != params.todate)
			to = getParsedDate(params.todate);

		//Building and Appending Header
		StringBuffer header = new StringBuffer()
		header.append("<header>")
		header.append("<hmonth>")
		def nextFromDate
		def currStartDate
		def currEndDate
		def startDate = from
		def endDate = to
		Calendar c1 = GregorianCalendar.getInstance();
		c1.setTime(startDate);
		int w1 = c1.get(Calendar.DAY_OF_MONTH);
		//Testing the new logic, based on number of days in a month.
		String month
		nextFromDate = startDate
		while ( nextFromDate <= endDate)
		{

			c1.setTime(nextFromDate)
			w1 = c1.get(Calendar.DAY_OF_MONTH)
			int daysLeftInCurrentMonth=c1.getActualMaximum(GregorianCalendar.DAY_OF_MONTH) - w1
			//println "Number of days left in the month :" + daysLeftInCurrentMonth
			currStartDate = c1.getTime()
			currEndDate = c1.getTime() + daysLeftInCurrentMonth
			//println "Current Start Date in the month :" + currStartDate
			//println "Current End Date in the month :" + currEndDate
			month = "" + c1.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + "-" + currStartDate[Calendar.YEAR]
			Log.debug(month)
			nextFromDate = currEndDate + 1
			//println "nextFromDate computed is: " + nextFromDate
			header.append("<$month/>")
			if(currEndDate > endDate) { currEndDate = endDate}
		}
		header.append("</hmonth>")
		header.append("</header>")

		Log.debug( "header Value is : " + header)
		response = response.append(header)
		response.append("<status code='0' error='false' description='Successfully retrieved Project information'/>")
		response.append("</reply>")
		Log.debug("Value of the response :" + response)
		String formattedResponse
		formattedResponse = response
		//println "Formatted Response : "  + formattedResponse.replaceAll('&' , 'amp;')
		return formattedResponse;

	}



	public String generateProjectEmployeeReport(HttpServletRequest request){


		def params=[
			"projects":request.getParameter("projects"),
			"fromdate":request.getParameter("fromdate"),
			"todate":request.getParameter("todate")
		]
		StringBuffer response= new StringBuffer()

		response.append("<reply>")



		try{

			Date from =null
			if(null != params.fromdate)
				from =getParsedDate(params.fromdate);

			Date to =null
			if(null != params.todate)
				to = getParsedDate(params.todate);


			def projects=[]


			if(null != params.projects && params.projects != ""){

				for(String project:params.projects.split(",")){
					projects.push(project)
				}

			}else{
				projects.push("")
			}
			ArrayList hashmaplist=new ArrayList();


			projects.each{curuser->
				//println("Fetching generateProjectEmployeeReport  for $curuser for $from to $to")

				def res=dataManager.getProjectEmployeeReport( curuser, from,to)
				if(null != res)
					hashmaplist.add(res)
			}




			// Add information as xml

			//println(summarylist.dump())
			if(hashmaplist.size() >0 ){
				hashmaplist.each {summarylist ->
					def curtoken=""
					summarylist.each{val->



						response.append("<project>")

						response.append("\n<name>${val.projectcode}</name>")

						def projectname=""
						def projectid=""
						if(null != val.projectInfo){
							projectname=val.projectInfo.name
							projectid=val.projectInfo.projectid
						}
						response.append("\n<projectname>${xml_string(projectname)}</projectname>")
						response.append("\n<projectid>${xml_string(projectid)}</projectid>")


						response.append("\n<user>${xml_string(val.user)}</user>")
						response.append("\n<team>${val.team}</team>")

						response.append("\n<hours>${val.hours}</hours>")
						response.append("\n<total></total>")



						def projdate=""
						if(val.entryDate){

							SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM dd yyyy");

							// (3) create a new String using the date format we want
							projdate= formatter.format(val.entryDate);
						}



						response.append("\n<projdate>${projdate}</projdate>")





						response.append("\n</project>")

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
				from =getParsedDate(params.fromdate);

			Date to =null
			if(null != params.todate)
				to = getParsedDate(params.todate);


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
				//println("Fetching summary for $curuser for $from to $to")

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
	public String getAdminAccess(HttpServletRequest request){


		def params=[
			"token":request.getParameter("token")
		]
		String ip=getIP(request)



		def msg=""

		if(params.token == Configurator.globalconfig?.adminaccesstoken ){



			if(getAdmins().contains(ip)){

				msg="Already an admin"
			}else{
				Configurator.customadmins.push(ip)
				msg="$ip added as admin"
			}

		}else{
			msg="Invalid token"
		}

		StringBuffer response= new StringBuffer()

		response.append("<reply>")



		response.append("<status code='0'  description='$msg'  />")


		response.append("</reply>")

		return response.toString();
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
		for(def bfr:Configurator.customadmins){



			if(i > 0)
				admins=admins +","



			admins=admins +"$bfr"

			i++
		}

		return admins

	}

	public String getTeams(){
		String admins=""
		int i=0
		for(def bfr:Configurator.globalconfig.teams){



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


	public String getappConfig(HttpServletRequest request){



		String ip=getIP(request)

		StringBuffer response= new StringBuffer()

		response.append("<reply>")
		def teams=getTeams();

		if( getAdmins().contains(ip))
			response.append("<status code='0' admin='true' description='Admin User' teams='$teams' />")
		else
			response.append("<status code='0' admin='false' description='Regular User' teams='$teams' />")

		response.append("</reply>")

		return response.toString();
	}


	public String getuserStatus(HttpServletRequest request){



		String ip=getIP(request)

		StringBuffer response= new StringBuffer()

		response.append("<reply>")


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
						),"enabled","enabled")

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
