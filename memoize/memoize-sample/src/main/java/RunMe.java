import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import pl.bedkowski.code.memoize.sample.WsContainer;

public class RunMe {

	private static Log log = LogFactory.getLog(RunMe.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		log.info("Started");

		BeanFactory bf = new FileSystemXmlApplicationContext(
				"classpath:spring.xml");

		WsContainer ws = bf.getBean(WsContainer.class);

		// this one should be cached
		for (int i = 0; i < 10; i++) {
			ws.run1();
		}

		// this one as well
		for (int i = 0; i < 10; i++) {
			ws.run2();
		}

		// this one not
		for (int i = 0; i < 10; i++) {
			ws.skipCache();
		}

		log.info("Finished");
	}

}
