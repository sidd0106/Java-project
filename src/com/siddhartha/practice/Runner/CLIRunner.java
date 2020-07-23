package com.siddhartha.practice.Runner;

import com.siddhartha.practice.DAO.PTLogService;
import com.siddhartha.practice.Helpers.ValidationHelper;
import com.siddhartha.practice.Models.Employee;
import com.siddhartha.practice.Models.PTLog;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Interface to take input from user in CLI and PTLogService.
 *
 * @author sid
 */
public final class CLIRunner implements CLIRunInterface {

	private PTLogService ptLogService;
	private final static int COMMAND_EXECUTION_RESULT_FAIL = 0;
	private final static int COMMAND_EXECUTION_RESULT_SUCCESS = 1;

	public CLIRunner() {
		this.ptLogService = new PTLogService();
	}

	/**
	 * Waits for user to continue with options in CLI.
	 */
	public void promptEnterKey() throws IOException {
		System.out.println("Press \"ENTER\" to continue.");
		try {
			System.in.read();
		} catch (IOException ioException) {
			throw ioException;
		}
	}

	/**
	 * Takes input for required parameter from user in CLI.
	 *
	 * @param outputStatement
	 * 		statement for CLI user.
	 * @param isLine
	 * 		boolean to denote whether input to be taken is a line or a word.
	 * @return
	 */
	public String scanInput(String outputStatement, boolean isLine) {
		System.out.print(outputStatement);
		if (isLine) {
			return (new Scanner(System.in).nextLine()).trim();
		} else {
			return (new Scanner(System.in).next()).trim();
		}
	}

	/**
	 * Starts the PT Logger service to interact with user for command options and database to run those queries.
	 */
	public void run() {

		Boolean exit = false;
		int option, commandExecutionResult = 0;

		try {
			while (!exit) {
				option = printUserOptions();
				switch (option) {
					case 1: {
						commandExecutionResult = addEmployeeOption();
						break;
					}

					case 2: {
						commandExecutionResult = addPTLogForEmployeeOption();
						break;
					}

					case 3: {
						commandExecutionResult = promoteToManagerOption();
						break;
					}

					case 4: {
						commandExecutionResult = assignEmployeeToManagerOption();
						break;
					}

					case 5: {
						commandExecutionResult = listEmployeeOption();
						break;
					}

					case 6: {
						commandExecutionResult = listPtLogOfEmployeeOption();
						break;
					}

					case 7: {
						commandExecutionResult = listPtLogOfEmployeeInRangeOption();
						break;
					}

					case 8: {
						commandExecutionResult = listPTLogUnderManagerOption();
						break;
					}

					case 9: {
						commandExecutionResult = listPTLogUnderManagerInRangeOption();
						break;
					}

					case 10: {
						deletPTLogofEmployeeInRangeOption();
						break;
					}

					case 11: {
						ptLogService.closeConnection();
						exit = true;
						commandExecutionResult = COMMAND_EXECUTION_RESULT_SUCCESS;
						break;
					}

					default:
						System.out.println("INVALID OPTION ENTERED.");
						break;
				}
				if (commandExecutionResult == COMMAND_EXECUTION_RESULT_FAIL) {
					System.out.println("Command execution failed.");
				} else {
					System.out.println("Command execution successful.");
				}
				promptEnterKey();

			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Prints the user option menu to the CLI.
	 *
	 * @return options entered by the user.
	 */
	private int printUserOptions() {
		int option = 0;
		String optionString = scanInput("1.\tAdd new employee \n" +
				"2.\tAdd PT log for an employee \n" +
				"3.\tPromote an employee to manager\n" +
				"4.\tAssign manager for an employee\n" +
				"5.\tList all employee\n" +
				"6.\tList all PTs of an employee in descending order of logging date\n" +
				"7.\tList all PTs of an employee in a given date range in descending order of logging date\n" +
				"8.\tList all PTs of all the team members that fall under one manager in descending order of logging date\n" +
				"9.\tList all PTs of all the team members that fall under one manager in a given date range in descending order of logging date\n" +
				"10.\tDelete all PTs of an employee in a date range\n" +
				"11.\tEXIT.\n" +
				"Enter option:\t", false);
		if (ValidationHelper.validateInt(optionString)) {
			option = Integer.parseInt(optionString);
		}
		return option;
	}

	/**
	 * Takes required input from user to make query call on DB and add an Employee to database.
	 *
	 * @return status of option execution.
	 */
	private int addEmployeeOption() {
		String name = scanInput("Enter Employee name: ", false);
		if (ValidationHelper.validateName(name) == false) {
			System.out.println("Invalid employee name entered.");
			return COMMAND_EXECUTION_RESULT_FAIL;
		}
		return ptLogService.addEmployee(name);
	}

	/**
	 * Takes required input from user to make query call on DB and add pt log for an Employee.
	 *
	 * @return status of option execution.
	 */
	private int addPTLogForEmployeeOption() {
		String idString = scanInput("Enter Employee Id: ", false);
		String details = scanInput("Enter PTLog details: ", true);
		String dateString = scanInput("Enter PTLog logged date [FORMAT YYYY-MM-DD, ex- 2020-07-20]: ", false);
		if (!(ValidationHelper.validateDate(dateString) &&
				ValidationHelper.validateInt(idString))) {
			System.out.println("Invalid input entered.");
			return COMMAND_EXECUTION_RESULT_FAIL;
		}
		int id = Integer.parseInt(idString);
		Date loggedDate = Date.valueOf(dateString);
		return ptLogService.addPTLogEmployee(id, details, loggedDate);
	}

	/**
	 * Takes required input from user to make query call on DB and update the designation of Employee to Manager.
	 *
	 * @return status of option execution.
	 */
	private int promoteToManagerOption() {
		String idString = scanInput("Enter Employee id: ", false);
		if (ValidationHelper.validateInt(idString) == false) {
			System.out.println("Invalid employee id entered.");
			return COMMAND_EXECUTION_RESULT_FAIL;
		}
		int id = Integer.parseInt(idString);
		return ptLogService.promoteToManager(id);
	}

	/**
	 * Takes required input from user to make query call on DB and assign Employee to a Manager.
	 *
	 * @return status of option execution.
	 */
	private int assignEmployeeToManagerOption() {
		String employeeIdString = scanInput("Enter Employee id: ", false);
		String managerIdString = scanInput("Enter Manager id: ", false);
		if (!(ValidationHelper.validateInt(employeeIdString) &&
				ValidationHelper.validateInt(managerIdString))) {
			System.out.println("Invalid id entered.");
			return COMMAND_EXECUTION_RESULT_FAIL;
		}
		int employeeId = Integer.parseInt(employeeIdString);
		int managerId = Integer.parseInt(managerIdString);
		return ptLogService.assignEmployeeToManager(employeeId, managerId);
	}

	/**
	 * Takes required input from user to make query call on DB and fetch all Employees in database.
	 *
	 * @return status of option execution.
	 */
	private int listEmployeeOption() {
		ArrayList<Employee> listEmployees = ptLogService.listEmployees();
		if (listEmployees != null) {
			System.out.printf("|%-20s |%-20s |%-20s|\n", "EMPLOYEE_ID", "Name", "DESIGNATION");
			for (Employee e : listEmployees) {
				System.out.printf("|%-20s |%-20s |%-20s|\n",
						e.getId(),
						e.getName(),
						e.getDesignation());
			}
		}
		return COMMAND_EXECUTION_RESULT_SUCCESS;
	}

	/**
	 * Takes required input from user to make query call on DB and fetch pt log of Employee.
	 *
	 * @return status of option execution.
	 */
	private int listPtLogOfEmployeeOption() {
		String idString = scanInput("Enter Employee id: ", false);
		if (!ValidationHelper.validateInt(idString)) {
			System.out.println("Invalid id entered.");
			return COMMAND_EXECUTION_RESULT_FAIL;
		}
		int id = Integer.parseInt(idString);
		ArrayList<PTLog> listPTLog = ptLogService.listPTLogOfEmployee(id);
		if (listPTLog != null) {
			System.out.printf("|%-20s |%-40s |%-20s|\n", "PTLOG_ID", "DETAILS", "LOGGED_DATE");
			for (PTLog p : listPTLog) {
				System.out.printf("|%-20s |%-40s |%-20s|\n",
						p.getId(),
						p.getDetails(),
						p.getLoggedDate());
			}
		}
		return COMMAND_EXECUTION_RESULT_SUCCESS;
	}

	/**
	 * Takes required input from user to make query call on DB and fetch pt log of Employee which have logged date in
	 * required range.
	 *
	 * @return status of option execution.
	 */
	private int listPtLogOfEmployeeInRangeOption() {
		String idString = scanInput("Enter Employee id: ", false);
		String startDateString = scanInput("Enter range start date [FORMAT YYYY-MM-DD, ex- 2020-07-20]: ", false);
		String endDateString = scanInput("Enter range end date [FORMAT YYYY-MM-DD, ex- 2020-07-20]: ", false);
		if (!(ValidationHelper.validateInt(idString) &&
				ValidationHelper.validateRange(startDateString, endDateString))) {
			System.out.println("Invalid input entered.");
			return COMMAND_EXECUTION_RESULT_FAIL;
		}
		int id = Integer.parseInt(idString);
		Date startDate = Date.valueOf(startDateString);
		Date endDate = Date.valueOf(endDateString);
		ArrayList<PTLog> listPTLog = ptLogService.listPTLogOfEmployeeInRange(id, startDate, endDate);
		if (listPTLog != null) {
			System.out.printf("|%-20s |%-40s |%-20s|\n", "PTLOG_ID", "DETAILS", "LOGGED_DATE");
			for (PTLog p : listPTLog) {
				System.out.printf("|%-20s |%-40s |%-20s|\n",
						p.getId(),
						p.getDetails(),
						p.getLoggedDate());
			}
		}
		return COMMAND_EXECUTION_RESULT_SUCCESS;
	}

	/**
	 * Takes required input from user to make query call on DB and fetch pt log of Employee under a manager.
	 *
	 * @return status of option execution.
	 */
	private int listPTLogUnderManagerOption() {
		String idString = scanInput("Enter Manager id: ", false);
		if (!ValidationHelper.validateInt(idString)) {
			System.out.println("Invalid id entered.");
			return COMMAND_EXECUTION_RESULT_FAIL;
		}
		int id = Integer.parseInt(idString);
		ArrayList<PTLog> listPTLog = ptLogService.listPTLogUnderManager(id);
		if (listPTLog != null) {
			System.out.printf("|%-20s |%-20s |%-40s |%-20s|\n", "EMPLOYEE_ID", "PTLOG_ID", "DETAILS", "LOGGED_DATE");
			for (PTLog p : listPTLog) {
				System.out.printf("|%-20s |%-20s |%-40s |%-20s|\n",
						p.getEmployeeId(),
						p.getId(),
						p.getDetails(),
						p.getLoggedDate());
			}
		}
		return COMMAND_EXECUTION_RESULT_SUCCESS;
	}

	/**
	 * Takes required input from user to make query call on DB and fetch pt log of Employee under a manager which have
	 * logged date in required range.
	 *
	 * @return status of option execution.
	 */
	private int listPTLogUnderManagerInRangeOption() {
		String idString = scanInput("Enter Employee id: ", false);
		String startDateString = scanInput("Enter range start date [FORMAT YYYY-MM-DD, ex- 2020-07-20]: ", false);
		String endDateString = scanInput("Enter range end date [FORMAT YYYY-MM-DD, ex- 2020-07-20]: ", false);
		if (!(ValidationHelper.validateInt(idString) &&
				ValidationHelper.validateRange(startDateString, endDateString))) {
			System.out.println("Invalid input entered.");
			return COMMAND_EXECUTION_RESULT_FAIL;
		}
		int id = Integer.parseInt(idString);
		Date startDate = Date.valueOf(startDateString);
		Date endDate = Date.valueOf(endDateString);
		ArrayList<PTLog> listPTLog = ptLogService.listPTLogUnderManagerInRange(id, startDate, endDate);
		if (listPTLog != null) {
			System.out.printf("|%-20s |%-20s |%-40s |%-20s|\n", "EMPLOYEE_ID", "PTLOG_ID", "DETAILS", "LOGGED_DATE");
			for (PTLog p : listPTLog) {
				System.out.printf("|%-20s |%-20s |%-40s |%-20s|\n",
						p.getEmployeeId(),
						p.getId(),
						p.getDetails(),
						p.getLoggedDate());
			}
		}
		return COMMAND_EXECUTION_RESULT_SUCCESS;
	}

	/**
	 * Takes required input from user to call make query call on DB and delete pt log of Employee which have logged date
	 * in required range.
	 *
	 * @return status of option execution.
	 */
	private int deletPTLogofEmployeeInRangeOption() {
		String idString = scanInput("Enter Employee id: ", false);
		String startDateString = scanInput("Enter range start date [FORMAT YYYY-MM-DD, ex- 2020-07-20]: ", false);
		String endDateString = scanInput("Enter range end date [FORMAT YYYY-MM-DD, ex- 2020-07-20]: ", false);
		if (!(ValidationHelper.validateInt(idString) &&
				ValidationHelper.validateRange(startDateString, endDateString))) {
			System.out.println("Invalid input entered.");
			return COMMAND_EXECUTION_RESULT_FAIL;
		}
		int id = Integer.parseInt(idString);
		Date startDate = Date.valueOf(startDateString);
		Date endDate = Date.valueOf(endDateString);
		ptLogService.deletePTLogOfEmployeeInRange(id, startDate, endDate);
		return COMMAND_EXECUTION_RESULT_SUCCESS;
	}

}
