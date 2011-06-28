package pl.bedkowski.code.conditionalgetter;

public class MyBean {

	private String name, email, address;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	private boolean nameAvailable;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail(boolean encrypted) {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isNameAvailable() {
		return nameAvailable;
	}

	public void setNameAvailable(boolean nameAvailable) {
		this.nameAvailable = nameAvailable;
	}

}
