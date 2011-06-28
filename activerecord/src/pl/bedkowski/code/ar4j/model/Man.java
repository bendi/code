package pl.bedkowski.code.ar4j.model;

import pl.bedkowski.code.ar4j.ActiveRecord;

@ActiveRecord
public class Man extends Person {
	private int height;

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

}
