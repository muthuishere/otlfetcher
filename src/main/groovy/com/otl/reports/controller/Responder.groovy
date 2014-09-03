package com.otl.reports.controller

import com.otl.reports.beans.UserInfo

class Responder {

	DataManager dataManager=null
	
	public Responder(){
		dataManager=new DataManager()
		dataManager.init();
		
	}
	/**
	 * Create private constructor
	 */
	
	 
	
	
	public String updateuser(def params,String ip){
		
		StringBuffer response= new StringBuffer()
		
		response.append("<reply>")
		
		if(null == params.user || null == params.pwd ){
			
			response.append("<status code='1' error='true' description='Invalid user inputs'/>")
			
		}else{
		
		try{
		dataManager.addUserEntries(new UserInfo(
		
		user: params.user,
		password:params.pwd,
		ip: ip
		))
		
		response.append("<status code='0' error='false' description='Successfully updated user information'/>")
		}catch(Exception e){
		
		
		response.append("<status code='1' error='true' description='${e.getMessage()}'/>")
		}
	
		}
		response.append("</reply>")
		
		return response;
	}
	
	
}
