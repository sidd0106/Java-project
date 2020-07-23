package com.siddhartha.practice.Models;

/**
 * Stores employee data of a manager.
 *
 * @author sid
 */
public class Manager extends TeamMember {

	public Manager() {
	}

	public Manager(int id, String name) {
		super(id, name);
	}

	@Override
	public Designation getDesignation() {
		return Designation.Manager;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append("Manager{ id=")
				.append(id)
				.append(", name=")
				.append(name)
				.append(", designation=")
				.append(this.getDesignation())
				.append('}')
				.toString();
	}
}
