package com.otl.reports.controller

import java.text.SimpleDateFormat
import java.util.ArrayList;

import com.gargoylesoftware.htmlunit.html.DomElement
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlFrame
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableBody
import com.otl.reports.beans.ProjectInfo
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

	static def getstringbetween(String str,String start,String end){

		def resp=null
		try{

			resp=str.substring(str.indexOf(start)+start.length(),str.indexOf(end, str.indexOf(start)))
		}catch(Exception){
		}
		return resp
	}

	def login(UserInfo userInfo){


		webBrowser.Navigate(Configurator.globalconfig.otl_host + Configurator.globalconfig.timesheet_url)




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
		webBrowser.NavigateInner(Configurator.globalconfig.otl_host + Configurator.globalconfig.timesheet_url)

		webBrowser.waitForPageLoad();
	}

	def gotoCreateTimecardPage(){
		webBrowser.NavigateInner(Configurator.globalconfig.otl_host + Configurator.globalconfig.timesheet_entry_url)

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

	def crawlProjectInfo(){

		ProjectInfo projectinfo=new ProjectInfo();

		//webBrowser.NavigateInner(url)



		HtmlFrame frame=webBrowser.getFirstElementByTag("frame")



		if(null ==frame) {

			println("No crawlProjectInfo details");
			return null

		}


		HtmlPage framepage=frame.getEnclosedPage();

		//println(framepage.asXml())
		def tblelem=framepage.getHtmlElementById("HXC_CUI_PROJECT_LOV_lovTable")


		DomNodeList<HtmlElement> spanelems= tblelem.getElementsByTagName("span"); //get a list of all table rows


		if(null ==spanelems || spanelems.size() == 0){

			println("No  response details");
			return null

		}
		def projectname=""
		def projectid=""
		def selectedprojnumber=""
		for(HtmlElement spanelem:spanelems){

			if(null != spanelem.getAttribute("title") && spanelem.getAttribute("title").contains("Project Number")){


				projectinfo.code=spanelem.asText()

			}
			if(null != spanelem.getAttribute("title") && spanelem.getAttribute("title").contains("Project Name")){

				projectinfo.name=spanelem.asText()


			}
			if(null != spanelem.getAttribute("title") && spanelem.getAttribute("title").contains("Project ID")){


				projectinfo.projectid=spanelem.asText()
			}


		}
		println(projectinfo.dump())
		return projectinfo;

	}
	def getProjectcodeSearchurl(){


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


		projcodechunk=projcodechunk.replaceAll("&amp;", "&")
		println(projcodechunk)
		//Split with key param
		//find  oas
		def oasstring=""
		/*
		 projcodechunk.split("&").each{keyvalpair ->
		 if(keyvalpair.contains("=") && keyvalpair.split("=")[0] == "oas" ){
		 oasstring=keyvalpair.replace("=", "%3D")
		 return
		 }
		 }
		 */
		projcodechunk=projcodechunk + "&event=lovFilter&source=A241N1display&searchText=#SEARCH#&enc=ISO-8859-1&contextURI=/OA_HTML/&configName=OAConfig"
		def redirecturl=projcodechunk//URLEncoder.encode(projcodechunk)
		println(redirecturl)

		//		%2FOA_HTML%2FOA.jsp%3Fregion%3D%2Foracle%2Fapps%2Fhxc%2Fselfservice%2Fconfigui%2Fwebui%2FCuiProjectLovRN%26regionCode%3DHXC_CUI_PROJECT_LOV%26regionAppId%3D809%26lovBaseItemName%3DA241N1display%26fndOAJSPinEmbeddedMode%3Dy%26_ti%3D634763544%26label%3DProject%26formName%3DDefaultFormName%26addBreadCrumb%3DS%26baseAppMod%3Doracle.apps.hxc.selfservice.timecard.server.TimecardAM%26amUsageMode%3D1%26lovMainCriteria%3DHxcCuiProjectNumber%26Criteria%3DA241N1display.HxcCuiProjectNumber%26PassiveCriteria%3D%26retainAM%3DY%26Selector%3DN%26lovMultiSelectDelimiter%3D%253B%26baseToLovKey%3D%2Foracle%2Fapps%2Fhxc%2FregionMap%2FHXCTIMECARDACTIVITIESPAGE.A241N1display_%2Foracle%2Fapps%2Fhxc%2Fselfservice%2Fconfigui%2Fwebui%2FCuiProjectLovRN%26baseCompMode%3D11.5.10%26event%3DlovFilter%26source%3DA241N1display%26searchText%3D
		def baseurl=Configurator.globalconfig.otl_host
		def project_code_url=Configurator.globalconfig.project_code_url


		def projectsearchurl="${baseurl}${redirecturl}"

		println("templateurl $projectsearchurl")

		return projectsearchurl;

	}

	def getSearchPage(){


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

		def script=getstringbetween(linkxml,"return ","\"");// linkxml.substring(linkxml.indexOf("return "),linkxml.indexOf("\"", linkxml.indexOf("return")))

		//def script=linkxml.substring(linkxml.indexOf("return"),linkxml.indexOf("\"", linkxml.indexOf("return"));


		webBrowser.executeScript(script)

		webBrowser.executeScriptforNewPage("gototspage();")

		return webBrowser.currentPage
	}


	def getProjectDetails(UserInfo userInfo,def projectcodes) throws ServiceException{

		ArrayList<ProjectInfo> projectdetails= new ArrayList<ProjectInfo>()


		if(!userInfo)
			throw new ServiceException("User Information cannot be empty")



		if(!login(userInfo))
			throw new ServiceException("INVALID CREDENTIALS:Invalid User Credentials")



		if(!projectcodes && projectcodes.size() == 0)
			throw new ServiceException("Project Information cannot be empty")




		Log.info("Opening goto CreateTimecard Page")

		gotoCreateTimecardPage()


		webBrowser.executeScript("var tspage;function gototspage(){ document.location=tspage; } ;window.open = function (open) {    return function (url, name, features) {               name = name || 'default_window_name';	tspage=url;	document.location=url;	return open(url);    };}(window.open);")


		Log.info("Opening goto search Page")
		HtmlPage curSearchPage=getSearchPage()


		Log.info("setfirstFrameAsPage")







		int i=1;
		for(def code:projectcodes){

			try{
				webBrowser.currentPage=curSearchPage;
				//if(i > 3)
				//break;

				HtmlFrame frame=webBrowser.getFirstElementByTag("frame")



				if(null ==frame) {


					throw new ServiceException("Unable to find frame details")

				}


				HtmlPage framepage=frame.getEnclosedPage();

				//if(!webBrowser.setfirstFrameAsPage()){


				//	}



				//type project code & search
				webBrowser.typeOnFrameName(framepage,"searchText",code +"")

				webBrowser.executeScriptforNewPageinFrame(framepage,"submitForm('_LOVResFrm',1,{'searchAreaMode':'',event:'lovFilter',source:'A241N1display'});")

				/*
				 if(!webBrowser.setfirstFrameAsPage()){
				 throw new ServiceException("Unable to find search details")
				 }
				 */
				//println("Search completed printing page")
				//webBrowser.printAll()

				def projectInfo=crawlProjectInfo()



				if(null != projectInfo && projectInfo.code != null &&  projectInfo.code.trim().equals("") == false)
					projectdetails.add(projectInfo)

				i++


			}	catch(Exception e){

				e.printStackTrace()
			}

		}


		close()


		Log.info "Completed Project code crawling"
		return projectdetails
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
