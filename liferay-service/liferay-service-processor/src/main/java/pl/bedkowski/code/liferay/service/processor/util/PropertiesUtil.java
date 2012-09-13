package pl.bedkowski.code.liferay.service.processor.util;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class PropertiesUtil {

	public static Properties load(String path, Class<?> clz) throws IOException {
		Properties props = new Properties();
		URL url = clz.getClassLoader().getResource(path);
		props.load(url.openStream());
		return props;
	}

}
