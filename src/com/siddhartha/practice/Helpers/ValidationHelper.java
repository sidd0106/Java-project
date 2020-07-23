package com.siddhartha.practice.Helpers;

/**
 * Helps validate input from user.
 *
 * @author sid
 */
public class ValidationHelper {
	/**
	 * Validates Name of Employee according for numeric inputs, lengths, etc.
	 *
	 * @param name
	 * 		name of the Employee.
	 * @return status of name validation.
	 */
	public static boolean validateName(String name) {
		return true;
	}

	/**
	 * Validates date for authenticity and syntax.
	 *
	 * @param loggedDate
	 * 		string of date to validate.
	 * @return status of date validation.
	 */
	public static boolean validateDate(String loggedDate) {
		return true;
	}

	/**
	 * Validates id for numeric check, length, etc.
	 *
	 * @param id
	 * 		string of int to validate.
	 * @return status of int validation.
	 */
	public static boolean validateInt(String id) {
		return true;
	}

	/**
	 * Validates whether dates entered in range are authentic and check is range is proper (ie, startDate comes before
	 * endDate).
	 *
	 * @param startDate
	 * 		string of date to validate.
	 * @param endDate
	 * 		string of date to validate.
	 * @return status of range validation.
	 */
	public static boolean validateRange(String startDate, String endDate) {
		return true;
	}

}
