package com.otl.reports.controller

import java.text.SimpleDateFormat
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue

import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableBody
import com.otl.reports.beans.ProjectInfo
import com.otl.reports.beans.TimeEntry
import com.otl.reports.beans.UserInfo
import com.otl.reports.exceptions.ServiceException
import com.otl.reports.model.WebBrowser
import com.otl.reports.helpers.Log

import java.util.concurrent.TimeUnit

class DbImporter {

	DataManager datamanager=null

	public DbImporter(){

		datamanager=new DataManager();
		datamanager.init();

		(1..Configurator.globalconfig.db_worker_thread_count).each {
			Thread.start { worker_thread( Configurator,datamanager ) }
		}
	}
	def parseCallback(def dbreport_response){
		

		if(dbreport_response.error == null || dbreport_response.error == ""){
		
			return true	
		}
		else
		{
			return false
		}
		
		
	}


	def start(def importdb_location,def dboverride){


		Configurator.isUpdating=true
		Configurator.resetupdatestatus()


		//Thread.sleep(100000)


		println("Starting DB Updation")

		def callbackQueue = new LinkedBlockingQueue()
		def statusmsg="Fail"
		def description=""
		try{

			def k=null
			ArrayList lstdata
			def arrayofarraylstdata
			def msgcount=0
			def successCount=0

			// get db instantce() => dbinstance , tablename

			def dbinstance //=curdbobj.instance
			def tablename //=curdbobj.tablename

			//First backup up the database
			//Then acquire the db connection of the imported database.
			//Write a method to find the DB schema name from the database handle
			//Now find the list of table name and get the content of each table in arraylist. Possibly put this in a loop
			//Modify the importDB() to take arraylist of records, table name and the standard parameters(overwrite, encryption).
			//Once you have the db data stored in the arraylist then split the database(500 records) and start the importDB thread
			//Now you have to put these arraylist, table name and callbackqueue reference into  import_worker_lbq queue
			//Also update the import_worker_lbq worker thread to call the dbimporter function.
			//Finally keep track of the success count.
			//Update the html page to run the progress icon.


			def dbBackupResult = datamanager.backupCurrentDB()
			if(dbBackupResult == false){
				Log.info("Failed to back up database.")
			}

			def curdbobj=datamanager.getDBHandler(importdb_location)
			if(curdbobj != null){
				def response
				Log.info("Obtained DB connection of the uploaded file. Now we will proceed to import the database")

				curdbobj.rows("SELECT tbl_name FROM sqlite_master UNION SELECT tbl_name FROM sqlite_temp_master;").each{
					if( it.tbl_name == "timeentry" ){
						lstdata = null
						arrayofarraylstdata = null
						tablename = "timeentry"
						//TODO implement
						lstdata=datamanager.getDataFromDB(curdbobj,tablename)
						//TODO change split data into chunks of 500
						arrayofarraylstdata=lstdata.collate(100)
						for (def arraylstdata:arrayofarraylstdata){

							Configurator.import_worker_lbq.put([ "callbackQueue": callbackQueue, "arraylstdata": arraylstdata,"tablename":tablename, "dboverride": dboverride, "response":response])

							msgcount++

						}
					}
					else if(it.tbl_name == "projectdetails"){

						lstdata = null
						arrayofarraylstdata = null
						tablename = "projectdetails"

						lstdata=datamanager.getDataFromDB(curdbobj,tablename)

						arrayofarraylstdata=lstdata.collate(100)
						for (def arraylstdata:arrayofarraylstdata){

							Configurator.import_worker_lbq.put([ "callbackQueue": callbackQueue, "arraylstdata": arraylstdata,"tablename":tablename, "dboverride": dboverride, "response":response])

							msgcount++

						}
					}
					else if(it.tbl_name == "userInfo"){

						lstdata = null
						arrayofarraylstdata = null
						tablename = "userInfo"

						Log.info "value of lstdata" +  datamanager.getDataFromDB(curdbobj,tablename)
						lstdata=datamanager.getDataFromDB(curdbobj,tablename)
						arrayofarraylstdata=lstdata.collate(30)
						println "value of arrayofarraylstdata " + arrayofarraylstdata


						for (def arraylstdata:arrayofarraylstdata){

							Configurator.import_worker_lbq.put([ "callbackQueue": callbackQueue, "arraylstdata": arraylstdata,"tablename":tablename, "dboverride": dboverride, "response":response])

							msgcount++

						}
					}
					else{
						Log.error("Invalid Table name. Following table Name is not available in OTL schema." + tableName)
					}
				}


				(1..msgcount).each() {

					def dbreport_response = callbackQueue.poll(Configurator.globalconfig.fetch_req_timeout, TimeUnit.MILLISECONDS)

					if(parseCallback(dbreport_response))
						successCount++
				}


				if(successCount == msgcount){

					statusmsg="Success"
				}
				else if(successCount ==0){

					statusmsg="Failure"
				}else if(successCount < msgcount){

					statusmsg="Partial Success"
				}

				description="Imported records sucessfully for $successCount  out of $msgcount chuncked slices"

			}
			else{
				Log.error("Invalid or empty data connection. Might be a corrupted DB file uploaded for import")
			}
		}catch(Exception e){
			e.printStackTrace();
			description="Fail while parsing ${e.toString()}"
		}

		SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM dd yyyy hh:mm:ss a");
		Configurator.setupdatestatus( statusmsg, description,formatter.format(new Date()))
		Configurator.isUpdating=false
		Log.info("Completed DB Import")
	}






	//JsonSlurper reportapp =  new JsonSlurper()


	// The worker thread.  These threads listen for incoming requests
	// in the worker LinkedBlockingQueue, issue HTTP requests to the given
	// downstream systems and then post the HTTP reply to the temporary
	// LinkedBlockingQueue of the requester given in the request message.

	def worker_thread(curConfigurator, datamanager)
	{
		curConfigurator.log("Worker: Initialising")



		while(true)
		{
			def req_msg = curConfigurator.import_worker_lbq.take()
			def reply_msg = req_msg
			def start_ms = System.currentTimeMillis()
			String response = null
			ArrayList<TimeEntry> timeEntries=new ArrayList<TimeEntry>()
			try
			{

				def dboverride = req_msg.dboverride
				def arraylstdata=req_msg.arraylstdata
				def tablename=req_msg.tablename

				response = datamanager.importDBRecords(tablename, arraylstdata, dboverride)
				println "Value of the response message is : " + response
				reply_msg.response = response
				def took = System.currentTimeMillis() - start_ms
				if(!(response.contains("error")))
				{
					reply_msg.error=""
				}
				curConfigurator.log("Worker: Took: ${took} ms")
			}
			catch(Exception e)
			{
				e.printStackTrace()

				reply_msg.error = "${e.getCause().toString()} (${e.getMessage()})"

			}

			try
			{

				//Send back the item
				req_msg.callbackQueue.put(reply_msg)
			}
			catch(Exception e)
			{
				curConfigurator.log("Error sending response ${e.getMessage()}")
			}
		}
	}

}
