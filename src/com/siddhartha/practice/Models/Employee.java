package com.siddhartha.practice.Models;

import static com.siddhartha.practice.Models.Designation.Default;

/**
 * Employee interface to depict required behaviors of employee classes.
 * @author sid
 */
public interface Employee {
	default Designation getDesignation() {
		return Default;
	}

	int getId();

	String getName();

	void setId(int id);

	void setName(String name);
}
