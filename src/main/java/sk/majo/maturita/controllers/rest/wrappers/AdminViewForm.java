package sk.majo.maturita.controllers.rest.wrappers;

import java.util.Set;

import lombok.Data;
import lombok.NoArgsConstructor;
import sk.majo.maturita.security.Permission;

/**
 * DTO class used in add member form.
 * @author Marian Lorinc
 *
 */
@Data
@NoArgsConstructor
public class AdminViewForm {
	private String email;
	private Set<Permission> permissions;
	/**
	 * If value is set to true, then it is invite to group.
	 * Otherwise it is membership.
	 */
	private boolean invited;
}
