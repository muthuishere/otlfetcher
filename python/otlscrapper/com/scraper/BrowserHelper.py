'''
Created on 26-Apr-2015

@author: muthuishere
'''
import mechanize
import cookielib
import httplib
import logging
import sys
from imaplib import Response_code



br = None 
webresponse = None

def init_browser( proxyobj ):
    # Browser
    global br
    br = mechanize.Browser()
    print proxyobj
    proxystring =   proxyobj['host'] + proxyobj['port']
    #br.set_proxies({"http": proxystring })
        # Add HTTP Basic/Digest auth username and password for HTTP proxy access.
            # (equivalent to using "joe:password@..." form above)
 #   br.add_proxy_password(proxyobj['user'], proxyobj['pwd'])

    # Cookie Jar
    cj = cookielib.LWPCookieJar()
    br.set_cookiejar(cj)
    logger = logging.getLogger("mechanize")
    logger.addHandler(logging.StreamHandler(sys.stdout))
    logger.setLevel(logging.INFO)
    
    # Browser options
    br.set_handle_equiv(True)
    br.set_handle_gzip(True)
    br.set_handle_redirect(True)
    br.set_handle_referer(True)
    br.set_handle_robots(False)
    
    # Follows refresh 0 but not hangs on refresh > 0
    br.set_handle_refresh(mechanize._http.HTTPRefreshProcessor(), max_time=1)
    
    # Want debugging messages?
    br.set_debug_http(True)
    br.set_debug_redirects(True)
    br.set_debug_responses(True)
    br.set_handle_gzip(True)
    
 
    # User-Agent (this is cheating, ok?)
    br.addheaders = [('User-agent', 'Mozilla/5.0 (Windows NT 6.3; rv:36.0) Gecko/20100101 Firefox/36.0')]
    
def addproxy( proxyobj ):
    global br
    
    proxystring =   proxyobj['host'] + proxyobj['port']
    br.set_proxies({"http": proxystring })
        # Add HTTP Basic/Digest auth username and password for HTTP proxy access.
            # (equivalent to using "joe:password@..." form above)
#    br.add_proxy_password(proxyobj['user'], proxyobj['pwd'])

def login( url ,auth = None):
    resp = navigate(url,auth)
#     if( resp ):
#         print "connect successfully"
#     else:
#          resp = navigate("http://ebiz.uk.three.com/",auth)
    return resp         
            
def navigate( url ,auth = None):
    # goto url
    global br  
    global webresponse
    
    webresponse = None
    if( auth ):
       br.add_password( url , auth['user'],  auth['pwd'])
       br.add_password( 'https://idmssop02.three.com' , auth['user'],  auth['pwd'])
       br.add_password( 'https://ebiz.uk.three.com' , auth['user'],  auth['pwd'])  
       
       print("Added Authentication")
        
    try:
            
        #webresponse = mechanize.urlopen( url )             
        br.open( url ) 
        webresponse = br.response()
        return webresponse
    
    except httplib.BadStatusLine:
      #  print( br )
        return None    

def printresponse():
    global webresponse
    print( webresponse)
    
def query( xpath ):
    # goto url
    global br
    br = mechanize.Browser()        
    
def executescript(scriptcontent):
    global br
    
def clickelement( elempath ):        
    global br
    
def typeval( elempath , value):
    global br    