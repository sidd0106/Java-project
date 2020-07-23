package com.siddhartha.practice.DAO;

import com.siddhartha.practice.Models.*;
import com.siddhartha.practice.Service.DBConnectionService;
import com.siddhartha.practice.Helpers.EmployeeFactory;

import java.sql.*;
import java.util.ArrayList;

/**
 * PTLogService class to interact with database. This runs the requested queries on the established connection.
 *
 * @author sid
 */
public final class PTLogService {
	private Connection connection = null;
	private final static int COMMAND_EXECUTION_RESULT_FAIL = 0;
	private final static int COMMAND_EXECUTION_RESULT_SUCCESS = 1;

	/**
	 * Constructor class for PTLogService. It fetches the connection established by DBConnectionService and returns the
	 * instance.
	 */
	public PTLogService() throws RuntimeException {
		try {
			connection = DBConnectionService.getConnection();
		} catch (RuntimeException runtimeException) {
			System.out.println("Failed to fetch database connection.");
			throw runtimeException;
		}
	}

	/**
	 * Calls the DBConnectionService to close the connection with the database.
	 */
	public void closeConnection() throws RuntimeException {
		try {
			DBConnectionService.closeConnection();
		} catch (RuntimeException runtimeException) {
			System.out.println("Failed to close database connection.");
			throw runtimeException;
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
		PreparedStatement statement = null;
		String query = "insert into employees (name,designation) values (?,?)";
		try {
			statement = connection.prepareStatement(query);
			statement.setString(1, name);
			statement.setString(2, String.valueOf(Designation.TeamMember));
			statement.executeUpdate();
		} catch (SQLException sqlException) {
			System.out.println("Failed to add Employee in database.\n" + sqlException.toString());
			return COMMAND_EXECUTION_RESULT_FAIL;
		}
		return COMMAND_EXECUTION_RESULT_SUCCESS;
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
		PreparedStatement statement = null;
		String query = "insert into ptlogs (emp_id, details, logged_date) values (?,?,?)";
		try {
			if (!checkEmployeeQuery(employeeId, Designation.TeamMember, false)) {
				return COMMAND_EXECUTION_RESULT_FAIL;
			}
			statement = connection.prepareStatement(query);
			statement.setInt(1, employeeId);
			statement.setString(2, details);
			statement.setDate(3, loggedDate);
			statement.executeUpdate();
		} catch (SQLException sqlException) {
			System.out.println("Failed to add PTLog of Employee " + employeeId + " in database.\n" + sqlException.toString());
			return COMMAND_EXECUTION_RESULT_FAIL;
		}
		return COMMAND_EXECUTION_RESULT_SUCCESS;
	}

	/**
	 * Changes the Designation of employee from TeamMember to Manager.
	 *
	 * @param id
	 * 		employee id to be promoted to manager.
	 * @return result of the update query executed on the database.
	 */
	public int promoteToManager(int id) {
		PreparedStatement statement = null;
		String query = "UPDATE employees set designation = (?) where emp_id = (?)";
		try {
			if (!checkEmployeeQuery(id, Designation.TeamMember, true)) {
				return COMMAND_EXECUTION_RESULT_FAIL;
			}
			statement = connection.prepareStatement(query);
			statement.setString(1, String.valueOf(Designation.Manager));
			statement.setInt(2, id);
			statement.executeUpdate();
		} catch (SQLException sqlException) {
			System.out.println("Failed to promote Employee to Manager in database.\n" + sqlException.toString());
		}
		return COMMAND_EXECUTION_RESULT_SUCCESS;
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
		PreparedStatement statement = null;
		String query = "insert into employee_managers (emp_id,mgr_id) values (?,?)";
		try {
			if (!(checkEmployeeQuery(employeeId, Designation.TeamMember, true) &&
					checkEmployeeQuery(managerId, Designation.Manager, true))) {
				return COMMAND_EXECUTION_RESULT_FAIL;
			}
			statement = connection.prepareStatement(query);
			statement.setInt(1, employeeId);
			statement.setInt(2, managerId);
			statement.executeUpdate();
		} catch (SQLException sqlException) {
			System.out.println("Failed to assign Employee to manager in database.\n" + sqlException.toString());
		}
		return COMMAND_EXECUTION_RESULT_SUCCESS;
	}

	/**
	 * Fetches list of employees in database.
	 *
	 * @return list of employees.
	 */
	public ArrayList<Employee> listEmployees() {
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String query = "select * from employees";
		ArrayList<Employee> listEmployee = new ArrayList<>();
		try {
			statement = connection.prepareStatement(query);
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				Employee employee = EmployeeFactory.getEmployee(Designation.valueOf(resultSet.getString("designation")));
				employee.setId(resultSet.getInt("emp_id"));
				employee.setName(resultSet.getString("name"));
				listEmployee.add(employee);
			}
		} catch (SQLException sqlException) {
			System.out.println("Failed to employees in database.\n" + sqlException.toString());
		}
		return listEmployee;
	}

	/**
	 * Fetches list of pt logs, logged by an employee in database.
	 *
	 * @param id
	 * 		employee id whose logs are fetched.
	 * @return list of pt logs.
	 */
	public ArrayList<PTLog> listPTLogOfEmployee(int id) {
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String query = "select * from ptlogs where emp_id = (?) order by logged_date";
		ArrayList<PTLog> listPTLog = new ArrayList<>();
		try {
			if (!checkEmployeeQuery(id, Designation.TeamMember, false)) {
				return null;
			}
			statement = connection.prepareStatement(query);
			statement.setInt(1, id);
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				PTLog ptLog = new PTLog(resultSet.getInt("ptlog_id"),
						resultSet.getInt("emp_id"),
						resultSet.getString("details"),
						resultSet.getDate("logged_date"));
				listPTLog.add(ptLog);
			}
		} catch (SQLException sqlException) {
			System.out.println("Failed to fetch ptlogs of employee in database.\n" + sqlException.toString());
		}
		return listPTLog;
	}

	/**
	 * Fetches list of pt logs, logged by an employee in between a range of date in database.
	 *
	 * @param id
	 * 		employee id whose logs are fetched.
	 * @param startDate
	 * 		start point of date range.
	 * @param endDate
	 * 		end point of date range.
	 * @return list of pt logs.
	 */
	public ArrayList<PTLog> listPTLogOfEmployeeInRange(int id, Date startDate, Date endDate) {
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String query = "select * from ptlogs where emp_id = (?) and (logged_date >= (?) and logged_date <= (?)) order by logged_date desc";
		ArrayList<PTLog> listPTLog = new ArrayList<>();
		try {
			if (!checkEmployeeQuery(id, Designation.TeamMember, false)) {
				return null;
			}
			statement = connection.prepareStatement(query);
			statement.setInt(1, id);
			statement.setDate(2, startDate);
			statement.setDate(3, endDate);
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				PTLog ptLog = new PTLog(resultSet.getInt("ptlog_id"),
						resultSet.getInt("emp_id"),
						resultSet.getString("details"),
						resultSet.getDate("logged_date"));
				listPTLog.add(ptLog);
			}
		} catch (SQLException sqlException) {
			System.out.println("Failed to fetch ptlogs of employee for a range of date in database.\n" + sqlException.toString());
		}
		return listPTLog;
	}

	/**
	 * Fetches list of pt logs, logged by all employees under a manager in database.
	 *
	 * @param id
	 * 		employee id of manager under which employees exists whose logs are fetched.
	 * @return list of pt logs.
	 */
	public ArrayList<PTLog> listPTLogUnderManager(int id) {
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String query = "select * from ptlogs as p join employee_managers as em on p.emp_id = em.emp_id where mgr_id = (?) order by logged_date desc";
		ArrayList<PTLog> listPTLog = new ArrayList<>();
		try {
			if (!checkEmployeeQuery(id, Designation.Manager, true)) {
				return null;
			}
			statement = connection.prepareStatement(query);
			statement.setInt(1, id);
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				PTLog ptLog = new PTLog(resultSet.getInt("ptlog_id"),
						resultSet.getInt("emp_id"),
						resultSet.getString("details"),
						resultSet.getDate("logged_date"));
				listPTLog.add(ptLog);
			}
		} catch (SQLException sqlException) {
			System.out.println("Failed to fetch ptlogs of employees under manager in database.\n" + sqlException.toString());
		}
		return listPTLog;
	}

	/**
	 * Fetches list of pt logs, logged by all employees under a manager logged in a range of date in database.
	 *
	 * @param id
	 * 		employee id of manager under which employees exists whose logs are fetched.
	 * @param startDate
	 * 		start point of date range.
	 * @param endDate
	 * 		endDate end point of date range.
	 * @return list of pt logs.
	 */
	public ArrayList<PTLog> listPTLogUnderManagerInRange(int id, Date startDate, Date endDate) {
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String query = "select * from ptlogs as p join employee_managers as em on p.emp_id = em.emp_id where mgr_id = (?) and (logged_date >= (?) and logged_date <= (?)) order by logged_date desc";
		ArrayList<PTLog> listPTLog = new ArrayList<>();
		try {
			if (!checkEmployeeQuery(id, Designation.Manager, true)) {
				return null;
			}
			statement = connection.prepareStatement(query);
			statement.setInt(1, id);
			statement.setDate(2, startDate);
			statement.setDate(3, endDate);
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				PTLog ptLog = new PTLog(resultSet.getInt("ptlog_id"),
						resultSet.getInt("emp_id"),
						resultSet.getString("details"),
						resultSet.getDate("logged_date"));
				listPTLog.add(ptLog);
			}
		} catch (SQLException sqlException) {
			System.out.println("Failed to fetch ptlogs of employees under manager for a range of date in database.\n" + sqlException.toString());
		}
		return listPTLog;
	}

	/**
	 * Deletes pt logs logged by an employee in a range of date in database.
	 *
	 * @param id
	 * 		employee id whose logs are deleted.
	 * @param startDate
	 * 		start point of date range.
	 * @param endDate
	 * 		endDate end point of date range.
	 * @return result of delete query executed on database.
	 */
	public int deletePTLogOfEmployeeInRange(int id, Date startDate, Date endDate) {
		PreparedStatement statement = null;
		String query = "delete from ptlogs where emp_id = (?) and (logged_date >= (?) and logged_date <= (?)) order by desc logged_date";
		try {
			statement = connection.prepareStatement(query);
			statement.setInt(1, id);
			statement.setDate(2, startDate);
			statement.setDate(3, endDate);
			statement.executeUpdate();
		} catch (SQLException sqlException) {
			System.out.println("Failed to delete ptlogs of employee for a range of date in database.\n" + sqlException.toString());
			return COMMAND_EXECUTION_RESULT_FAIL;
		}
		return COMMAND_EXECUTION_RESULT_SUCCESS;
	}


	/**
	 * Checks if the Employee id exists in the database or not. Also checks the designation of employee when needed and
	 * compare with designation required.
	 *
	 * @param id
	 * 		Employee id to check whether exists in database.
	 * @param designation
	 * 		required designation to compare with required employee designation from database.
	 * @param checkDesignation
	 * 		signal to compare or skip designation check of employee.
	 * @return Status that the given employee id satisfies all the checks.
	 * @throws SQLException
	 */
	public Boolean checkEmployeeQuery(int id, Designation designation, Boolean checkDesignation) throws SQLException {
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String checkQuery = "select * from employees where emp_id = (?)";
		try {
			statement = connection.prepareStatement((checkQuery));
			statement.setInt(1, id);
			resultSet = statement.executeQuery();
			if (!resultSet.next()) {
				System.out.println("Id doesn't exists in database.");
				return false;
			} else if (checkDesignation && !resultSet.getString("designation").equals(designation.name())) {
				System.out.println("Employee entered is not of correct designation " + designation.name());
				return false;
			}
		} catch (SQLException sqlException) {
			throw sqlException;
		}
		return true;
	}
}
