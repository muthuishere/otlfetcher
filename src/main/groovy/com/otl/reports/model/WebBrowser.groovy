package com.otl.reports.model

import com.gargoylesoftware.htmlunit.ScriptResult
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.WebWindowEvent
import com.gargoylesoftware.htmlunit.WebWindowListener
import com.gargoylesoftware.htmlunit.html.DomElement
import com.gargoylesoftware.htmlunit.html.DomNodeList
import com.gargoylesoftware.htmlunit.html.HtmlElement
import com.gargoylesoftware.htmlunit.html.HtmlInput
import com.gargoylesoftware.htmlunit.html.HtmlPage
import org.apache.commons.lang3.StringUtils
import org.codehaus.groovy.control.CompilerConfiguration
import com.gargoylesoftware.htmlunit.BrowserVersion
import com.otl.reports.helpers.BrowserHelper
import com.otl.reports.helpers.Log
class WebBrowser {

	WebClient webClient = null;
	CurWebWindowListener curWebWindowListener=null;


	//Browser the user wishes to run against
	def String selectedBrowser

	//The Navigator item which is currently active, by active we mean it was the last item returned from the browser which is a NonEmptyNavigator
	HtmlPage  currentPage = null




	def init(String selectedBrowser){

		curWebWindowListener=new CurWebWindowListener()

		webClient = new WebClient(BrowserVersion.CHROME);

		//webClient.getWebWindowByName(selectedBrowser).getEnclosedPage()

		webClient.waitForBackgroundJavaScript(50000);
		//  webClient.getOptions().setThrowExceptionOnScriptError(false);
		//   webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
		webClient.getOptions().setCssEnabled(true);
		webClient.getOptions().setJavaScriptEnabled(true);
		// webClient.getOptions().setTimeout(config.getMaxTimeoutMS());
		//webClient.set


		webClient.addWebWindowListener(curWebWindowListener);


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

		currentPage.executeJavaScript(content);


	}

	def waitForPageLoad(){

		while(!curWebWindowListener.pageChanged){
			Thread.currentThread().wait(5000)
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

	def printAll(){

		Log.info(currentPage.asXml())
	}
	def Navigate(String url){

		currentPage = webClient.getPage(url);



	}
}