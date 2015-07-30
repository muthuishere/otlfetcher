import com.gargoylesoftware.htmlunit.*
import com.gargoylesoftware.htmlunit.html.*
import com.gargoylesoftware.htmlunit.util.*

import  net.sourceforge.htmlunit.corejs.javascript.*
import org.apache.xml.utils.*
import XTrustProvider;
import io.netty.handler.codec.http.DefaultHttpRequest
import io.netty.handler.codec.http.HttpObject
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.HttpResponse
import org.littleshoot.proxy.HttpFilters
import org.littleshoot.proxy.HttpFiltersAdapter
import org.littleshoot.proxy.HttpFiltersSourceAdapter
import org.littleshoot.proxy.HttpProxyServer
import org.littleshoot.proxy.impl.DefaultHttpProxyServer
import org.apache.commons.codec.binary.Base64;


/*
Class clazz =...;
StringBuffer results = new StringBuffer();

ClassLoader cl = clazz.getClassLoader();
results.append("\n" + clazz.getName() + "(" + 
               Integer.toHexString(clazz.hashCode()) + ").ClassLoader=" + cl);
ClassLoader parent = cl;

while (parent != null) {
    results.append("\n.."+parent);
    URL[] urls = getClassLoaderURLs(parent);

    int length = urls != null ? urls.length : 0;
    for(int u = 0; u < length; u ++) {
 results.append("\n...."+urls[u]);
    }

    if (showParentClassLoaders == false) {
 break;
    }
    if (parent != null) {
 parent = parent.getParent();
    }
}

CodeSource clazzCS = clazz.getProtectionDomain().getCodeSource();
if (clazzCS != null) {
    results.append("\n++++CodeSource: "+clazzCS);
} else {
    results.append("\n++++Null CodeSource");
}

*/



 /*
 webClient.setCssEnabled(false);
webClient.setIncorrectnessListener(new IncorrectnessListener() {
    @Override
    public void notify(String arg0, Object arg1) {
        // ...
    }
});
webClient.setCssErrorHandler(new ErrorHandler() {
    @Override
    public void warning(CSSParseException exception) throws CSSException {
        // ...
    }
    @Override
    public void fatalError(CSSParseException exception) throws CSSException {
        // ...
    }
    @Override
    public void error(CSSParseException exception) throws CSSException {
        // ...
    }
});
webClient.setJavaScriptErrorListener(new JavaScriptErrorListener() {
 
    @Override
    public void timeoutError(HtmlPage arg0, long arg1, long arg2) {
        // ...
    }
    @Override
    public void scriptException(HtmlPage arg0, ScriptException arg1) {
        // ...
    }
    @Override
    public void malformedScriptURL(HtmlPage arg0, String arg1, MalformedURLException arg2) {
        // ...
    }
 
    @Override
    public void loadScriptError(HtmlPage arg0, URL arg1, Exception arg2) {
        // ...
    }
});
webClient.setHTMLParserListener(new HTMLParserListener() {
    @Override
    public void warning(String arg0, URL arg1, int arg2, int arg3, String arg4) {
        // ...
    }
    @Override
    public void error(String arg0, URL arg1, int arg2, int arg3, String arg4) {
        // ...
    }
});

*/

String buildBasicAuthorizationString(String username, String password) {

    String credentials = username + ":" + password;
    return "Basic " + new String(Base64.encodeBase64(credentials.getBytes()));
}

  System.setProperty("basicAuth", buildBasicAuthorizationString("mnavaneethakrishnan@corpuk.net", "July#2015"));
  
			XTrustProvider.install();
			
			
// LittleProxy setup
def proxyPort = 8888
HttpProxyServer server = DefaultHttpProxyServer.bootstrap()
    .withPort(proxyPort)
    .withFiltersSource(
    new HttpFiltersSourceAdapter() {
        @Override
        HttpFilters filterRequest(HttpRequest originalRequest) {
            return new HttpFiltersAdapter(originalRequest) {
                @Override
                HttpResponse requestPre(HttpObject httpObject) {
                    if (httpObject instanceof DefaultHttpRequest) {
                        if (httpObject.getUri().startsWith(baseUrl)) {
                            if (httpObject.headers().contains("Authorization")) {
                                println "Already contains the Authorization header: " + httpObject.getUri()
                            } else {
                                println "Adding Authorization header to request: " + httpObject.getUri()
                                httpObject.headers().add("Authorization",  System.getProperty("basicAuth"))
                            }
                        } else {
                            println "Ignoring request: " + httpObject.getUri()
                        }
                    } else {
                        println "Ignoring event: " + httpObject
                    }
                    return null
                }
            }
        }
    })
    .start();
	
	
			  BrowserVersion bv = BrowserVersion.CHROME;
        bv.setUserAgent("Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36");
       WebClient webClient = new WebClient(bv, "127.0.0.1", proxyPort);
		
				//WebClient webClient = new WebClient(BrowserVersion.CHROME);

webClient.getOptions().setThrowExceptionOnScriptError(false);
webClient.getOptions().setPrintContentOnFailingStatusCode(false)

   //DefaultCredentialsProvider userCredentials = new DefaultCredentialsProvider();
    //userCredentials.addCredentials("mnavaneethakrishnan@corpuk.net", "July#2015");
    //webClient.setCredentialsProvider(userCredentials);
	webClient.getOptions().setTimeout(240000);
	 
	// oracle.uix=0^^GMT+5:30^p
	 
	 CookieManager cookieManager = webClient.getCookieManager();
    cookieManager.setCookiesEnabled(true);

	
    Cookie cookie = new Cookie("ebiz.three.com", "oracle.uix", "0^^GMT+5:30^p");

    cookieManager.addCookie(cookie);
    webClient.setCookieManager(cookieManager);
	
	
		
//final HtmlPage page = webClient.getPage("https://ebiz.three.com/OA_HTML/AppsLogin");
 final HtmlPage page = webClient.getPage("http://localhost:9999/redirect.php");
 
 println page.asXml()
 
 /*
def printClassPath(classLoader) {
  println "$classLoader"
  classLoader.getURLs().each {url->
     println "- ${url.toString()}"
  }
  if (classLoader.parent) {
     printClassPath(classLoader.parent)
  }
}
printClassPath this.class.classLoader

println "hi"

*/