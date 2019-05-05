package sk.majo.maturita.security.services;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.NoArgsConstructor;
import sk.majo.maturita.controllers.exceptions.NotFound;
import sk.majo.maturita.database.models.Group;
import sk.majo.maturita.database.models.GroupMembership;
import sk.majo.maturita.database.models.SchoolUser;
import sk.majo.maturita.database.models.markers.Authorable;
import sk.majo.maturita.database.models.markers.Groupable;
import sk.majo.maturita.helpers.AuthenticationHelper;
import sk.majo.maturita.repositories.GroupMembershipRepository;
import sk.majo.maturita.repositories.rest.RestGroupMembershipRepository;
import sk.majo.maturita.security.Authority;
import sk.majo.maturita.security.Permission;

/**
 * Utility service which simplifies authorization operations in repositories.
 * @author Majo
 *
 */
@Service("permissions")
@NoArgsConstructor
public class PermissionService {

	@Autowired
	private GroupMembershipRepository memberRepo;

	public static final String SPSJM_AUTHORITY = "ROLE_SPSJM_ADMIN";

	private Optional<Authority> getAuthority(GroupMembership membership) {
		if (membership == null || membership.getInvited()) {
			return Optional.empty();
		}
		return Optional.of(new Authority(membership.getPermissions()));
	}

	private Optional<Authority> getAuthority(SchoolUser user, Long groupId) {
		return getAuthority(memberRepo.findOneByUserIdAndGroupId(user.getId(), groupId));
	}

	private Optional<Authority> getAuthority(SchoolUser user, String groupName) {
		return getAuthority(memberRepo.findOneByUserIdAndGroupName(user.getId(), groupName));
	}

	private Optional<Authority> getAuthority(SchoolUser user, Group group) {
		return getAuthority(user, group.getId());
	}

	public boolean isOwner(SchoolUser user, String groupName) {
		return getAuthority(user, groupName).filter(x -> x.has(Permission.ROOT)).isPresent();
	}

	public boolean isOwner(SchoolUser user, Long groupId) {
		return getAuthority(user, groupId).filter(x -> x.has(Permission.ROOT)).isPresent();
	}

	public boolean isOwner(SchoolUser user, Group group) {
		return isOwner(user, group.getId());
	}

	public boolean isAdmin(SchoolUser user, String groupName) {
		return getAuthority(user, groupName).filter(x -> x.has(Permission.ADMIN)).isPresent();
	}

	public boolean isAdmin(SchoolUser user, Long groupId) {
		return getAuthority(user, groupId).filter(x -> x.has(Permission.ADMIN)).isPresent();
	}

	public boolean isAdmin(SchoolUser user, Group group) {
		return isAdmin(user, group.getId());
	}
	
	public boolean isMember(SchoolUser user, String groupName) {
		return getAuthority(user, groupName).isPresent();
	}

	public boolean isMember(SchoolUser user, Long groupId) {
		return getAuthority(user, groupId).isPresent();
	}

	public boolean isMember(SchoolUser user, Group group) {
		return isMember(user, group.getId());
	}
	
	public boolean isMember(SchoolUser user, Groupable groupable) {
		return isMember(user, groupable.getGroup());
	}

	public boolean canWrite(SchoolUser user, String groupName) {
		return getAuthority(user, groupName).filter(x -> x.has(Permission.WRITE)).isPresent();
	}

	public boolean canWrite(SchoolUser user, Long groupId) {
		return getAuthority(user, groupId).filter(x -> x.has(Permission.WRITE)).isPresent();
	}

	public boolean canWrite(SchoolUser user, Group group) {
		return canWrite(user, group.getId());
	}
	
	public boolean canWrite(SchoolUser user, Authorable<?, ?> authorable) {
		return canWrite(user, authorable.getGroup());
	}
	
	public boolean canWrite(SchoolUser user, Groupable groupable) {
		return canWrite(user, groupable.getGroup());
	}

	public boolean canDeleteAuthorable(SchoolUser user, Authorable<SchoolUser, ?> authorable) {
		Group group = authorable.getGroup();
		GroupMembership userMembership = memberRepo.findOneByUserAndGroup(user, group);

		if (userMembership == null) {
			return false;
		}

		GroupMembership authorMembership = memberRepo.findOneByUserAndGroup(authorable.getAuthor(), group);
		return authorable.isAuthor(user) || userMembership.getAuthorityLevel() > authorMembership.getAuthorityLevel();
	}
	
	public boolean canModifyAuthorable(SchoolUser user, Authorable<SchoolUser, ?> authorable) {
		return isAuthor(user, authorable) && canWrite(user, authorable);
	}

	public boolean canCreateAuthorable(SchoolUser user, Authorable<SchoolUser, ?> authorable) {
		return isMember(user, authorable) && canWrite(user, authorable);
	}
	
	public Set<Permission> getPermissions(SchoolUser user, Group group) {
		if (user.getRoleList().contains(SPSJM_AUTHORITY)) {
			return new Authority(Permission.ROOT).getPermissions();
		}
		return getAuthority(user, group.getId()).orElseThrow(NotFound::new).getPermissions();
	}

	public boolean isAuthor(SchoolUser user, Authorable<SchoolUser, ?> authorable) {
		return authorable.isAuthor(user);
	}
	
	public boolean isAuthor(Authorable<SchoolUser, ?> authorable) {
		return isAuthor(AuthenticationHelper.getUser(), authorable);
	}
	
}
