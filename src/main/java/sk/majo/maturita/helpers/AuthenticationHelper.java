package sk.majo.maturita.helpers;

import java.util.Optional;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import sk.majo.maturita.database.models.SchoolUser;

/**
 * Utility functions
 * @author Marian Lorinc
 */
public class AuthenticationHelper {

	public static boolean isAnonymous() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);
	}

	public static Optional<SchoolUser> getUserOptional() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !(auth.getPrincipal() instanceof SchoolUser)) {
			return Optional.ofNullable(null);
		}
		else {
			return Optional.of((SchoolUser) auth.getPrincipal());
		}
	}
	
	public static SchoolUser getUser() {
		return getUserOptional().get();
	}
	
}
