/**
 * 
 */
package com.otl.reports.controller

/**
 * @author hutchuk
 *
 */

import java.text.SimpleDateFormat
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue

import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableBody
import com.otl.reports.beans.JobDescription;
import com.otl.reports.beans.ProjectInfo
import com.otl.reports.beans.TimeEntry
import com.otl.reports.beans.UserInfo
import com.otl.reports.exceptions.ServiceException
import com.otl.reports.model.WebBrowser
import com.otl.reports.helpers.Log

import java.util.concurrent.TimeUnit

class JobScheduler {

	DataManager datamanager=null
	JobExecutor jobExecutor
	public JobScheduler(){

		datamanager=new DataManager()
		datamanager.init()
		jobExecutor = new JobExecutor()
		
		
		
		(1..Configurator.globalconfig.job_creator_thread_count).each {
			insertJobDescription(datamanager)//This part is just for test data generation. Can remove it later
			Thread.start { worker_thread( Configurator,datamanager ) }
		}
	}
	
	
	private def insertJobDescription(DataManager datamanager){
		//--------To be removed---
		Date today = new Date()
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(today)
		def from = calendar.getTime()
		
		Random rand = new Random();
		int randomNum = 0
		int max = 100
		int min = 1
		randomNum = rand.nextInt((max - min) + 1) + min;
		
		
		def JobDescription jobDescription = new JobDescription()
		
		//Mandate three fields for jobId creation
		
		jobDescription.jobOwner = "bsubramanian@corpuk.net"
		
		calendar.add(Calendar.DAY_OF_WEEK, -randomNum)
		from = calendar.getTime()
		Log.debug("Value of the random number :" + randomNum + ", new from date is + " + from)
		jobDescription.jobStartDate = from
		
		randomNum = rand.nextInt((max - min) + 1) + min;
		calendar.add(Calendar.DAY_OF_WEEK, randomNum)
		//calendar.setTime(today)//To be deleted. Only for testing
		def to = calendar.getTime()
		
		Log.debug("Value of the random number :" + randomNum + ", new from date is + " + from)
		jobDescription.jobValidity = "valid"
		
		int intFrequencyDesider = 0
		//intFrequencyDesider = (randomNum % 4)
		// 0 indicates adhoc
		// 1 indicates hourly
		// 2 indicates daily 
		// 3 indicates weekly
		
		
		switch (intFrequencyDesider) {
			case 0:
				jobDescription.jobName = "RandomAdhocJob" + randomNum
				jobDescription.jobFrequency="adhoc"
				jobDescription.jobReportFolder=".\\report\\adhoc"
				jobDescription.jobToAddress="bsubramanian"//"balaji.subramanian@three.co.uk"
			break;
			case 1:
				jobDescription.jobName = "RandomHourlyJob" + randomNum
				jobDescription.jobFrequency="hourly"
				jobDescription.jobReportFolder=".\\report\\hourly"
				jobDescription.jobToAddress="balaji.subramanian@three.co.uk"
			break;
			case 2:
				jobDescription.jobName = "RandomDailyJob" + randomNum
				jobDescription.jobFrequency="daily"
				jobDescription.jobReportFolder=".\\report\\daily"
				jobDescription.jobToAddress="balaji.subramanian@three.co.uk"
			break;
            case 3:  
				jobDescription.jobName = "RandomWeeklyJob" + randomNum
				jobDescription.jobFrequency="weekly"
				jobDescription.jobReportFolder=".\\report\\weekly"
				jobDescription.jobToAddress="balaji.subramanian@three.co.uk"
			break;
		}
		
		
		
		jobDescription.jobCCAddress = ""
		jobDescription.jobBCCAddress = ""
		jobDescription.jobSubject = "Automated Mail: Invalid OTL Fetcher Credentials"
		//jobDescription.jobSubject = "Automated Weekly Status Report"
		jobDescription.jobDesc = "Test Jobs"
		jobDescription.jobMailContent = " your OTL credentials are not updated. Please update in the following link http://01hw292906:2111"
		//jobDescription.jobMailContent = "Sample mail content for testing"
		jobDescription.jobRetry="5"
		jobDescription.jobEndDate = to
		jobDescription.jobLastCreatedInstTime = from
		jobDescription.jobTaskName = "fetchdb"//"uservalidity"
		
		
		datamanager.insertJobdescription(jobDescription, "disabled");
		//--------Only for test data population---
	} 
	
	def createJobInstances(JobDescription jobDescription){
		
		boolean jobInstanceCreated = false
		
		jobInstanceCreated = datamanager.createJobInstances(jobDescription)
		
		if(jobInstanceCreated){
			Log.debug("Job instance created successfully for parentjob : " + jobDescription.jobID)
			
		}else{
			//TODO -- Need to add code to update the status of the jobDescription that it failed.
		}
		
		
	}

	// The worker thread.  These threads listen for incoming requests
	// in the worker LinkedBlockingQueue, issue HTTP requests to the given
	// downstream systems and then post the HTTP reply to the temporary
	// LinkedBlockingQueue of the requester given in the request message.

	def worker_thread(curConfigurator, datamanager)
	{
		curConfigurator.log("Job Scheduler Thread: Initialising")

		JobDescription jobDescription = new JobDescription()
		

		while(true)
		{
			Log.info("Now few JOb Instances have to be created.")
			//Here the logic is to read the jobdescription table, then find the list of new child job instances to be created.
			
			def jobDescriptionList = datamanager.getJobDescription("")
			Log.debug("Number of list retrieved : " + jobDescriptionList.size())
			jobDescriptionList.each { desc ->
				if(desc.jobValidity.trim() == "valid"){
					//Here you have to check if the last job created time is less than Accepted JOB creation timing
					//If so then you have to create new jobs in the jobInstance and update the last updated job description time.
					jobDescription.jobName = desc.jobName
					jobDescription.jobDesc = desc.jobDesc
					jobDescription.jobValidity = desc.jobValidity
					jobDescription.jobStartDate = desc.jobStartDate
					jobDescription.jobEndDate = desc.jobEndDate
					jobDescription.jobFrequency = desc.jobFrequency
					jobDescription.jobRetry = desc.jobRetry
					jobDescription.jobReportFolder = desc.jobReportFolder
					jobDescription.jobToAddress = desc.jobToAddress
					jobDescription.jobCCAddress = desc.jobCCAddress
					jobDescription.jobBCCAddress = desc.jobBCCAddress
					jobDescription.jobSubject = desc.jobSubject
					jobDescription.jobMailContent = desc.jobMailContent
					jobDescription.jobOwner = desc.jobOwner
					jobDescription.jobLastCreatedInstTime = desc.jobLastCreatedInstTime
					jobDescription.jobTaskName = desc.jobTaskName
					
					createJobInstances(jobDescription)
					Log.info("Now few JOb Instances have to be created.")
				}
				
			}
			
			//Call to JobExecutor will be made. We are trying to read the jobs to be executed from database and move it to the LBQ
			Log.info("------------starting the job executor thread")
			jobExecutor.tasks_creator()
			Thread.sleep(500000)
			
		}
	}

}
