package com.siddhartha.practice.DAO;

import com.siddhartha.practice.Models.*;
import com.siddhartha.practice.Service.DBConnectionService;

import java.sql.*;
import java.util.ArrayList;

/**
 * DatabaseDAO class to interact with database. This runs the requested queries on the established connection.
 *
 * @author sid
 */
public final class DatabaseDAO {
	private Connection connection = null;
	private PreparedStatement statement = null;
	private ResultSet resultSet = null;
	private String query;

	/**
	 * Constructor class for DatabaseDAO. It fetches the connection established by DBConnectionService and returns the
	 * instance.
	 */
	public DatabaseDAO() {
		try {
			connection = DBConnectionService.getConnection();
		} catch (RuntimeException runtimeException) {
			System.out.println("Failed to fetch database connection.");
		}
	}

	/**
	 * Calls the DBConnectionService to close the connection with the database.
	 */
	public void closeConnection() {
		try {
			DBConnectionService.closeConnection();
		} catch (RuntimeException runtimeException) {
			System.out.println("Failed to close database connection.");
		}
	}

	/**
	 * Fetches the version of the database.
	 */
	public void getVersion() {
		query = "SELECT VERSION()";
		try {
			statement = connection.prepareStatement(query);
			resultSet = statement.executeQuery();
			System.out.println(resultSet.toString());
		} catch (SQLException sqlException) {
			System.out.println("Failed to fetch database version.");
		}
	}

	/**
	 * Adds new employee to the database. By default, the designation of the employee is TeamMember.
	 *
	 * @param name
	 * 		name of the employee being added.
	 * @return result of the data insertion in the database.
	 */
	public int addEmployee(String name) {
		int result = 0;
		query = "insert into employees (name) values (?)";
		try {
			statement = connection.prepareStatement(query);
			statement.setString(1, name);
			return statement.executeUpdate();
		} catch (SQLException sqlException) {
			System.out.println("Failed to add Employee in database.\n" + sqlException.toString());
		}
		return result;
	}

	/**
	 * Adds Pt log by an employee.
	 *
	 * @param employeeId
	 * 		id of employee adding pt log.
	 * @param details
	 * 		details of the log.
	 * @param loggedDate
	 * 		date when log is added.
	 * @return
	 */
	public int addPTLogEmployee(int employeeId, String details, Date loggedDate) {
		String checkQuery = "select * from employees where emp_id = (?)";
		query = "insert into ptlogs (emp_id, details, logged_date) values (?,?,?)";
		try {
			statement = connection.prepareStatement((checkQuery));
			statement.setInt(1, employeeId);
			resultSet = statement.executeQuery();
			if (!resultSet.next()) {
				System.out.println("Employee id doesn't exists in database.");
				return 0;
			}
			statement = connection.prepareStatement(query);
			statement.setInt(1, employeeId);
			statement.setString(2, details);
			statement.setDate(3, loggedDate);
			return statement.executeUpdate();
		} catch (SQLException sqlException) {
			System.out.println("Failed to add PTLog of Employee " + employeeId + " in database.\n" + sqlException.toString());
		}
		return 0;
	}

	/**
	 * Changes the Designation of employee from TeamMember to Manager.
	 *
	 * @param id
	 * 		employee id to be promoted to manager.
	 * @return result of the update query executed on the database.
	 */
	public int promoteToManager(int id) {
		String checkQuery = "select * from employees where emp_id = (?)";
		query = "UPDATE employees set designation = (?) where emp_id = (?)";
		try {
			statement = connection.prepareStatement((checkQuery));
			statement.setInt(1, id);
			resultSet = statement.executeQuery();
			if (!resultSet.next()) {
				System.out.println("Employee id doesn't exists in database.");
				return 0;
			}
			statement = connection.prepareStatement(query);
			statement.setString(1, String.valueOf(Designation.Manager));
			statement.setInt(2, id);
			return statement.executeUpdate();
		} catch (SQLException sqlException) {
			System.out.println("Failed to promote Employee to Manager in database.\n" + sqlException.toString());
		}
		return 0;
	}

	/**
	 * Adds employee under manager.
	 *
	 * @param employeeId
	 * 		employee id to be added under manager.
	 * @param managerId
	 * 		manager id under whom employee is added.
	 * @return
	 */
	public int assignEmployeeToManager(int employeeId, int managerId) {
		String checkQuery = "select * from employees where emp_id = (?)";
		query = "insert into employee_managers (emp_id,mgr_id) values (?,?)";
		try {
			statement = connection.prepareStatement((checkQuery));
			statement.setInt(1, employeeId);
			resultSet = statement.executeQuery();
			if (!resultSet.next()) {
				System.out.println("Employee id doesn't exists in database.");
				return 0;
			} else if (!resultSet.getString("designation").equals("TeamMember")) {
				System.out.println("Employee id entered is not a Team Member.");
				return 0;
			}
			statement = connection.prepareStatement((checkQuery));
			statement.setInt(1, managerId);
			resultSet = statement.executeQuery();
			if (!resultSet.next()) {
				System.out.println("Manager id doesn't exists in database.");
				return 0;
			} else if (!resultSet.getString("designation").equals("Manager")) {
				System.out.println("Manager id entered is not a manager.");
				return 0;
			}
			statement = connection.prepareStatement(query);
			statement.setInt(1, employeeId);
			statement.setInt(2, managerId);
			return statement.executeUpdate();
		} catch (SQLException sqlException) {
			System.out.println("Failed to assign Employee to manager in database.\n" + sqlException.toString());
		}
		return 0;
	}

	/**
	 * Fetches list of employees in database.
	 * @return list of employees.
	 */
	public ArrayList<Employee> listEmployees() {
		query = "select * from employees";
		try {
			statement = connection.prepareStatement(query);
			resultSet = statement.executeQuery();
			ArrayList<Employee> listEmployee = new ArrayList<>();
			while (resultSet.next()) {
				if (resultSet.getString("designation").equals("TeamMember")) {
					TeamMember employee = new TeamMember();
					employee.setId(resultSet.getInt("emp_id"));
					employee.setName(resultSet.getString("name"));
					listEmployee.add(employee);
				} else if (resultSet.getString("designation").equals("Manager")) {
					Manager employee = new Manager();
					employee.setId(resultSet.getInt("emp_id"));
					employee.setName(resultSet.getString("name"));
					listEmployee.add(employee);
				}
			}
			return listEmployee;
		} catch (SQLException sqlException) {
			System.out.println("Failed to employees in database.\n" + sqlException.toString());
		}
		return null;
	}

	/**
	 * Fetches list of pt logs, logged by an employee in database.
	 * @param id employee id whose logs are fetched.
	 * @return list of pt logs.
	 */
	public ArrayList<PTLog> listPTLogOfEmployee(int id) {
		String checkQuery = "select * from employees where emp_id = (?)";
		query = "select * from ptlogs where emp_id = (?)";
		try {
			statement = connection.prepareStatement((checkQuery));
			statement.setInt(1, id);
			resultSet = statement.executeQuery();
			if (!resultSet.next()) {
				System.out.println("Employee id doesn't exists in database.");
				return null;
			}
			statement = connection.prepareStatement(query);
			statement.setInt(1, id);
			resultSet = statement.executeQuery();
			ArrayList<PTLog> listPTLog = new ArrayList<>();
			while (resultSet.next()) {
				PTLog ptLog = new PTLog();
				ptLog.setDetails(resultSet.getString("details"));
				ptLog.setId(resultSet.getInt("ptlog_id"));
				ptLog.setLoggedDate(resultSet.getDate("logged_date"));
				ptLog.setId(id);
				listPTLog.add(ptLog);
			}
			return listPTLog;
		} catch (SQLException sqlException) {
			System.out.println("Failed to fetch ptlogs of employee in database.\n" + sqlException.toString());
		}
		return null;
	}

	/**
	 * Fetches list of pt logs, logged by an employee in between a range of date in database.
	 * @param id employee id whose logs are fetched.
	 * @param startDate start point of date range.
	 * @param endDate end point of date range.
	 * @return list of pt logs.
	 */
	public ArrayList<PTLog> listPTLogOfEmployeeInRange(int id, Date startDate, Date endDate) {
		String checkQuery = "select * from employees where emp_id = (?)";
		query = "select * from ptlogs where emp_id = (?) and (logged_date >= (?) and logged_date <= (?))";
		try {
			statement = connection.prepareStatement((checkQuery));
			statement.setInt(1, id);
			resultSet = statement.executeQuery();
			if (!resultSet.next()) {
				System.out.println("Employee id doesn't exists in database.");
				return null;
			}
			statement = connection.prepareStatement(query);
			statement.setInt(1, id);
			statement.setDate(2, startDate);
			statement.setDate(3, endDate);
			resultSet = statement.executeQuery();
			ArrayList<PTLog> listPTLog = new ArrayList<>();
			while (resultSet.next()) {
				PTLog ptLog = new PTLog();
				ptLog.setDetails(resultSet.getString("details"));
				ptLog.setId(resultSet.getInt("ptlog_id"));
				ptLog.setLoggedDate(resultSet.getDate("logged_date"));
				ptLog.setId(resultSet.getInt("emp_id"));
				listPTLog.add(ptLog);
			}
			return listPTLog;
		} catch (SQLException sqlException) {
			System.out.println("Failed to fetch ptlogs of employee for a range of date in database.\n" + sqlException.toString());
		}
		return null;
	}

	/**
	 * Fetches list of pt logs, logged by all employees under a manager in database.
	 * @param id employee id of manager under which employees exists whose logs are fetched.
	 * @return list of pt logs.
	 */
	public ArrayList<PTLog> listPTLogUnderManager(int id) {
		String checkQuery = "select * from employees where emp_id = (?)";
		query = "select * from ptlogs as p join employee_managers as em on p.emp_id = em.emp_id where mgr_id = (?)";
		try {
			statement = connection.prepareStatement((checkQuery));
			statement.setInt(1, id);
			resultSet = statement.executeQuery();
			if (!resultSet.next()) {
				System.out.println("Manager id doesn't exists in database.");
				return null;
			} else if (!resultSet.getString("designation").equals("Manager")) {
				System.out.println("Manager id entered is not a manager.");
				return null;
			}
			statement = connection.prepareStatement(query);
			statement.setInt(1, id);
			resultSet = statement.executeQuery();
			ArrayList<PTLog> listPTLog = new ArrayList<>();
			while (resultSet.next()) {
				PTLog ptLog = new PTLog();
				ptLog.setDetails(resultSet.getString("details"));
				ptLog.setId(resultSet.getInt("ptlog_id"));
				ptLog.setLoggedDate(resultSet.getDate("logged_date"));
				ptLog.setEmployeeId(resultSet.getInt("p.emp_id"));
				listPTLog.add(ptLog);
			}
			return listPTLog;
		} catch (SQLException sqlException) {
			System.out.println("Failed to fetch ptlogs of employees under manager in database.\n" + sqlException.toString());
		}
		return null;
	}

	/**
	 * Fetches list of pt logs, logged by all employees under a manager logged in a range of date in database.
	 * @param id employee id of manager under which employees exists whose logs are fetched.
	 * @param startDate start point of date range.
	 * @param endDate endDate end point of date range.
	 * @return list of pt logs.
	 */
	public ArrayList<PTLog> listPTLogUnderManagerInRange(int id, Date startDate, Date endDate) {
		String checkQuery = "select * from employees where emp_id = (?)";
		query = "select * from ptlogs as p join employee_managers as em on p.emp_id = em.emp_id where mgr_id = (?) and (logged_date >= (?) and logged_date <= (?))";
		try {
			statement = connection.prepareStatement((checkQuery));
			statement.setInt(1, id);
			resultSet = statement.executeQuery();
			if (!resultSet.next()) {
				System.out.println("Manager id doesn't exists in database.");
				return null;
			} else if (!resultSet.getString("designation").equals("Manager")) {
				System.out.println("Manager id entered is not a manager.");
				return null;
			}
			statement = connection.prepareStatement(query);
			statement.setInt(1, id);
			statement.setDate(2, startDate);
			statement.setDate(3, endDate);
			resultSet = statement.executeQuery();
			ArrayList<PTLog> listPTLog = new ArrayList<>();
			while (resultSet.next()) {
				PTLog ptLog = new PTLog();
				ptLog.setDetails(resultSet.getString("details"));
				ptLog.setId(resultSet.getInt("ptlog_id"));
				ptLog.setLoggedDate(resultSet.getDate("logged_date"));
				ptLog.setEmployeeId(resultSet.getInt("p.emp_id"));
				listPTLog.add(ptLog);
			}
			return listPTLog;
		} catch (SQLException sqlException) {
			System.out.println("Failed to fetch ptlogs of employees under manager for a range of date in database.\n" + sqlException.toString());
		}
		return null;
	}

	/**
	 * Deletes pt logs logged by an employee in a range of date in database.
	 * @param id employee id whose logs are deleted.
	 * @param startDate start point of date range.
	 * @param endDate endDate end point of date range.
	 * @return result of delete query executed on database.
	 */
	public int deletePTLogOfEmployeeInRange(int id, Date startDate, Date endDate) {
		query = "delete from ptlogs where emp_id = (?) and (logged_date >= (?) and logged_date <= (?))";
		try {
			statement = connection.prepareStatement(query);
			statement.setInt(1, id);
			statement.setDate(2, startDate);
			statement.setDate(3, endDate);
			return statement.executeUpdate();
		} catch (SQLException sqlException) {
			System.out.println("Failed to delete ptlogs of employee for a range of date in database.\n" + sqlException.toString());
		}
		return 0;
	}
}
