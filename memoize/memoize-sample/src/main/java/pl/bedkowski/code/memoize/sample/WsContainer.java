package pl.bedkowski.code.memoize.sample;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WsContainer {

	@Autowired
	private WebServiceClient wsClient;

	public List<String> run1() {
		return wsClient.lengthySearch("run1");
	}

	public List<String> run2() {
		return wsClient.lengthySearch("run1", "run2");
	}

	public List<String> skipCache() {
		return wsClient.excludedMethod();
	}
}
