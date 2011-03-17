package pl.bedkowski.code.memoize.sample;

import java.util.List;

public interface WebServiceClient {

	public List<String> lengthySearch(String someId);

	public List<String> lengthySearch(String someId, String someId2);

	public void noCachingRequired();

	public List<String> excludedMethod();
}
