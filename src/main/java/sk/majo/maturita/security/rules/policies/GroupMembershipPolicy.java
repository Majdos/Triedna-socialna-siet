package sk.majo.maturita.security.rules.policies;

/**
 * The class stores basic security rules
 * @author Marian Lorinc
 */
public final class GroupMembershipPolicy {

	public static final String DELETE = "principal == #membership?.user or @permissions.isAdmin(principal, #membership?.group) or hasRole('ROLE_SPSJM_ADMIN')";

	public static final String DELETE_FILTER = "principal == filterObject?.user or @permissions.isAdmin(principal, filterObject?.group) or hasRole('ROLE_SPSJM_ADMIN')";

	public static final String SAVE = "principal == #membership?.user or @permissions.isAdmin(principal, #membership?.group) or hasRole('ROLE_SPSJM_ADMIN')";

	public static final String SAVE_FILTER = "principal == filterObject?.user and filterObject.invited or @permissions.isAdmin(principal, filterObject?.group) or hasRole('ROLE_SPSJM_ADMIN')";

}
