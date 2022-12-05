package com.ventura.panverification.model;

public enum NsdlApiErrorCodeEnum {

	E1("SUCCESS"),
	E2("SystemError"),
	E3("Authentication Failure"),
	E4("User not authorized"),
	E5("No PANs Entered"),
	E6("User validity has expired"),
	E7("Number of PANs exceeds the limit (5)"),
	E8("Not enough balance"),
	E9("Not an HTTPs request"),
	E10("POST method not used"),
	E11("SLAB_CHANGE_RUNNING"),
	E12("Invalid version number entered");

	private String errorDescription;
	
	NsdlApiErrorCodeEnum(String errorDescription) {
		this.errorDescription = errorDescription;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}
	
}
