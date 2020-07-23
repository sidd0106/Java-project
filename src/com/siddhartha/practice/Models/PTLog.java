package com.siddhartha.practice.Models;

import java.sql.Date;

/**
 * Stores PT log data of a Employee.
 *
 * @author sid
 */
public class PTLog {
	private int id;
	private int employeeId;
	private String details;

	public PTLog() {
	}

	public PTLog(int id, int employeeId, String details, Date loggedDate) {
		this.id = id;
		this.employeeId = employeeId;
		this.details = details;
		this.loggedDate = loggedDate;
	}

	private Date loggedDate;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public Date getLoggedDate() {
		return loggedDate;
	}

	public void setLoggedDate(Date loggedDate) {
		this.loggedDate = loggedDate;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append("PTLog{")
				.append("id=").append(id)
				.append(", employeeId=")
				.append(employeeId)
				.append(", details=")
				.append(details)
				.append(", loggedDate=")
				.append(loggedDate)
				.append('}')
				.toString();
	}
}
