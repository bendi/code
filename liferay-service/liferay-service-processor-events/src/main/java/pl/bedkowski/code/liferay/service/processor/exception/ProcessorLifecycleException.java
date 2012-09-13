package pl.bedkowski.code.liferay.service.processor.exception;

public class ProcessorLifecycleException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = -702299132256783832L;

	public ProcessorLifecycleException() {
		super();
	}

	public ProcessorLifecycleException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ProcessorLifecycleException(String arg0) {
		super(arg0);
	}

	public ProcessorLifecycleException(Throwable arg0) {
		super(arg0);
	}

}
