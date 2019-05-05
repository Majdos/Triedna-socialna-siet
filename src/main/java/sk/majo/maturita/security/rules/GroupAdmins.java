package sk.majo.maturita.security.rules;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RUNTIME)
@Target({ TYPE, METHOD })
@PreAuthorize("@permissions.isAdmin(principal, #group) or hasRole('ROLE_SPSJM_ADMIN')")
public @interface GroupAdmins {

}
