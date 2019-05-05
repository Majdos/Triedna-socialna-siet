package sk.majo.maturita.controllers.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFound extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NotFound() {
	}

	public NotFound(String arg0) {
		super(arg0);
	}

	public NotFound(Throwable arg0) {
		super(arg0);
	}

	public NotFound(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public NotFound(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
