package com.db.awmd.challenge.exception;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorTo {

	@JsonProperty
	private int errorCode;

	@JsonProperty
	private String errorMessage;

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + errorCode;
		result = prime * result + ((errorMessage == null) ? 0 : errorMessage.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ErrorTo other = (ErrorTo) obj;
		if (errorCode != other.errorCode)
			return false;
		if (errorMessage == null) {
			if (other.errorMessage != null)
				return false;
		} else if (!errorMessage.equals(other.errorMessage))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ErrorTo [errorCode=" + errorCode + ", errorMessage=" + errorMessage + "]";
	}

	@JsonCreator
	public ErrorTo(
			@JsonProperty("errorCode") int errorCode,
			@JsonProperty("errorMessage") String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}


	public ErrorTo() {
		super();
	}

}
