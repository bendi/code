package pl.bedkowski.code.jdd.validation;

public interface Constraint {

	public boolean isValid(Object value);

	public String getErrorMessage();

}
