package pl.bedkowski.code.version.ejb3;

/**
 * <p>
 * Interface reponsible for exposing application version.
 * </p>
 *
 * @author marek.bedkowski
 *
 */
public interface ApplicationVersionMBean {

	/**
	 *
	 * @return
	 */
	public String getVersion();
}
