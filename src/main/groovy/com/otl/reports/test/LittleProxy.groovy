package com.otl.reports.test

import com.gargoylesoftware.htmlunit.*
import com.gargoylesoftware.htmlunit.html.*
import com.gargoylesoftware.htmlunit.util.*

import  net.sourceforge.htmlunit.corejs.javascript.*
import org.apache.xml.utils.*
import org.littleshoot.proxy.impl.DefaultHttpProxyServer
import com.otl.reports.helpers.XTrustProvider;

class LittleProxy {
	
	static setupproxy(int port){
		
	def	proxyServer = new DefaultHttpProxyServer(port, new HttpResponseFilters() {
			public HttpFilter getFilter(String hostAndPort) {
				return null;
			}
		}, new ChainProxyManager() {
			public void onCommunicationError(String hostAndPort) {
				// TODO Auto-generated method stub
			}
			public String getChainProxy(HttpRequest httpRequest) {
				return "proxy.mycompany.org:3128";
			}
		}, null, new HttpRequestFilter() {
			
			public void filter(HttpRequest httpRequest) {
				httpRequest.addHeader("Proxy-Authorization", "Basic XYZ1234==");
			}
		});
	
	}

	static apptest(){
		
		XTrustProvider.install();
		
		  BrowserVersion bv = BrowserVersion.CHROME;
	bv.setUserAgent("Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36");
  // WebClient webClient = new WebClient(bv, "127.0.0.1", 8888);
	WebClient webClient = new WebClient(bv, "10.248.44.17", 8080);
	//WebClient webClient = new WebClient(bv);
	
			//WebClient webClient = new WebClient(BrowserVersion.CHROME);

webClient.getOptions().setThrowExceptionOnScriptError(false);
webClient.getOptions().setPrintContentOnFailingStatusCode(false)

DefaultCredentialsProvider userCredentials = new DefaultCredentialsProvider();
userCredentials.addCredentials("mnavaneethakrishnan@corpuk.net", "July#2015");
webClient.setCredentialsProvider(userCredentials);
webClient.getOptions().setTimeout(240000);
 
// oracle.uix=0^^GMT+5:30^p
 
 CookieManager cookieManager = webClient.getCookieManager();
cookieManager.setCookiesEnabled(true);


Cookie cookie = new Cookie("ebiz.three.com", "oracle.uix", "0^^GMT+5:30^p");

cookieManager.addCookie(cookie);
webClient.setCookieManager(cookieManager);





final HtmlPage page = webClient.getPage("https://ebiz.three.com/OA_HTML/AppsLogin");
// final HtmlPage page = webClient.getPage("http://localhost:9999/redirect.php");

println page.asXml()

	}
	static main(args) {
		
		apptest();
	}
}
