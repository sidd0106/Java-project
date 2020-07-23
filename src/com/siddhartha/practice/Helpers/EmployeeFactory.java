package com.siddhartha.practice.Helpers;

import com.siddhartha.practice.Models.Designation;
import com.siddhartha.practice.Models.Employee;
import com.siddhartha.practice.Models.Manager;
import com.siddhartha.practice.Models.TeamMember;

/**
 * Employee factory class to create interface to interact with Employee class without taking into consideration of the
 * type of employee.
 * @author sid
 */
public final class EmployeeFactory {

	/**
	 * Private constructor to avoid instance creation.
	 */
	private EmployeeFactory() {

	}

	/**
	 * Create instance of Employee according to type of employee required.
	 *
	 * @param designation
	 * 		to decide Type of Employee required.
	 * @return Instance of TeamMember or Manager class according to request.
	 */
	public static Employee getEmployee(Designation designation) {
		if (designation.equals(Designation.TeamMember)) {
			return new TeamMember();
		} else if (designation.equals(Designation.Manager)) {
			return new Manager();
		}
		return null;
	}

}
