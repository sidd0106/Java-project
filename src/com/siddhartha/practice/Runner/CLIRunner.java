package com.siddhartha.practice.Runner;

import com.siddhartha.practice.DAO.DatabaseDAO;
import com.siddhartha.practice.Helpers.ValidationHelper;
import com.siddhartha.practice.Models.Employee;
import com.siddhartha.practice.Models.Manager;
import com.siddhartha.practice.Models.PTLog;
import com.siddhartha.practice.Models.TeamMember;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Interface to take input from user in CLI and DatabaseDAO.
 *
 * @author sid
 */
public final class CLIRunner implements Runnable {

	/**
	 * Waits for user to continue with options in CLI.
	 */
	public void promptEnterKey() {
		System.out.println("Press \"ENTER\" to continue.");
		try {
			System.in.read();
		} catch (IOException ioException) {
			System.out.println("Invalid input from user.\n" + ioException.toString());
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
			return new Scanner(System.in).nextLine();
		} else {
			return new Scanner(System.in).next();
		}
	}

	@Override
	public void run() {
		DatabaseDAO databaseDAO = new DatabaseDAO();
		Boolean exit = false;
		int option, result = 0;

		databaseDAO.getVersion();
		try {
			while (!exit) {
				option = Integer.parseInt(scanInput("1.\tAdd new employee \n" +
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
						"Enter option:\t", false));

				switch (option) {
					case 1: {
						String name = scanInput("Enter Employee name: ", false);
						if (ValidationHelper.validateName(name) == false) {
							System.out.println("Invalid employee name entered.");
							break;
						}
						result = databaseDAO.addEmployee(name);
						break;
					}

					case 2: {
						int id = Integer.parseInt(scanInput("Enter Employee Id: ", false));
						String details = scanInput("Enter PTLog details: ", true);
						String dateString = scanInput("Enter PTLog logged date [FORMAT YYYY-MM-DD, ex- 2020-07-20]: ", false);
						Date loggedDate = Date.valueOf(dateString);
						if (!(ValidationHelper.validateDate(loggedDate) &&
								ValidationHelper.validateInt(id))) {
							System.out.println("Invalid input entered.");
							break;
						}
						result = databaseDAO.addPTLogEmployee(id, details, loggedDate);
						break;
					}

					case 3: {
						int id = Integer.parseInt(scanInput("Enter Employee id: ", false));
						if (ValidationHelper.validateInt(id) == false) {
							System.out.println("Invalid employee id entered.");
							break;
						}
						result = databaseDAO.promoteToManager(id);
						break;
					}

					case 4: {
						int employeeId = Integer.parseInt(scanInput("Enter Employee id: ", false));
						int managerId = Integer.parseInt(scanInput("Enter Manager id: ", false));
						if (!(ValidationHelper.validateInt(employeeId) &&
								ValidationHelper.validateInt(managerId))) {
							System.out.println("Invalid id entered.");
							break;
						}
						result = databaseDAO.assignEmployeeToManager(employeeId, managerId);
						break;
					}

					case 5: {
						ArrayList<Employee> listEmployees = databaseDAO.listEmployees();
						if (listEmployees != null) {
							System.out.printf("|%-20s |%-20s |%-20s|\n", "EMPLOYEE_ID", "Name", "DESIGNATION");
							for (Employee e : listEmployees) {
								if (e instanceof TeamMember) {
									System.out.printf("|%-20s |%-20s |%-20s|\n",
											((TeamMember) e).getId(),
											((TeamMember) e).getName(),
											((TeamMember) e).getDesignation());
								} else if (e instanceof Manager) {
									System.out.printf("|%-20s |%-20s |%-20s|\n",
											((Manager) e).getId(),
											((Manager) e).getName(),
											((Manager) e).getDesignation());
								}
							}
						}
						result = 1;
						break;
					}

					case 6: {
						int id = Integer.parseInt(scanInput("Enter Employee id: ", false));
						if (!ValidationHelper.validateInt(id)) {
							System.out.println("Invalid id entered.");
							break;
						}
						ArrayList<PTLog> listPTLog = databaseDAO.listPTLogOfEmployee(id);
						if (listPTLog != null) {
							System.out.printf("|%-20s |%-40s |%-20s|\n", "PTLOG_ID", "DETAILS", "LOGGED_DATE");
							for (PTLog p : listPTLog) {
								System.out.printf("|%-20s |%-40s |%-20s|\n",
										p.getId(),
										p.getDetails(),
										p.getLoggedDate());
							}
						}
						result = 1;
						break;
					}

					case 7: {
						int id = Integer.parseInt(scanInput("Enter Employee id: ", false));
						String dateString = scanInput("Enter range start date [FORMAT YYYY-MM-DD, ex- 2020-07-20]: ", false);
						Date startDate = Date.valueOf(dateString);
						dateString = scanInput("Enter range end date [FORMAT YYYY-MM-DD, ex- 2020-07-20]: ", false);
						Date endDate = Date.valueOf(dateString);
						if (!(ValidationHelper.validateDate(startDate) &&
								ValidationHelper.validateInt(id) &&
								ValidationHelper.validateDate(startDate))) {
							System.out.println("Invalid input entered.");
							break;
						}
						ArrayList<PTLog> listPTLog = databaseDAO.listPTLogOfEmployeeInRange(id, startDate, endDate);
						if (listPTLog != null) {
							System.out.printf("|%-20s |%-40s |%-20s|\n", "PTLOG_ID", "DETAILS", "LOGGED_DATE");
							for (PTLog p : listPTLog) {
								System.out.printf("|%-20s |%-40s |%-20s|\n",
										p.getId(),
										p.getDetails(),
										p.getLoggedDate());
							}
						}
						result = 1;
						break;
					}

					case 8: {
						int id = Integer.parseInt(scanInput("Enter Manager id: ", false));
						if (!ValidationHelper.validateInt(id)) {
							System.out.println("Invalid id entered.");
							break;
						}
						ArrayList<PTLog> listPTLog = databaseDAO.listPTLogUnderManager(id);
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
						result = 1;
						break;
					}

					case 9: {
						int id = Integer.parseInt(scanInput("Enter Manager id: ", false));
						String dateString = scanInput("Enter range start date [FORMAT YYYY-MM-DD, ex- 2020-07-20]: ", false);
						Date startDate = Date.valueOf(dateString);
						dateString = scanInput("Enter range end date [FORMAT YYYY-MM-DD, ex- 2020-07-20]: ", false);
						Date endDate = Date.valueOf(dateString);
						if (!(ValidationHelper.validateDate(startDate) &&
								ValidationHelper.validateInt(id) &&
								ValidationHelper.validateDate(startDate))) {
							System.out.println("Invalid input entered.");
							break;
						}
						ArrayList<PTLog> listPTLog = databaseDAO.listPTLogUnderManagerInRange(id, startDate, endDate);
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
						result = 1;
						break;
					}

					case 10: {
						int id = Integer.parseInt(scanInput("Enter Employee id: ", false));
						String dateString = scanInput("Enter range start date [FORMAT YYYY-MM-DD, ex- 2020-07-20]: ", false);
						Date startDate = Date.valueOf(dateString);
						dateString = scanInput("Enter range end date [FORMAT YYYY-MM-DD, ex- 2020-07-20]: ", false);
						Date endDate = Date.valueOf(dateString);
						if (!(ValidationHelper.validateDate(startDate) &&
								ValidationHelper.validateInt(id) &&
								ValidationHelper.validateDate(startDate))) {
							System.out.println("Invalid input entered.");
							break;
						}
						result = databaseDAO.deletePTLogOfEmployeeInRange(id, startDate, endDate);
						break;
					}

					case 11: {
						databaseDAO.closeConnection();
						exit = true;
						result = 1;
						break;
					}

					default:
						System.out.println("INVALID OPTION ENTERED.");
						break;
				}
				if (result == 0) {
					System.out.println("Command execution failed.");
				} else {
					System.out.println("Command execution successful.");
				}
				promptEnterKey();

			}
		} catch (Exception exception) {
			System.out.println("Exception encountered.\n" + exception.toString());
		}
	}
}
