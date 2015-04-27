'''
Created on 26-Apr-2015

@author: muthuishere
'''
import sys
import json
import BrowserHelper
from pprint import pprint



if __name__ == '__main__':
    pass




#for arg in sys.argv:
#   print arg
#    print "=="




print sys.argv[1]

data = sys.argv[1]
data = data.replace("'", "\"");

scrapconfig = json.loads(data)

#"{'startdate':'21-Jan-15','enddate':'21-Jan-16','username':'mnavaneethakrishnan@corpuk.net','password':'April#2015','proxyhost':'10.248.44.17','proxyport':'8080','proxyuser':'mnavaneethakrishnan','proxypwd':'April#2015'}"


print( scrapconfig)
proxyobj = {'host':scrapconfig['proxyhost'],'port':scrapconfig['proxyport'],'user':scrapconfig['proxyuser'] , 'pwd': scrapconfig['proxypwd']}

BrowserHelper.init_browser( proxyobj )


# create dictionary for proxy

#BrowserHelper.addproxy(proxyobj)
authobj={'user':scrapconfig['username'] , 'pwd': scrapconfig['password']}
res = BrowserHelper.login("http://ebiz.uk.three.com/oa_servlets/AppsLogin",authobj)
print res
BrowserHelper.printresponse()
 
 #open browser
 
 #set proxy credentials
 
 #set Basic Auth
 
 #
