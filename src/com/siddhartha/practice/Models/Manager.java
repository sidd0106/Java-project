package com.siddhartha.practice.Models;

/**
 * Stores employee data of a manager.
 *
 * @author sid
 */
public class Manager extends TeamMember {

	@Override
	public Designation getDesignation() {
		return Designation.Manager;
	}

	@Override
	public String toString() {
		return "Manager{" +
				"id=" + id +
				", name='" + name + ", designation=" + this.getDesignation() + '\'' +
				'}';
	}
}
