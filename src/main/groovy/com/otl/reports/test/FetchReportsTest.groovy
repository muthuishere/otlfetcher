package com.otl.reports.test


import com.gargoylesoftware.htmlunit.Page
import com.gargoylesoftware.htmlunit.html.DomNodeList
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlFrame
import com.gargoylesoftware.htmlunit.html.HtmlPage

import com.gargoylesoftware.htmlunit.html.DomNodeList
import com.gargoylesoftware.htmlunit.html.HtmlElement

import com.otl.reports.beans.TimeEntry
import com.otl.reports.beans.UserInfo
import com.otl.reports.controller.DataManager
import com.otl.reports.controller.DbUpdater
import com.otl.reports.controller.FetchUserReport;
import com.otl.reports.controller.OTLServer
import com.otl.reports.controller.Responder
import com.otl.reports.model.WebBrowser

import groovy.json.JsonSlurper

import java.text.SimpleDateFormat
import java.util.ArrayList;
import java.util.Date;

import com.otl.reports.controller.Configurator;

class FetchReportsTest {

	static void respondTest(){

		Responder res=new Responder();
		println(res.getvalidusergroups());
	}

	static void timesheetupdatetest(){




		ArrayList<TimeEntry> timeEntries = new ArrayList<TimeEntry>();



		timeEntries.add(new TimeEntry(
				user:"mnk@testorac.net",
				projecttask:"task2 08",
				projectcode:"101185",
				entryDate: new Date()+1,
				tasktype:"Normal Hours - E&D Vendor - (Straight Time)",
				hours:8

				))

		timeEntries.add(new TimeEntry(
				user:"mnk@testorac.net",
				projecttask:"task2 08",
				projectcode:"101185",
				entryDate: new Date()+2,
				tasktype:"Normal Hours - E&D Vendor - (Straight Time)",
				hours:8

				))


		timeEntries.add(new TimeEntry(
				user:"mnk@testorac.net",
				projecttask:"task2 08",
				projectcode:"101185",
				entryDate: new Date(),
				tasktype:"Normal Hours - E&D Vendor - (Straight Time)",
				hours:8

				))

		timeEntries.add(new TimeEntry(
				user:"mnk@testorac.net",
				projecttask:"task2 08",
				projectcode:"101185",
				entryDate: new Date()+5,
				tasktype:"Normal Hours - E&D Vendor - (Straight Time)",
				hours:8

				))


		timeEntries.add(new TimeEntry(
				user:"mnkcheck@testorac.net",
				projecttask:"task2 08",
				projectcode:"101185",
				entryDate: new Date()+5,
				tasktype:"Normal Hours - E&D Vendor - (Straight Time)",
				hours:8

				))

		def user
		def startdate
		def enddate

		println(timeEntries.dump())
		timeEntries.sort{it.entryDate}
		//timeEntries.sort { a, b -> b.value.entryDate <=> a.value.entryDate }

		println(timeEntries.dump())

		def usertimemap=[:]
		for(TimeEntry timeEntry:timeEntries){

			def curtimeentries=[]
			if(usertimemap.contains(timeEntry.user) == false){

				curtimeentries.push(timeEntry)


			}else{
				curtimeentries=usertimemap[timeEntry.user]
				curtimeentries.push(timeEntry)

			}
			usertimemap[timeEntry.user]=curtimeentries


		}
		usertimemap.each {curuser,lstentries->

			lstentries.sort(it.entryDate)

			println("$curuser  ${lstentries[0].entryDate} ${lstentries[lstentries.size()-1].entryDate} ");

		}



	}

	static void  integrate(){



		DataManager dataManager=new DataManager()

		dataManager.init()

		dataManager.addUserEntries(new UserInfo(

				user: "mnavaneethakrishnan@corpuk.net",
				password:"welcomeaug1"
				),"enabled","enabled")

		dataManager.addUserEntries(new UserInfo(

				user: "pvenkatesan@corpuk.net",
				password:"welcomeaug1"
				),"enabled","enabled")


		Date date = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - c.getFirstDayOfWeek();
		c.add(Calendar.DAY_OF_MONTH, -dayOfWeek);

		Date weekStart = c.getTime();
		// we do not need the same day a week after, that's why use 6, not 7
		c.add(Calendar.DAY_OF_MONTH, 6);
		Date weekEnd = c.getTime();



		Date startdate = new SimpleDateFormat("yyyy.MM.dd").parse("2014.08.12");


		Date from=startdate;// weekStart;

		Date to= weekEnd;


		Thread.start {

			new DbUpdater().start(from ,to)


		}
		waitformore()

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
		(1..10).each { sleep( 1000 ) }
		CLEANUP_REQUIRED = false

	}
	static void  servertest(){

		OTLServer server=new OTLServer()
		server.init()
		server.startServer()


		waitformore(server)

		/*
		 while(!getbreakSignal()){
		 Thread.currentThread().sleep(100000);
		 }
		 */

	}

	static def getbreakSignal(){

		def cons = System.console()
		def yn
		if (cons) {
			yn = {((cons.readLine(it + " (y/n) ")?:"n").trim()?:"n")?.charAt(0).toLowerCase().toString() }
		} else {
			cons = javax.swing.JOptionPane.&showInputDialog
			yn = {((cons(it + " (y/n) ")?:"n").trim()?:"n")?.charAt(0).toLowerCase().toString() }
		}
		if (yn("Press y to stop server?") == 'y'){

			return true
		}
		return false
	}

	static void browserNavigatetest(){

		WebBrowser webBrowser=new WebBrowser()
		webBrowser.init(Configurator.globalconfig.proxy)
		webBrowser.Navigate("http://ebiz.uk.three.com:80/OA_HTML/RF.jsp?function_id=10129&resp_id=51959&resp_appl_id=808&security_group_id=0&lang_code=US")
		webBrowser.printAll()


	}

	static void updatetimesheettest(){

		/*
		 * =======<com.otl.reports.beans.TimeEntry@c67308 entryDate=Wed Aug 12 00:00:00 IST 1970 user=mnk@testorac.net projectcode=101185 projecttask=Software Maintenance tasktype=Normal Hours - E&D Vendor - (Straight Time) hours=2 details=null>=========
		 =======<com.otl.reports.beans.TimeEntry@65657e entryDate=Fri Aug 14 00:00:00 IST 1970 user=mnk@testorac.net projectcode=101185 projecttask=Software Maintenance tasktype=Normal Hours - E&D Vendor - (Straight Time) hours=1 details=null>=========
		 =======<com.otl.reports.beans.TimeEntry@1c9b008 entryDate=Thu Aug 13 00:00:00 IST 1970 user=mnk@testorac.net projectcode=101681 projecttask=task2 08 tasktype=Normal Hours - E&D Vendor - (Straight Time) hours=2 details=null>=========
		 =======<com.otl.reports.beans.TimeEntry@30173b entryDate=Fri Aug 14 00:00:00 IST 1970 user=mnk@testorac.net projectcode=101681 projecttask=task2 08 tasktype=Normal Hours - E&D Vendor - (Straight Time) hours=4 details=null>=========
		 =======<com.otl.reports.beans.TimeEntry@1de69f2 entryDate=Wed Aug 12 00:00:00 IST 1970 user=mnk@testorac.net projectcode=101681 projecttask=task2 08 tasktype=Normal Hours - E&D Vendor - (Straight Time) hours=6 details=null>=========
		 =======<com.otl.reports.beans.TimeEntry@c71fda entryDate=Thu Aug 13 00:00:00 IST 1970 user=mnk@testorac.net projectcode=101681 projecttask=task2 08 tasktype=Normal Hours - E&D Vendor - (Straight Time) hours=6 details=null>=========
		 =======<com.otl.reports.beans.TimeEntry@4820da entryDate=Fri Aug 14 00:00:00 IST 1970 user=mnk@testorac.net projectcode=101681 projecttask=task2 08 tasktype=Normal Hours - E&D Vendor - (Straight Time) hours=3 details=null>=========
		 =======<com.otl.reports.beans.TimeEntry@115da09 entryDate=Tue Aug 11 00:00:00 IST 1970 user=mnk@testorac.net projectcode=101183 projecttask=Unpaid Absence tasktype=Normal Hours - E&D Vendor - (Straight Time) hours=8 details=null>=========
		 =======<com.otl.reports.beans.TimeEntry@1a95c64 entryDate=Sat Aug 15 00:00:00 IST 1970 user=mnk@testorac.net projectcode=101183 projecttask=Unpaid Absence tasktype=Normal Hours - E&D Vendor - (Straight Time) hours=8 details=null>=========
		 webWindowClosed Page : <com.gargoylesoftware.htmlunit.WebWindowEvent@8fda59 oldPage_=HtmlPage(about:blank)@4721772 newPage_=null type_=2 source=FrameWindow[name="_pprIFrame"]>
		 * 
		 * 	
		 */
		ArrayList<TimeEntry> timeEntries= new ArrayList<TimeEntry>()

		//if(!entryDate || !user || !projectcode || !projecttask ||  !tasktype)


		timeEntries.add(new TimeEntry(
				user:"mnk@testorac.net",
				projecttask:"task2 08",
				projectcode:"101185",
				entryDate: new Date(),
				tasktype:"Normal Hours - E&D Vendor - (Straight Time)",
				hours:8

				))

		timeEntries.add(new TimeEntry(
				user:"mnk@testorac.net",
				projecttask:"task2 08",
				projectcode:"101185",
				entryDate: new Date()+1,
				tasktype:"Normal Hours - E&D Vendor - (Straight Time)",
				hours:8

				))

		timeEntries.add(new TimeEntry(
				user:"mnk@testorac.net",
				projecttask:"task2 08",
				projectcode:"101185",
				entryDate: new Date()+2,
				tasktype:"Normal Hours - E&D Vendor - (Straight Time)",
				hours:8

				))

		timeEntries.add(new TimeEntry(
				user:"mnk@testorac.net",
				projecttask:"task2 08",
				projectcode:"101185",
				entryDate: new Date()+2,
				tasktype:"Normal Hours - E&D Vendor - (Straight Time)",
				hours:8

				))


		DataManager dataManager=new DataManager()

		dataManager.init("txt.db")

		dataManager.print()
		dataManager.addTimeEntries(timeEntries);



		dataManager.addUserEntries(new UserInfo(

				user: "mnk@testorac.net",
				password:"alka"
				),"enabled","enabled")

		dataManager.addUserEntries(new UserInfo(

				user: "mnk2@testorac.net",
				password:"alka"
				),"enabled","enabled")

		ArrayList<TimeEntry> timeentries=dataManager.getTimesheetEntries("mnk@testorac.net",new Date()-10,new Date()+1)

		println timeentries

		//	 timeentries.each{timeentry->
		//		 println timeentry
		//	   }


		ArrayList<UserInfo> userentries=dataManager.getUserEntries()

		println userentries


		//	  userentries.each{timeentry->
		//		 println timeentry
		//	   }
		//
		UserInfo userinfo =dataManager.findUser("mnk@testorac.net")
		println userinfo


		userinfo =dataManager.findUser("@testorac.net")
		println userinfo
		/*
		 UserInfo userInfo=dataManager.findUser("mnk2@testorac.net") 
		 println(userInfo?.user);
		 userInfo=dataManager.findUser("mjknavaneethakrishnan2@testorac.net")
		 println(userInfo?.user);
		 //	getTimesheetEntries(Date from,Date to)
		 dataManager.close()
		 */
	}

	static void apptest(){

		FetchUserReport fetchUserReport=new FetchUserReport()
		fetchUserReport.init()


		Date date = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - c.getFirstDayOfWeek();
		c.add(Calendar.DAY_OF_MONTH, -dayOfWeek);

		Date weekStart = c.getTime();
		// we do not need the same day a week after, that's why use 6, not 7
		c.add(Calendar.DAY_OF_MONTH, 6);
		Date weekEnd = c.getTime();



		Date startdate = new SimpleDateFormat("yyyy.MM.dd").parse("2014.08.12");


		Date from=startdate;// weekStart;

		Date to= weekEnd;

		//println(fetchUserReport.parseDate("		 Mon, Aug 18"))

		//
		ArrayList<TimeEntry> timeEntries= new ArrayList<TimeEntry>()

		timeEntries=fetchUserReport.startFetch(new UserInfo(
				user: "mnk@testorac.net",
				password:"wa1"
				), from, to)


	}
	static void timesheetstatustest(){

		WebBrowser webBrowser=new WebBrowser()
		webBrowser.init(null)
		webBrowser.Navigate("http://localhost:7080/test.html")
		def resultContainer= webBrowser.findElemById("Hxcmytcsearchresults")
		//println(resultContainer.dump())

		DomNodeList<HtmlElement> links=resultContainer.getElementsByTagName("a")

		List<HtmlElement> validlinks= new ArrayList<HtmlElement>();

		for(HtmlElement link:links){

			if(link.asXml().contains("DetailEnable")){

				def currow=link.getParentNode().getParentNode();
				println("===" + currow.childNodes.get(0).asText()+ "==")
				def cursheet=[:];
				cursheet["link"]=link
				cursheet["status"]=currow?.childNodes?.get(0)?.asText()

				validlinks.add(cursheet)


			}

		}


		//
	}
	static void browsertest(){

		WebBrowser webBrowser=new WebBrowser()
		webBrowser.init("firefox")
		webBrowser.Navigate("http://ebiz.uk.three.com:80/OA_HTML/RF.jsp?function_id=10129&resp_id=51959&resp_appl_id=808&security_group_id=0&lang_code=US")
		//webBrowser.printAll()

		//webBrowser.typeOnName("ssousername", "mnk@testorac.net")
		//webBrowser.typeOnName("password", "wa1")

		webBrowser.typeOnName("ssousername", "mnavaneesdsdthakrishnan@testorac.net")
		webBrowser.typeOnName("password", "wa1")
		//webBrowser.clickOnInputName(webBrowser)



		webBrowser.executeScriptforNewPage("buttonSubmit('OK');")

		webBrowser.waitForPageLoad();

		//webBrowser.printAll()

		if(webBrowser.findElemByName("ssousername") ==null){

			//Verify Page is valid page, check element name exists

			webBrowser.NavigateInner("http://ebiz.uk.three.com:80/OA_HTML/RF.jsp?function_id=10129&resp_id=51959&resp_appl_id=808&security_group_id=0&lang_code=US")

			webBrowser.waitForPageLoad();
			webBrowser.printAll()
			println("Valid User identified")

		}else{
			println("Invalid User identified")
		}


		println "Completed Closing"
		webBrowser.close()
		println "Completed Closed"
	}

	static void displayClassPath() {

		ClassLoader classloader = Thread.currentThread().getContextClassLoader();

		while(classloader != null) {
			URL[] urls = ((URLClassLoader)classloader).getURLs();

			for(URL url: urls){
				System.out.println(url.getFile());
			}
			classloader = (URLClassLoader)classloader.getParent();

		}
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

	static void updateprojectcodetest(){

		Thread.start {

			new DbUpdater().updateprojectcodes()


		}


	}
	static void leavecodetest(){


		String leavecode=""
		int i=0
		for(def bfr:Configurator.globalconfig.leavecodes){



			if(i > 0)
				leavecode=leavecode +","



			leavecode=leavecode +"'$bfr'"

			i++
		}

		println leavecode
	}

	static void browserprojectcodetest(){

		def baseurl="http://ebiz.uk.three.com:80"
		baseurl="http://localhost:8080/otlcheck"

		def searchsuffix="&categoryChoice=HxcCuiProjectNumber&N1%3Alength=0&_FORM=_LOVResFrm&tableNameFormValue=N1&stateCheckForTable=FFFF&event=lovFilter&source=&searchAreaMode=&value=&state=&partialTargets=&partial=&searchText="
		def timeurl=baseurl + "/createtime.htm"

		def projcode="100202"


		WebBrowser webBrowser=new WebBrowser()
		webBrowser.init(null)
		webBrowser.Navigate(timeurl)
		//webBrowser.printAll()

		//webBrowser.typeOnName("ssousername", "mnk@testorac.net")
		//webBrowser.typeOnName("password", "wa1")

		//document.querySelector("input.x4").parentNode.querySelector("a")

		ArrayList<HtmlElement> cells=webBrowser.getElemsByTagClass("input","x4")

		if(null ==cells || cells.size() == 0){

			println("No response details");
			return null

		}

		HtmlElement validparent=cells.get(0).parentNode;


		DomNodeList<HtmlElement> links= validparent.getElementsByTagName("a"); //get a list of all table rows


		if(null ==links || links.size() == 0){

			println("No Link response details");
			return null

		}

		def linkxml=links.get(0).asXml();

		linkxml=linkxml.replace("{", "#")
		linkxml=linkxml.replace("}", "#")
		println(linkxml)



		//	println(linkxml.split("#").length)
		//	println(linkxml.split("#")[1].split(",").length)

		def chunks=linkxml.split("#")[1].split(",")
		def projcodechunk=""
		for(def chunk:chunks){

			if(chunk.contains("'D'"))
				projcodechunk=chunk.split(":")[1].replace("'","")

		}

		def pcurl="${baseurl}${projcodechunk}${searchsuffix}${projcode}"

		println(pcurl)

		webBrowser.Navigate(pcurl)

		HtmlFrame frame=webBrowser.getFirstElementByTag("frame")

		if(null ==frame) {

			println("No response details");
			return null

		}


		HtmlPage framepage=frame.getEnclosedPage();

		def tblelem=framepage.getHtmlElementById("HXC_CUI_PROJECT_LOV_lovTable")


		DomNodeList<HtmlElement> spanelems= tblelem.getElementsByTagName("span"); //get a list of all table rows


		if(null ==links || links.size() == 0){

			println("No tblelem response details");
			return null

		}
		def projectname=""
		def projectid=""
		def selectedprojnumber=""
		for(HtmlElement spanelem:spanelems){

			if(null != spanelem.getAttribute("title") && spanelem.getAttribute("title").contains("Project Number")){

				selectedprojnumber=spanelem.asText()

			}
			if(null != spanelem.getAttribute("title") && spanelem.getAttribute("title").contains("Project Name")){

				projectname=spanelem.asText()

			}
			if(null != spanelem.getAttribute("title") && spanelem.getAttribute("title").contains("Project ID")){

				projectid=spanelem.asText()

			}


		}

		//HXC_CUI_PROJECT_LOV_lovTable
		println("$projectname $projectid $selectedprojnumber")

	}
	
	static void browserAuthenticatetest(){
		
				WebBrowser webBrowser=new WebBrowser()
				webBrowser.init(Configurator.globalconfig.proxy,	[host: 'ebiz.uk.three.com', user: 'mnavaneethakrishnan@corpuk.net', pwd: 'April#2015'])
				webBrowser.Navigate("http://ebiz.uk.three.com:80/OA_HTML/RF.jsp?function_id=10129&resp_id=51959&resp_appl_id=808&security_group_id=0&lang_code=US")
				webBrowser.printAll()
		
		
			}
	

	static main(args) {
		//	displayClassPath();

		//	browsertest();
		//	apptest()

		//	updatetimesheettest()

		//	servertest()

		def configFileName
		
		 if(args.size() != 1)
		 {
		 println "Usage: FetchReports.groovy <configuration file>"
		 System.exit(1)
		 }
		 configFileName=args[0]
		 

		//configFileName="C:\\muthu\\otl\\otlfetcher\\origotlfetcher.conf"
		parseconfig(configFileName)

		browserAuthenticatetest();



	}

}
