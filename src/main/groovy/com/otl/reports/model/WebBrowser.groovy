package com.otl.reports.model

import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page
import com.gargoylesoftware.htmlunit.ScriptResult
import com.gargoylesoftware.htmlunit.TextPage
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.WebWindowEvent
import com.gargoylesoftware.htmlunit.WebWindowListener
import com.gargoylesoftware.htmlunit.html.DomElement
import com.gargoylesoftware.htmlunit.html.DomNode
import com.gargoylesoftware.htmlunit.html.DomNodeList
import com.gargoylesoftware.htmlunit.html.HtmlElement
import com.gargoylesoftware.htmlunit.html.HtmlFrame
import com.gargoylesoftware.htmlunit.html.HtmlInput
import com.gargoylesoftware.htmlunit.html.HtmlPage

import org.apache.commons.lang3.StringUtils
import org.codehaus.groovy.control.CompilerConfiguration

import com.gargoylesoftware.htmlunit.BrowserVersion
import com.otl.reports.exceptions.ServiceException
import com.otl.reports.helpers.BrowserHelper
import com.otl.reports.helpers.Log
class WebBrowser {

	WebClient webClient = null;
	CurWebWindowListener curWebWindowListener=null;




	//The Navigator item which is currently active, by active we mean it was the last item returned from the browser which is a NonEmptyNavigator
	HtmlPage  currentPage = null

	public testapp(){
		
		curWebWindowListener=new CurWebWindowListener()
		//"http://ebiz.uk.three.com/oa_servlets/AppsLogin
		try{
		webClient = new WebClient(BrowserVersion.CHROME, "10.248.44.17", 8080);
		//webClient = new WebClient(BrowserVersion.CHROME);
		
		final DefaultCredentialsProvider credentialsProvider = (DefaultCredentialsProvider) webClient.getCredentialsProvider();
		
		
//		if(null != proxy){
//			if(proxy?.user &&  proxy?.pwd)
//			credentialsProvider.addCredentials(proxy?.user, proxy?.pwd,proxy.host, proxy.port, null	)
//			
//			
//			println("Setting credentials")
//			
//			
//		}
//		println("Setting OTL credentials")
		//println(otlcredentials)
		
		//setCredentials(webClient,otlcredentials)
		credentialsProvider.addCredentials("mnavaneethakrishnan", "April#2015","10.248.44.17", 8080, null	)
		
		//credentialsProvider.addCredentials("mnavaneethakrishnan@corpuk.net","April#2015","https://ebiz.uk.three.com", -1, null	)
		
		credentialsProvider.addCredentials("mnavaneethakrishnan@corpuk.net","April#2015","ebiz.uk.three.com", -1, null	)
		credentialsProvider.addCredentials("mnavaneethakrishnan@corpuk.net","April#2015","ebiz.uk.three.com", -1, null	)
		credentialsProvider.addCredentials("mnavaneethakrishnan@corpuk.net","April#2015","idmssop02.three.com", -1, null	)
		
		
		//credentialsProvider.addCredentials(otlcredentials.user,otlcredentials.pwd);
		webClient.setCredentialsProvider(credentialsProvider);
		
		//webClient.getWebWindowByName(selectedBrowser).getEnclosedPage()

		webClient.waitForBackgroundJavaScript(50000);
		//  webClient.getOptions().setThrowExceptionOnScriptError(false);
		//   webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
		webClient.getOptions().setCssEnabled(true);
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.setRefreshHandler(null)
	//	webClient.setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setPrintContentOnFailingStatusCode(true)
		webClient.getOptions().setRedirectEnabled(true);
		webClient.getOptions().setUseInsecureSSL(true);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false)
		//getOptions().isThrowExceptionOnFailingStatusCode()
		webClient.addWebWindowListener(curWebWindowListener);
		
		// webClient.getOptions().setTimeout(config.getMaxTimeoutMS());
		//webClient.set

		
		
		Page textpage=webClient.getPage("https://ebiz.uk.three.com/oa_servlets/AppsLogin")
		
		if(textpage.isHtmlPage())
			currentPage=textpage
		else{
			
			println("============")
			println(textpage.getUrl().toString())
			println("============")
			println(textpage.getWebResponse().contentAsString)
			println("============")
		}	
		
		print(textpage.dump()) 
		
		}catch(Exception e){
		
		e.printStackTrace();
		
		
		print
		//currentPage=webClient.getPage("https://ebiz.three.com/OA_HTML/OA.jsp?OAFunc=OAHOMEPAGE")
		
		}	
		
	}

	
	public login(String url){
		
		
		try{
			
			Page textpage=webClient.getPage(url)
			
			
			if(textpage.isHtmlPage()){
				currentPage=textpage
				println (currentPage.asXml())
				
			}else
				println(textpage.getUrl().toString())
				
				
			
			}catch(FailingHttpStatusCodeException ex){
			
			ex.printStackTrace();
				//login(curWebWindowListener.lastpage)
			}
		
		/*
		if(textpage.isHtmlPage()){
			
			println ("HTML page found")
			currentPage=textpage
			println( currentPage.asXml())
			
		}
		else{
			
			
			
			println(textpage.getUrl().toString())
			println("Attempting to go to login again")
			
			textpage=webClient.getPage("https://ebiz.three.com/OA_HTML/OA.jsp?OAFunc=OAHOMEPAGE")
			
			println("==After logging in ==========")
			if(textpage.isHtmlPage()){
				currentPage=textpage
				println (currentPage.asXml())
				
			}else
				println(textpage.getUrl().toString())
			
		}
		
		*/
		
	}
	
	def setCredentials(WebClient webClient,def otlcredentials)
	{
	
	  String base64encodedUsernameAndPassword = base64Encode(otlcredentials.user + ":" + otlcredentials.pwd);
	  webClient.addRequestHeader("Authorization", "Basic " + base64encodedUsernameAndPassword);
	}
  
	private static String base64Encode(String stringToEncode)
	{
	  return javax.xml.bind.DatatypeConverter.printBase64Binary(stringToEncode.getBytes());
	}
	

	def init(def webconfig){
		
	
		
		curWebWindowListener=new CurWebWindowListener()

		//println(proxy)

	
		def proxy=webconfig?.proxy
		def otlcredentials=webconfig?.otlcredentials
		def authsites=webconfig?.authsites
		
		if(null != proxy){

			println("Setting proxy")
			webClient = new WebClient(BrowserVersion.CHROME, proxy.host, proxy.port);

			//set proxy username and password
			//if(null != proxy?.user && null != proxy?.pwd ){
			
			
			
			
		
			//	}


		}else{
			webClient = new WebClient(BrowserVersion.CHROME);
		}
		
		
		final DefaultCredentialsProvider credentialsProvider = (DefaultCredentialsProvider) webClient.getCredentialsProvider();
		
		
		if(null != proxy){
			if(proxy?.user &&  proxy?.pwd)
			credentialsProvider.addCredentials(proxy?.user, proxy?.pwd,proxy.host, proxy.port, null	)
			//credentialsProvider.addCredentials("mnavaneethakrishnan", "April#2015","10.248.44.17", 8080, null	)
			
			//credentialsProvider.addCredentials("mnavaneethakrishnan@corpuk.net","April#2015","https://ebiz.uk.three.com", -1, null	)
			
			//credentialsProvider.addCredentials("mnavaneethakrishnan@corpuk.net","April#2015","ebiz.uk.three.com", -1, null	)			
			//credentialsProvider.addCredentials("mnavaneethakrishnan@corpuk.net","April#2015","idmssop02.three.com", -1, null	)
			
			
			
			println("Setting credentials")
			
			
		}
		println("Setting OTL credentials")
		//println(otlcredentials)
		
		//setCredentials(webClient,otlcredentials)
		//credentialsProvider.addCredentials(otlcredentials.user,otlcredentials.pwd,otlcredentials.host, -1, null	)

		
		for(def site:authsites){
			
			credentialsProvider.addCredentials(otlcredentials.user,otlcredentials.pwd,site, -1, null	)
			
		}
		
		
		
		
		webClient.setCredentialsProvider(credentialsProvider);
		
		//webClient.getWebWindowByName(selectedBrowser).getEnclosedPage()

		webClient.waitForBackgroundJavaScript(50000);
		//  webClient.getOptions().setThrowExceptionOnScriptError(false);
		//   webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
		webClient.getOptions().setCssEnabled(true);
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.setRefreshHandler(null)
	//	webClient.setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setPrintContentOnFailingStatusCode(true)
		webClient.getOptions().setRedirectEnabled(true);
		webClient.getOptions().setUseInsecureSSL(true);
		webClient.getOptions().setSSLClientProtocols(null)
		//webClient.getOptions().setThrowExceptionOnFailingStatusCode(false)
		//getOptions().isThrowExceptionOnFailingStatusCode()
		//webClient.setCookieManager(null)
		webClient.addWebWindowListener(curWebWindowListener);
		
		

	}

	HtmlElement getFirstElementByTag(String tag ){

		DomNodeList<HtmlElement> domNodeList= currentPage.getElementsByTagName(tag); //get a list of all table rows
		if(null == domNodeList || domNodeList.size() ==0)

			return null


		return  domNodeList[0]

	}

	def querySelector(def str){
		DomElement elem = null
		
		try{
			elem=currentPage.querySelector(str)
			Log.info("Element found ${elem}")
		}catch(Exception e)
		{
			Log.error("Exception found ${e}")
		}
		
		return elem
	}
	
	def querySelectorAll(def str){
		DomNodeList<HtmlElement> domNodeList = null
		
		try{
			domNodeList = currentPage.querySelectorAll(str)
			Log.info("Element found ${domNodeList}")
		}catch(Exception e)
		{
			Log.error("Exception found ${e}")
		}
		
		return domNodeList
	}
	
	
	def close(){

		webClient.closeAllWindows();

	}


	def findElemByName(String name){
		DomElement elem=null

		try{
			elem=currentPage.getElementByName(name)
			println("Element found ${elem}")
		}catch(Exception e){
			println("Exception found ${e}")
		}


		return  elem

	}

	ArrayList<HtmlElement> getElemsByTagClass(String tag ,String name){

		DomNodeList<HtmlElement> domNodeList= currentPage.getElementsByTagName(tag); //get a list of all table rows		
		ArrayList<HtmlElement> elems= new ArrayList<HtmlElement>();

		for (HtmlElement row : domNodeList)
		{
			String className = row.getAttribute("class");
			if (className.contains(name))
			{
				elems.add(row)
			}
		}


		return  elems

	}

	def findElemById(def name){
		return  currentPage.getElementById(name)

	}
	def clickOnInputId(def elemName){

		DomElement htmlElement=currentPage.getElementById(elemName)
		boolean flgSuccess=false;

		if(null !=htmlElement){
			((HtmlElement)htmlElement).click()
			flgSuccess=true
		}

		else
			Log.error("Cannot find element ${elemName} for click")


		return flgSuccess


	}

	def clickOnInputName(def elemName){

		DomElement htmlElement=null
		boolean flgSuccess=false;

		try{
			htmlElement=currentPage.getElementByName(elemName)


			if(null !=htmlElement){
				((HtmlElement)htmlElement).click()
				flgSuccess=true
			}

		}catch(Exception e){
			Log.error("Cannot find element ${elemName} for click")
		}






		return flgSuccess



	}

	def typeOnId(def elemName,def value){
		DomElement htmlInput=currentPage.getElementById(elemName)
		boolean flgSuccess=false;

		if(null !=htmlInput){
			((HtmlInput)htmlInput).setValueAttribute(value)
			flgSuccess=true
		}

		else
			Log.error("Cannot find element ${elemName}")


		return flgSuccess
	}

	def typeOnName(def elemName,def value){

		boolean flgSuccess=false;

		try{
			DomElement htmlInput=currentPage.getElementByName(elemName)


			if(null !=htmlInput){
				((HtmlInput)htmlInput).setValueAttribute(value)
				flgSuccess=true
			}

			else
				Log.error("Cannot find element ${elemName}")

		}catch(Exception e){

		}

		return flgSuccess


	}

	def typeOnFrameName(HtmlPage page,def elemName,def value){

		boolean flgSuccess=false;

		try{
			DomElement htmlInput=page.getElementByName(elemName)


			if(null !=htmlInput){
				((HtmlInput)htmlInput).setValueAttribute(value)
				flgSuccess=true
			}

			else
				Log.error("Cannot find element ${elemName}")

		}catch(Exception e){

		}

		return flgSuccess


	}

	def executeScriptforNewPageinFrame(HtmlPage page,String content){

		curWebWindowListener.pageChanged=false
		ScriptResult result= page.executeJavaScript(content);

		currentPage=result.getNewPage()


	}

	def executeScriptforNewPage(String content){

		curWebWindowListener.pageChanged=false
		ScriptResult result= currentPage.executeJavaScript(content);

		currentPage=result.getNewPage()


	}

	def clickLink(HtmlElement link){

		curWebWindowListener.pageChanged=false

		currentPage=link.click();


	}

	def executeScript(String content){

		try{
			currentPage.executeJavaScript(content);
		}catch(Exception e){
			e.printStackTrace();

		}



	}
	
	//TODO: Add a function that takes configurable wait period as an argument
	def waitForPageLoad(){

		while(!curWebWindowListener.pageChanged){
			//Have increased the thread wait period by *10 to address delay in network
			Thread.currentThread().wait(50000)
		}

		//currentPage=webClient.
		curWebWindowListener.pageChanged=false

	}
	def getContent(def elem){
		def res=""


		return res
	}

	def checkContentContains(def elem,def value){

		def res=false


		return res

	}
	def checkContentEquals(def elem,def value){

		def res=false


		return res

	}

	def NavigateInner(String url){

		curWebWindowListener.pageChanged=false
		executeScriptforNewPage "document.location='${url}'"





	}

	def setfirstFrameAsPage() {


		HtmlFrame frame=getFirstElementByTag("frame")


		if(null ==frame) {

			println("No Frame available response details");
			return false


		}


		currentPage=frame.getEnclosedPage();

		return true

	}

	def printAll(){

		Log.info(currentPage.asXml())
	}
	def Navigate(String url){

		println "Value of the URL to be parsed: " + url
		currentPage = webClient.getPage(url);



	}
}
