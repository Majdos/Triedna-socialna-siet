package sk.majo.maturita.helpers;

import java.security.Principal;

import org.springframework.security.core.Authentication;

/**
 * @author Marian Lorinc
 */
public class Redirects {

	/**
	 * Redirects logged in user to another view. Anonymous users are redirected to login form.
	 * @param principal logged in user
	 * @param loginTemplate login view name
	 * @param next view name to be redirected to
	 * @return view name
	 */
	public static String redirectIfLoggedIn(Principal principal, String loginTemplate, String next) {
		if (principal != null && ((Authentication) principal).isAuthenticated()) {
			return "redirect:" + next;
		}
		return loginTemplate;
	}

}
