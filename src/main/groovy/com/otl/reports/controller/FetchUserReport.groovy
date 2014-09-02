package com.otl.reports.controller

import java.text.SimpleDateFormat
import java.util.ArrayList;

import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableBody
import com.otl.reports.beans.TimeEntry
import com.otl.reports.beans.UserInfo
import com.otl.reports.exceptions.ServiceException
import com.otl.reports.model.WebBrowser
import com.otl.reports.helpers.Log

class FetchUserReport {

	WebBrowser webBrowser=null

	def init(){

		 webBrowser=new WebBrowser()
		webBrowser.init("firefox")
	}

	def login(UserInfo userInfo){


		webBrowser.Navigate("http://ebiz.uk.three.com:80/OA_HTML/RF.jsp?function_id=10129&resp_id=51959&resp_appl_id=808&security_group_id=0&lang_code=US")




		webBrowser.typeOnName("ssousername", userInfo.user)
		webBrowser.typeOnName("password",  userInfo.password)
		//webBrowser.clickOnInputName(webBrowser)



		webBrowser.executeScriptforNewPage("buttonSubmit('OK');")

		webBrowser.waitForPageLoad();
		if(webBrowser.findElemByName("ssousername") ==null)
			return true
		else
			return false


	}

	def gotoTimesheetPage(){
		webBrowser.NavigateInner("http://ebiz.uk.three.com:80/OA_HTML/RF.jsp?function_id=10129&resp_id=51959&resp_appl_id=808&security_group_id=0&lang_code=US")

		webBrowser.waitForPageLoad();
	}
	
	def ShowReports(String from,String to){
		
		
	
		
		webBrowser.typeOnId("Hxcdatesfrom",from)
		webBrowser.typeOnId("Hxcdatesto",  to)
		
		
		String script = "submitForm('DefaultFormName',1,{'_FORM_SUBMIT_BUTTON':'Hxcgocontrol'});"
		webBrowser.executeScriptforNewPage(script)
		
		webBrowser.waitForPageLoad();
	}
	
	def parseDate(String dstr){
		
		//"		 Mon, Aug 18"
		Date startdate = new SimpleDateFormat("EEE, MMM d").parse( dstr.trim());
		return startdate
	}
	def getTimesheetData(def user){
		
		//document.querySelectorAll("#Hxctimecard")[0].querySelectorAll("td.x1r")[0].parentNode.parentNode
		
		def resultContainer= webBrowser.findElemById("Hxctimecard")
		
		
		ArrayList<HtmlElement> cells=webBrowser.getElemsByTagClass("td","x1r")
		
		
		ArrayList<TimeEntry> timeEntries= new ArrayList<TimeEntry>()
		if(cells.size() == 0){
						 
			Log.error("No response details");
			return
			
		}
		
		String startdateStr= cells.get(3).asText()
		
		ArrayList<Date> dates= new ArrayList<Date>()
		//Mon, Aug 18"
		Date startdate = parseDate(startdateStr);//new SimpleDateFormat("EEE, MMM d").parse( startdateStr);
		
		dates.add(startdate)
		dates.add(startdate+1)
		dates.add(startdate+2)
		dates.add(startdate+3)
		dates.add(startdate+4)
		dates.add(startdate+5)
		dates.add(startdate+6)
		
	
		
		HtmlTableBody tableBody=cells.get(0)?.getParentNode()?.getParentNode();
		
		
		boolean init=true
		
			for(int i=1;i<tableBody.getChildNodes().size()-1;i++){
				
				HtmlElement row=tableBody.getChildNodes().get(i)//getElementsByTagName("tr").get(i)
				
				def projectcode=row.getChildNodes().get(0).asText().trim()
				def projecttask=row.getChildNodes().get(1).asText().trim()
				def tasktype=row.getChildNodes().get(2).asText().trim()
				
				int datePointer=0
				for(int j=3;j<row.getChildNodes().size()-2;j++){
					
					HtmlElement curcell=row.getChildNodes().get(j)
					//println("#########" + curcell.asText())
					
					def curhours=getNum(curcell.asText())
					def curDate=dates.get(datePointer)
					datePointer++
					if(curhours >0){
						TimeEntry te=new TimeEntry(
							entryDate:curDate,
							user: user,  
							projectcode:projectcode, 
							tasktype: tasktype, 
							projecttask: projecttask, 
							hours: curhours 
							)
						
						timeEntries.add(te)
						println("=======${te.dump()}=========")
					}
					
				}
				
				
			}
		
		
		
		return timeEntries
	}
	
	def getNum(String res){
		
		def curhours=0
		try{
			if(res.trim() != "")
			curhours=Integer.parseInt(res.trim())
			
		}catch(Exception e){
		
			println(e)
		}
		
		return curhours
	}
	def startFetch(UserInfo userInfo,Date from,Date to) throws ServiceException{

		ArrayList<TimeEntry> timeEntries= new ArrayList<TimeEntry>()
		
		if(!userInfo)
			throw new ServiceException("User Information cannot be empty")
		
			if(!from || !to)
			throw new ServiceException("from date & to date cannot be empty")
			
			
		if(!login(userInfo))
			throw new ServiceException("Invalid User Credentials")


			Log.info("Valid User identified")
			
			Log.info("Opening Timesheet page")
			
		gotoTimesheetPage()

		//Verify Page is valid page, check element name exists

		Log.info("Opening Reports page")
		ShowReports(from.format("dd-MMM-yyyy") ,to.format("dd-MMM-yyyy"))
		
	
		
		
		def resultContainer= webBrowser.findElemById("Hxcmytcsearchresults")
		
		if(null == resultContainer)
			throw new ServiceException("Empty response")
			
		
		DomNodeList<HtmlElement> links=resultContainer.getElementsByTagName("a")
		
		if(links.length == 0){
			//verify no results is there
			//Return response No timesheet
			if(resultContainer.asXml().contains("No results found")){
				
				Log.info("No response for particular period ${userInfo.user} from ${from} to ${to}")
				return;
			}
			 
			Log.error("No response details");
			return
			
		}
		
		List<HtmlElement> validlinks= new ArrayList<HtmlElement>();
		
		for(HtmlElement link:links){
			
			if(link.asXml().contains("DetailEnable")){
				validlinks.add(link)
				Log.info(link.asXml())
				
			}
			
		}
		
		Log.info("Opening Reports page")
		
		HtmlPage  resultPage=webBrowser.currentPage
		
		
		
		for(HtmlElement validlink:validlinks){
			
			//Click element
			
			webBrowser.clickLink(validlink)
			webBrowser.waitForPageLoad();
			//webBrowser.printAll()
			
			timeEntries.addAll(getTimesheetData(userInfo.user))
			
			//document.querySelectorAll("#Hxctimecard")[0].querySelectorAll("td.x1r")[0].parentNode.parentNode
			
			webBrowser.currentPage=resultPage
			//wait for page load
			
			//read information
			
			//print
			
			//reset current page
		}
		
		Log.info("Completed" +timeEntries.dump())
		//Log.info("Printing TImesheet Reports")
		
		//Log.info(resultContainer.asXml())
		
		//Iterate table
		
		

		//webBrowser.printAll()


		close()


		Log.info "Completed Closed"
		return timeEntries
	}

	def close(){
		webBrowser.close()
	}
}
