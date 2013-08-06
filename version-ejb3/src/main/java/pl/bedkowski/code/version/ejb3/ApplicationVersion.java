package pl.bedkowski.code.version.ejb3;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * <p>
 * Bean holding reference to our version value and registering itself as MBean
 * </p>
 *
 */
public class ApplicationVersion implements ApplicationVersionMBean {

	public final static String MBEAN_NAME = "pl.bedkowski.code.version:type=ApplicationVersion";

	private String version;

	private MBeanServer mbeanServer;
	private ObjectName objectName;

	@Override
	public String getVersion() {
		return version;
	}

	/**
	 *
	 */
	public void postConstruct() {
		try {
			objectName = new ObjectName(MBEAN_NAME);
			mbeanServer = ManagementFactory.getPlatformMBeanServer();
			mbeanServer.registerMBean(this, objectName);
		} catch (Exception e) {
			throw new IllegalStateException(
					"Problem during registration of Monitoring into JMX:" + e);
		}
	}

	/**
	 *
	 */
	public void preDestroy() {
		try {
			mbeanServer.unregisterMBean(objectName);
		} catch (Exception e) {
			throw new IllegalStateException(
					"Problem during unregistration of Monitoring into JMX:" + e);
		}
	}

}
