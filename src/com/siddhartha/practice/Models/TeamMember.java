package com.siddhartha.practice.Models;

/**
 * Stores employee data of a TeamMember.
 *
 * @author sid
 */
public class TeamMember implements Employee {
	protected int id;
	protected String name;

	public TeamMember() {
	}

	public TeamMember(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append("TeamMember{")
				.append("id=")
				.append(id)
				.append(", name=")
				.append(name)
				.append(", designation=")
				.append(this.getDesignation())
				.append('}').toString();
	}

	@Override
	public Designation getDesignation() {
		return Designation.TeamMember;
	}
}
