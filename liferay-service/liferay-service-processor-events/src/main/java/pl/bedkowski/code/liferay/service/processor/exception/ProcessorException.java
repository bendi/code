package pl.bedkowski.code.liferay.service.processor.exception;

@SuppressWarnings("serial")
public class ProcessorException extends Exception {

	public ProcessorException() {
		super();
	}

	public ProcessorException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ProcessorException(String arg0) {
		super(arg0);
	}

	public ProcessorException(Throwable arg0) {
		super(arg0);
	}

}
