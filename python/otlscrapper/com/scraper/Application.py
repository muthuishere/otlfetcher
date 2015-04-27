'''
Created on 26-Apr-2015

@author: muthuishere
'''
import sys
import json
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

#"{'startdate':'21-Jan-15','enddate':'21-Jan-16','username':'xxx','password':'yyyy'}"


print  scrapconfig['startdate']
 
 
 #open browser
 
 #set proxy credentials
 
 #set Basic Auth
 
 #
