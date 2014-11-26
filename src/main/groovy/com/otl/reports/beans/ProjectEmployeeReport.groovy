package com.otl.reports.beans

import com.otl.reports.exceptions.ServiceException

import groovy.transform.ToString

//Class to hold the projectEmployee report and projectMnthly Report
@ToString
class ProjectEmployeeReport {

	Date entryDate
	def user
	def team
	def projectcode
	def projecttask
	ProjectInfo projectInfo;
	def tasktype
	def hours
	def month
	def totalhrs
	def status

	
	
}
