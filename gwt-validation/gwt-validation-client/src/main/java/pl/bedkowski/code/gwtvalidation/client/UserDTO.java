package pl.bedkowski.code.gwtvalidation.client;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import pl.bedkowski.code.beanvalidation.preprocessor.BeanValidation;

@BeanValidation
public class UserDTO implements DTO {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public enum Sex{Male, Female};

	@NotNull
	@Size(min = 1)
	private String firstName;

	@NotNull
	@Size(min = 1)
	private String lastName;

	@NotNull
	private Sex sex;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	@Size(max = 250)
	private String jobTitle;

}
