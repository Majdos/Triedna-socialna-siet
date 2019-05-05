package sk.majo.maturita.security.rules.policies;

/**
 * The class stores basic security rules
 * @author Marian Lorinc
 */
public final class AuthorablePolicy {
	
	public static final String DELETE = "@permissions.canDeleteAuthorable(principal, #authorable) or hasRole('ROLE_SPSJM_ADMIN')";

	public static final String DELETE_FILTER = "@permissions.canDeleteAuthorable(principal,  filterObject) or hasRole('ROLE_SPSJM_ADMIN')";

	public static final String SAVE = "@permissions.canModifyAuthorable(principal,  #authorable) or hasRole('ROLE_SPSJM_ADMIN')";

	public static final String SAVE_FILTER = "@permissions.canModifyAuthorable(principal,  filterObject) or hasRole('ROLE_SPSJM_ADMIN')";

	public static final String GROUP_AUTHORABLE = "@permissions.isMember(principal, #authorable) or hasRole('ROLE_SPSJM_ADMIN')";

	public static final String GROUP_AUTHORABLE_FILTER = "@permissions.isMember(principal, filterObject) or hasRole('ROLE_SPSJM_ADMIN')";

}
