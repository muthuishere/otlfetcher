package com.otl.reports.test

import com.otl.reports.beans.TimeEntry
import com.otl.reports.beans.UserInfo
import com.otl.reports.controller.DataManager
import com.otl.reports.controller.FetchUserReport;
import com.otl.reports.controller.OTLServer
import com.otl.reports.model.WebBrowser

import java.text.SimpleDateFormat
import java.util.ArrayList;
import java.util.Date;

class FetchReportsTest {

	
	
	static void  servertest(){
		
		OTLServer server=new OTLServer()
		server.init(2111, "//pages//")
		server.startServer()
		
		
		while(!getbreakSignal()){
			
			Thread.currentThread().sleep(100000);
		}
		server.stopServer()
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
		))
	
	dataManager.addUserEntries(new UserInfo(
		
		user: "mnk2@testorac.net",
		password:"alka"
		))
		
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
	
	static main(args) {
	//	displayClassPath();
		
	//	browsertest();
	//	apptest()
		
	//	updatetimesheettest()
		
		servertest()
		
	}

}
