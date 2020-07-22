package com.siddhartha.practice.Service;

import com.siddhartha.practice.Runner.CLIRunner;


/**
 * PTLogAPI to log and view PT for employees with various options like view logs by employee
 * under manager or view logs from a range of date.
 */
public class PTLogAPI {
	public static void main(String[] args) {
		CLIRunner cliRunner = new CLIRunner();
		Thread ptLoggerThread = new Thread(cliRunner);
		ptLoggerThread.start();
	}
}
