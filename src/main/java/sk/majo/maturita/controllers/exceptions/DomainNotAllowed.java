package sk.majo.maturita.controllers.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class DomainNotAllowed extends RuntimeException {
    public DomainNotAllowed() {
    }

    public DomainNotAllowed(String message) {
        super(message);
    }

    public DomainNotAllowed(String message, Throwable cause) {
        super(message, cause);
    }

    public DomainNotAllowed(Throwable cause) {
        super(cause);
    }
}
