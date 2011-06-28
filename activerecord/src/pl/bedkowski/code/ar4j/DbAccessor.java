package pl.bedkowski.code.ar4j;

import pl.bedkowski.code.ar4j.iface.ActiveRecord;


public interface DbAccessor  {

	/**
	 * This method will get a reference to the object and 
	 * initialize it based on its properties and database state
	 * 
	 * @param target
	 */
	public void  init(ActiveRecord target);
	public void save(ActiveRecord target);
	public void delete(ActiveRecord target);
}
