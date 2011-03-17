package pl.bedkowski.code.memoize.sample;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import pl.bedkowski.code.memoize.annotation.Exclude;
import pl.bedkowski.code.memoize.annotation.Memoize;

@Component("wsClient")
@Memoize
public class WebServiceClientImpl implements WebServiceClient {

	@Override
	public List<String> lengthySearch(String someId) {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return Arrays.asList(new String[] { "a", "b", "c" });
	}

	@Exclude
	public List<String> excludedMethod() {
		return null;
	}

	@Override
	public void noCachingRequired() {
	}

	@Override
	public List<String> lengthySearch(String someId, String someId2) {
		try {
			Thread.sleep(8000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return Arrays.asList(new String[] { "a", "b", "c" });
	}

}
