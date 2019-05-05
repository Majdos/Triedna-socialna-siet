package sk.majo.maturita.controllers.rest.resources;

import java.util.Set;

import javax.validation.constraints.NotNull;

import org.springframework.hateoas.core.Relation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import sk.majo.maturita.database.models.Group;
import sk.majo.maturita.database.models.GroupMembership;
import sk.majo.maturita.database.models.SchoolUser;
import sk.majo.maturita.security.Permission;

/**
 * Creates group membership resource
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Relation(collectionRelation = "memberships")
public class GroupMembershipResource extends AbstractResource<GroupMembership> {

	@NotNull
	private SchoolUser user;
	
	@NotNull
	private Group group;
	
	@NotNull
	private Set<Permission> permissions;
	
	private int authorityLevel;
	
	private boolean invited;
	
	@Override
	public void init(GroupMembership entity) {
		user = entity.getUser();
		group = entity.getGroup();
		permissions = entity.getPermissions();
		authorityLevel = entity.getAuthorityLevel();
		invited = entity.getInvited();
	}

}
