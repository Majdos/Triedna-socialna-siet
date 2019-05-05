package sk.majo.maturita.security.rules;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RUNTIME)
@Target(METHOD)
@PreAuthorize("@permissions.isMember(principal, #entity?.authorable.group) or hasRole('ROLE_SPSJM_ADMIN')")
public @interface GroupAuthorable {

}
