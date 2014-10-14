package com.otl.reports.beans

import com.otl.reports.exceptions.ServiceException

import groovy.transform.ToString

@ToString
class TimesheetStatusReport {

	
	def user
	def startdate
	def enddate
	def team
	
	long totalhrs
	def status

	
	
}
