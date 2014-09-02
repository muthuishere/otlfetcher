package com.otl.reports.model

import com.gargoylesoftware.htmlunit.WebWindowEvent;
import com.gargoylesoftware.htmlunit.WebWindowListener
import com.gargoylesoftware.htmlunit.html.HtmlPage

class CurWebWindowListener implements WebWindowListener {

	def curpage=null
	def pageChanged=false
	
	void webWindowContentChanged(WebWindowEvent event) {
		System.out.println("webWindowContentChanged  Page : "+event.getNewPage().url);
		
		curpage=(HtmlPage)event.getNewPage();
//		   webClient.closeAllWindows();
		pageChanged=true
	}

	@Override
	public void webWindowOpened(WebWindowEvent event) {
		// TODO Auto-generated method stub
		System.out.println("webWindowOpened Page : "+event.dump());
		
	}

	@Override
	public void webWindowClosed(WebWindowEvent event) {
		// TODO Auto-generated method stub
		System.out.println("webWindowClosed Page : "+event.dump());
		
	}
}
