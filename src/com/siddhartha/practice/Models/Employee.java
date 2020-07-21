package com.siddhartha.practice.Models;

import static com.siddhartha.practice.Models.Designation.Default;

/**
 * @author sid
 */
public interface Employee {
	default Designation getDesignation() {
		return Default;
	}
}
