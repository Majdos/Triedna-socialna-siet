package sk.majo.maturita.controllers.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadRequest extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BadRequest() {
	}

	public BadRequest(String arg0) {
		super(arg0);
	}

	public BadRequest(Throwable arg0) {
		super(arg0);
	}

	public BadRequest(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public BadRequest(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
