package sk.majo.maturita.security.rules;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RUNTIME)
@Target(METHOD)
@PreAuthorize("true")
public @interface IgnorePreAuthorize {

}
