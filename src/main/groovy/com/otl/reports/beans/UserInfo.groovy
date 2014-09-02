package com.otl.reports.beans

import groovy.transform.ToString

@ToString
class UserInfo {

	def user
	def password
	def ip
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		UserInfo person = (UserInfo) o;

		if (user != null ? !user.equals(person.user) : person.user != null) return false;
		

		return true;
	}
}
