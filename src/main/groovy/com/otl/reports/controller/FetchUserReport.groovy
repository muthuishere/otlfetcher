package com.otl.reports.controller

import java.text.SimpleDateFormat
import java.util.ArrayList;

import com.gargoylesoftware.htmlunit.html.DomElement
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

	def init(def proxy){

		 webBrowser=new WebBrowser()
		webBrowser.init(proxy)
	}
	
		
	
	def login(UserInfo userInfo){


		webBrowser.Navigate(Configurator.globalconfig.timesheet_url)

		


		webBrowser.typeOnName(Configurator.globalconfig.element_map.loginuser, userInfo.user)
		webBrowser.typeOnName(Configurator.globalconfig.element_map.loginpwd,  userInfo.password)
		//webBrowser.clickOnInputName(webBrowser)



		webBrowser.executeScriptforNewPage("buttonSubmit('OK');")

		webBrowser.waitForPageLoad();
		if(webBrowser.findElemByName(Configurator.globalconfig.element_map.loginuser) ==null)
			return true
		else
			return false


	}

	def gotoTimesheetPage(){
		webBrowser.NavigateInner(Configurator.globalconfig.timesheet_url)

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
		Date startdate = new SimpleDateFormat("EEE, MMM d yyyy").parse( dstr.trim());
		return startdate
	}
	def getTimesheetData(def user,def tsstatus){
		
		//document.querySelectorAll("#Hxctimecard")[0].querySelectorAll("td.x1r")[0].parentNode.parentNode
		
		
		
		String timeval= webBrowser.findElemById("DISPLAY_START_TIME").getTextContent().trim().replaceAll("\"", "").trim()
		//Monday, May 26 2014
		Date startdate = new SimpleDateFormat("EEEE, MMM dd yyyy").parse( timeval.trim());
		
		def resultContainer= webBrowser.findElemById("Hxctimecard")
		
		
		ArrayList<HtmlElement> cells=webBrowser.getElemsByTagClass("td","x1r")
		
		
		ArrayList<TimeEntry> timeEntries= new ArrayList<TimeEntry>()
		if(cells.size() == 0){
						 
			Log.error("No response details");
			return null
			
		}
		
		//String startdateStr= cells.get(3).asText().trim() +" " +yearstring
		
		ArrayList<Date> dates= new ArrayList<Date>()
		//Mon, Aug 18"
		//Date startdate = parseDate(startdateStr);//new SimpleDateFormat("EEE, MMM d").parse( startdateStr);
		
		
		//println("============== $startdate =======================")
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
							hours: curhours ,
							status:tsstatus,
							details:""
							)
						
						boolean exists=false
						for(TimeEntry existingTimeEntry:timeEntries){
							
							if(existingTimeEntry.objectequals(te)){
								//Add hours
								// add detail
								existingTimeEntry.hours += te.hours
								existingTimeEntry.details = existingTimeEntry.details + "  " + te.details
								exists=true
							}
						}
						if(!exists)
							timeEntries.add(te)
							
							
							
						//addorupdate(timeEntries,te)
					//	println("=======${te.dump()}=========")
						Log.info("Adding for ${user} from ${te.dump()}")
					}
					
				}
				
				
			}
		
		
		
		return timeEntries
	}
	def addorupdate(ArrayList<TimeEntry>  timeEntries,TimeEntry timeEntry){
		boolean exists=false
		for(TimeEntry existingTimeEntry:timeEntries){
			
			if(existingTimeEntry.equals(timeEntry)){
				//Add hours
				// add detail
				existingTimeEntry.hours += timeEntry.hours
				existingTimeEntry.details = existingTimeEntry.details + "  " + timeEntry.details
				exists=true
			}
		}
		if(!exists)
			timeEntries.add(timeEntry)
			
			
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
			throw new ServiceException("INVALID CREDENTIALS:Invalid User Credentials")


			
			
			Log.info("Opening Timesheet page for ${userInfo.user}")
			
		gotoTimesheetPage()

		//Verify Page is valid page, check element name exists

		Log.info("Opening Reports page [" +from.format("dd-MMM-yyyy") +"    " +  to.format("dd-MMM-yyyy"))
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
				return timeEntries;
			}
			 
			Log.error("No response details");
			return timeEntries
			
		}
		
		def tsresults= new ArrayList();
		
		for(HtmlElement link:links){
			
			if(link.asXml().contains("DetailEnable")){
				def cursheet=[:];
				cursheet.link=link
				def currow=link.getParentNode().getParentNode();
				
				cursheet.status=currow?.childNodes?.get(0)?.asText()
				
				tsresults.add(cursheet)
				
				Log.info(link.asXml() + " Status ${cursheet.status}")
			//	Log.info(link.asXml())
				
			}
			
		}
		
		Log.info("Opening Reports page for ${userInfo.user}")
		
		HtmlPage  resultPage=webBrowser.currentPage
		
		
		
		for(def tsresult:tsresults){
			
			
			
			
			webBrowser.clickLink(tsresult.link)
			webBrowser.waitForPageLoad();
			//webBrowser.printAll()
			
			def resplist=getTimesheetData(userInfo.user,tsresult.status)
			if(null !=resplist )
				timeEntries.addAll(resplist)
			
			//document.querySelectorAll("#Hxctimecard")[0].querySelectorAll("td.x1r")[0].parentNode.parentNode
			
			webBrowser.currentPage=resultPage
			
		}
		
		


		close()


		Log.info "Completed for ${userInfo.user} fetched ${timeEntries.size()} entries from ${from} to  ${to}"
		return timeEntries
	}

	def close(){
		webBrowser.close()
	}
}
