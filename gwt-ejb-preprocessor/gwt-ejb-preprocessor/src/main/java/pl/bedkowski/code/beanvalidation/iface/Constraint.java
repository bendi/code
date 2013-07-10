package pl.bedkowski.code.beanvalidation.iface;

public interface Constraint {

	public boolean isValid(Object value);

	public String getErrorMessage();

}
