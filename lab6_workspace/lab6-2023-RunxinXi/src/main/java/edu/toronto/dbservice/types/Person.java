package edu.toronto.dbservice.types;

import java.io.Serializable;

public class Person implements Serializable {
	public String name;
	public int salary;
	public int newsalary;
	public int dependent;
	
	public Person(String n, int v) {
		this.name = n;
		this.salary = v;
	}
	
	public String getName() {
		return name;
	}

	public int getDependent() {
		return dependent;
	}
}