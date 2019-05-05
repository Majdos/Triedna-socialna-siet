package sk.majo.maturita.controllers.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This exception is thrown when user tries to create new database record which already exists.
 * @author Marian Lorinc
 *
 */
@ResponseStatus(value = HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ConflictException() {
		super();
	}

	public ConflictException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConflictException(String message) {
		super(message);
	}

	public ConflictException(Throwable cause) {
		super(cause);
	}

}
