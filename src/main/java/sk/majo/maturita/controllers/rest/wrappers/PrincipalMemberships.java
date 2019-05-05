package sk.majo.maturita.controllers.rest.wrappers;

import java.util.List;

import org.springframework.hateoas.core.Relation;

import lombok.AllArgsConstructor;
import lombok.Data;
import sk.majo.maturita.controllers.rest.SchoolUserRest;
import sk.majo.maturita.controllers.rest.resources.GroupMembershipResource;

/**
 * Gets memberships of logged in user. Used by {@link SchoolUserRest#getMemberships(int, int, sk.majo.maturita.database.models.SchoolUser)
 * @author Marian Lorinc
 *
 */
@Data
@AllArgsConstructor
@Relation("memberships")
public class PrincipalMemberships {
	private List<GroupMembershipResource> memberships;
	/**
	 * Number of elements in {@link org.springframework.data.domain.Page}
	 */
	private long totalElements;
}
