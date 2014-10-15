package com.otl.reports.beans

import groovy.transform.ToString

@ToString
class ProjectInfo {

	def name
	def code
	def projectid
	
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ProjectInfo projectInfo = (ProjectInfo) o;

		if (code != null ? !code.equals(projectInfo.code) : projectInfo.code != null) return false;
		

		return true;
	}
}
