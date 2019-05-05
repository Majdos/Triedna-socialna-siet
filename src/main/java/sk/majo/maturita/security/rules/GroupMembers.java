package sk.majo.maturita.security.rules;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RUNTIME)
@PreAuthorize("@permissions.isMember(principal, #group) or hasRole('ROLE_SPSJM_ADMIN')")
public @interface GroupMembers {

}
