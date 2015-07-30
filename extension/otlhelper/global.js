

var globalOtlFetcher = {
	doc : null,
	initialized : false,
	

	addAutoLoginInfo : function (autoLoginInfo) {

		var rawxml = ""

			if (localStorage["autologinxml"] == undefined || localStorage["autologinxml"] == "")
				rawxml = "<root></root>" else
						rawxml = Helper.decrypt(localStorage["autologinxml"])

							//console.log("Start addAutoLoginInfo with XML " + rawxml)

							//Set Autologin List first
							var parser = new DOMParser();
					var docxml = parser.parseFromString(rawxml, "text/xml");
			globalOtlFetcher.updateResponse(docxml)

			//Remove from Autologin Object Autologin List first
			var removeResponse = globalOtlFetcher.removeSite(autoLoginInfo)

			//Site removed , Update XML
			if (removeResponse == true) {

				var oSerializer = new XMLSerializer();
				rawxml = oSerializer.serializeToString(globalOtlFetcher.autologinXMLList);
				// console.log("After removal" + rawxml)

			}

			rawxml = rawxml.replace("</root>", autoLoginInfo + "</root>");

		localStorage["autologinxml"] = Helper.encrypt(rawxml);
		globalOtlFetcher.loadDoc();

	},
	ismatchURL : function (currentURL, elemname) {

		docxml = globalOtlFetcher.autologinXMLList;

		try {

			var divs = docxml.getElementsByTagName("site"),
			i = divs.length;

			if (i == 0)
				return false;
			while (i--) {

				iurl = globalOtlFetcher.getXMLElementval(divs[i], elemname);

				if (Utils.getdomainName(currentURL) == Utils.getdomainName(iurl)) {
					//alert(divs[i].url)
					return divs[i];
				}

			}

		} catch (exception) {

			console.log("Issue" + exception)

		}

		return false;

	},

	getXmlObjectForPage : function (currentURL) {

		if (globalOtlFetcher.autologinList == null) {
			globalOtlFetcher.logmessage("autologinList null");
			return false;
		}

		var flgReturn = globalOtlFetcher.ismatchURL(currentURL, "loginurl");
		return flgReturn;

	},

	startsWith : function (data, str) {
		return !data.indexOf(str);
	},

	jq : function (formulae) {
		$mb = jQuery.noConflict();
		return $mb(formulae, document);
	},
	hasalreadyloggedin : function (formulae) {

		if (formulae == "")
			return false;

		try {
			var elemhtml = globalOtlFetcher.jq(formulae);

			if (null != elemhtml.html())
				return true;

		} catch (exception) {}

		return false;

	},
	canSubmit : function (curlocation) {

		var curdomainName = Utils.getdomainName(curlocation)
			var curTimeinMs = Date.now()

			var timedifference = curTimeinMs - globalOtlFetcher.lastloggedInTimeinMilliseconds
			var MAX_ALLOWED_TIME_DIFFERENCE = 60 * 1000

			if (null != globalOtlFetcher.lastloggedInDomain && curdomainName == globalOtlFetcher.lastloggedInDomain && timedifference < MAX_ALLOWED_TIME_DIFFERENCE) {
				globalOtlFetcher.blacklistDomains.push(curdomainName)
				return false
			}
			return true

	},
	updateSuccessLogin : function (curlocation) {
		var curdomainName = Utils.getdomainName(curlocation)
			var curTimeinMs = Date.now()
			globalOtlFetcher.lastloggedInDomain = curdomainName
			globalOtlFetcher.lastloggedInTimeinMilliseconds = curTimeinMs;

		//console.log("Updating Success Login for domain" + globalOtlFetcher.lastloggedInDomain + " at time" + globalOtlFetcher.lastloggedInTimeinMilliseconds)


	},

	// returns
	//0 - If Script can be injected
	//-1 - If URL is blacklisted
	//1- for remaining status
	canInjectURL : function (curlocation) {

		if (globalOtlFetcher.autologinList == null) {

			return 1;
		}

		var curdomainName = Utils.getdomainName(curlocation)
			var curXMLObject = globalOtlFetcher.getXmlObjectForPage(curlocation)

			var flgAutologinEnabled = true;

		if (curXMLObject != false) {
			var enabledautologinValue = globalOtlFetcher.getXMLElementval(curXMLObject, "enabled");

			if (null == enabledautologinValue || "" == enabledautologinValue || enabledautologinValue == "true")
				flgAutologinEnabled = true else
						flgAutologinEnabled = false
		}
		if (curXMLObject != false && flgAutologinEnabled == true && globalOtlFetcher.blacklistDomains.indexOf(curdomainName) == -1) {

			return 0;

		}
		if (globalOtlFetcher.blacklistDomains.indexOf(curdomainName) != -1) {

			console.log("Blacklisted domain" + curdomainName)
			return -1;

		}

		if (flgAutologinEnabled == false) {

			console.log("Disabled domain" + curdomainName)
			return -1;
		}

		return 1;

	},

	logmessage : function (aMessage) {

		//  alert(aMessage)

		//console.log(aMessage)


	},

	removeSite : function (autologinRawXML) {

		var parser = new DOMParser();
		var autologinObject = parser.parseFromString(autologinRawXML, "text/xml");

		var currentURL = globalOtlFetcher.getXMLElementval(autologinObject, "loginurl")

			try {

				var divs = globalOtlFetcher.autologinXMLList.getElementsByTagName("site"),
				i = divs.length;

				if (i == 0)
					return false;
				while (i--) {

					iurl = globalOtlFetcher.getXMLElementval(divs[i], "loginurl");

					if (Utils.getdomainName(currentURL) == Utils.getdomainName(iurl)) {
						//alert(divs[i].url)
						// return divs[i];

						//remove site
						divs[i].parentNode.removeChild(divs[i]);

						return true;

					}

				}

			} catch (exception) {

				console.log("Issue" + exception)

			}

			return false;

	},

	getXMLElementval : function (node, elemName) {

		try {
			val = node.getElementsByTagName(elemName)[0].firstChild.nodeValue;
			return val
		} catch (exception) {
			return "";
		}
	},
	updateResponse : function (docxml) {

		var dummyresp = '';

		globalOtlFetcher.autologinXMLList = docxml;
		var jsonresp = new Array();

		try {

			globalOtlFetcher.logmessage(docxml);
			var divs = docxml.getElementsByTagName("site"),
			i = divs.length;
			//globalOtlFetcher.logmessage("getResposnseasJSON" + i );
			if (i == 0)
				return null;
			while (i--) {

				var partner = {};
				partner.url = globalOtlFetcher.getXMLElementval(divs[i], "url");

				partner.loginurl = globalOtlFetcher.getXMLElementval(divs[i], "loginurl");
				partner.username = globalOtlFetcher.getXMLElementval(divs[i], "username");

				partner.password = globalOtlFetcher.getXMLElementval(divs[i], "password");
				partner.userelement = globalOtlFetcher.getXMLElementval(divs[i], "userelement");
				partner.pwdelement = globalOtlFetcher.getXMLElementval(divs[i], "pwdelement");

				partner.btnelement = globalOtlFetcher.getXMLElementval(divs[i], "btnelement");
				partner.enabled = globalOtlFetcher.getXMLElementval(divs[i], "enabled");
				partner.formelement = globalOtlFetcher.getXMLElementval(divs[i], "formelement");

				jsonresp.push(partner);

			}

			dummyresp = JSON.stringify(jsonresp);

			globalOtlFetcher.autologinList = dummyresp;

			//globalOtlFetcher.logmessage(dummyresp);

			return true;
		} catch (exception) {

			console.log("decode issue" + exception)
			return null;
		}

	},
	loadXMLDoc : function (dname) {

		xhttp = new XMLHttpRequest();

		xhttp.open("GET", dname, false);
		xhttp.send();

		localStorage["autologinxml"] = Helper.encrypt(xhttp.responseText);
		globalOtlFetcher.loadDoc();
	},
	loadDoc : function () {

		if (localStorage["autologinxml"] == undefined || localStorage["autologinxml"] == "")
			return;

		var rawxml = Helper.decrypt(localStorage["autologinxml"]);

		var parser = new DOMParser();
		var docxml = parser.parseFromString(rawxml, "text/xml");

		globalOtlFetcher.updateResponse(docxml)

	},
	try_count : 0,
	retrieveCredentials : function (status) {
		var url = status.url;
		console.log(url)

		if (url.indexOf("idmssop02.three.com")) {

			var credential = {};
			credential.username = "mnavaneethakrishnan@corpuk.net"
				credential.password = "July#2015"

				if (status.requestId == last_request_id && status.tabId == last_tab_id) {
					++globalOtlFetcher.try_count;
				} else {
					globalOtlFetcher.try_count = 0;
				}

				if (globalOtlFetcher.try_count < 2) {

					console.log("try_count" + globalOtlFetcher.try_count)
					last_request_id = status.requestId;
					last_tab_id = status.tabId;

					//cb(" ", success_color, credential, status.tabId);
					console.log("sent credentials" + url)
					return {
						authCredentials : {
							username : credential.username,
							password : credential.password
						}
					};

				} else {

					console.log("try_count exceeded")
				}
		}

		return {};
	},

	
	initExtension : function () {
		chrome.webRequest.onAuthRequired.addListener(globalOtlFetcher.retrieveCredentials, {
			urls : ["<all_urls>"]
		}, ["blocking"]);

	},
	initOldExtension : function () {

		//validate and set logged in
		var credential = localStorage["credential"]
			var promptrequired = localStorage["promptrequired"]

			if (undefined == credential || null == credential || undefined == promptrequired || null == promptrequired || promptrequired === 'false')
				globalOtlFetcher.loggedIn = true else
						globalOtlFetcher.loggedIn = false

							console.log("globalOtlFetcher.loggedIn" + globalOtlFetcher.loggedIn);

					globalOtlFetcher.loadDoc()

	}

};

var Utils = {

	getdomainName : function (str) {
		var a = document.createElement('a');
		a.href = str;
		return a.hostname
	}

};

var Messenger = {
	address : "127"
	getcredential : function () {},
	onCommandReceived : function () {},
	sendCommand : function (cmd) {}

}
var PageHandler = {

	gotopage : function (url) {},
	injectScript:function(script){
	
	//current tab inject script
	
	},
	getelement:function(queryselector){
	
	//current tab inject script
	
	},
	

};

//globalOtlFetcher.loadXMLDoc(chrome.extension.getURL('autologin.xml'))
globalOtlFetcher.initExtension()

chrome.tabs.onUpdated.addListener(function (tabId, changeInfo, tab) {

	/*
	if(tab.url !== undefined && changeInfo.status == "complete" ){

	var status=globalOtlFetcher.canInjectURL(tab.url)
	if(  status == 0) {

	if(globalOtlFetcher.loggedIn==false){



	chrome.tabs.executeScript(tabId, {file:"scripts/autoLoginCredentials.js"}, function() {
	//script injected
	});



	}else{
	chrome.tabs.executeScript(tabId, {file:"scripts/autoLogin.js"}, function() {
	//script injected
	});
	}
	}else if( status == 1) {

	//console.log("got complete")
	var jscode='var extnid="'+ chrome.extension.getURL("/") + '"';


	chrome.tabs.executeScript(tabId, {code:jscode,allFrames :false}, function() {
	//script injected
	chrome.tabs.executeScript(tabId, {file:"scripts/autoLoginCapture.js"}, function() {
	//script injected
	//	console.log("got autoLoginCapture" +tabId)
	});

	});



	}
	}

	 */
});

chrome.runtime.onMessage.addListener(
	function (request, sender, sendResponse) {
	// console.log(sender.tab ?
	// "from a content script:" + sender.tab.url :
	// "from the extension");


	if (request.action == "captureautologin") {

		PageActionHandler.setCaptureReady(sender.tab)
		chrome.pageAction.show(sender.tab.id);
		// Return nothing to let the connection be cleaned up.
		sendResponse({});

	} else if (request.action == "getData") {

		var rawxml = Helper.decrypt(localStorage["autologinxml"]);

		sendResponse({
			"xml" : rawxml
		});

	} else if (request.action == "injectAutoLogin") {

		chrome.tabs.executeScript(sender.tab.id, {
			file : "scripts/autoLogin.js"
		}, function () {
			//script injected
		});

		sendResponse({
			"valid" : true
		});

	} else if (request.action == "validateCredential") {

		var userCredential = request.info;

		var savedCredential = Helper.decrypt(localStorage["credential"]);

		if (userCredential == savedCredential) {

			globalOtlFetcher.loggedIn = true

				sendResponse({
					"valid" : true
				});
		} else {
			globalOtlFetcher.loggedIn = false;
			sendResponse({
				"valid" : false
			});
		}

	} else if (request.action == "addCredential") {

		var credential = request.info;

		localStorage["credential"] = Helper.encrypt(credential);

		sendResponse({
			"valid" : true
		});

	} else if (request.action == "updateCredential") {

		var credential = request.currentCredential;

		var newCredential = request.newCredential;

		var savedCredential = Helper.decrypt(localStorage["credential"]);

		if (credential == savedCredential) {
			localStorage["credential"] = Helper.encrypt(newCredential);
			sendResponse({
				"valid" : "true"
			});
		} else
			sendResponse({
				"valid" : "false"
			});

	} else if (request.action == "getPromptAtStartup") {

		promptrequired = localStorage["promptrequired"]

			sendResponse({
				"promptrequired" : (promptrequired === 'true')
			});

	} else if (request.action == "updatePromptAtStartup") {

		localStorage["promptrequired"] = request.promptrequired;

		sendResponse({
			"valid" : true
		});

	} else if (request.action == "hasCredential") {

		var savedCredential = Helper.decrypt(localStorage["credential"]);

		var result = (savedCredential != "")

		sendResponse({
			"valid" : result
		});

	} else if (request.action == "refreshData") {

		globalOtlFetcher.loadDoc()

		sendResponse({});

	} else if (request.action == "cansubmit") {

		var flgResponse = globalOtlFetcher.canSubmit(sender.tab.url)

			if (flgResponse == true)
				globalOtlFetcher.updateSuccessLogin(sender.tab.url)

				sendResponse({
					actionresponse : flgResponse
				});

	} else if (request.action == "addAutoLoginInfo") {

		globalOtlFetcher.addAutoLoginInfo(request.info)

		sendResponse({});

	} else if (request.action == "success") {

		globalOtlFetcher.updateSuccessLogin(sender.tab.url)
		sendResponse({
			actionresponse : "success"
		});
	}

});

/*
// Called when the user clicks on the page action.
chrome.pageAction.onClicked.addListener(function(tab) {

PageActionHandler.handleClick(tab);

});

*/
