package com.siddhartha.practice.Service;


import com.mysql.cj.jdbc.Driver;
import config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection service class to create,close and manage connection to a database. Singleton class to avoid
 * multiple instance of same database connection.
 */
public class DBConnectionService {

	private static Connection connection = null;

	/**
	 * Private constructor of DBConnectionService to support singleton class.
	 */
	private DBConnectionService() {

	}

	/**
	 * Creates instance of connection with database.
	 *
	 * @return
	 * @throws RuntimeException
	 */
	public static Connection getConnection() throws RuntimeException {
		try {
			if (connection == null) {
				DriverManager.registerDriver(new Driver());
				connection = DriverManager.getConnection(DatabaseConfig.url, DatabaseConfig.userName, DatabaseConfig.password);
			}
			return connection;
		} catch (SQLException ex) {
			throw new RuntimeException("Error connecting to the database", ex);
		}
	}

	/**
	 * Closes connection with the database.
	 *
	 * @throws RuntimeException
	 */
	public static void closeConnection() throws RuntimeException {
		try {
			if (connection != null) {
				connection.close();
				connection = null;
			}
		} catch (SQLException ex) {
			throw new RuntimeException("Error connecting to the database", ex);
		}
	}
}
