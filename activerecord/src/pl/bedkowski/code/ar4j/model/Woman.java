package pl.bedkowski.code.ar4j.model;

import pl.bedkowski.code.ar4j.ActiveRecord;

@ActiveRecord
public class Woman extends Person {

	private String name = "Default name";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}