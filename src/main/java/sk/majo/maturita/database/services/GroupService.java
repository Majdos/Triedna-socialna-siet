package sk.majo.maturita.database.services;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sk.majo.maturita.controllers.exceptions.NotFound;
import sk.majo.maturita.database.models.Group;
import sk.majo.maturita.database.models.GroupMembership;
import sk.majo.maturita.database.models.SchoolUser;
import sk.majo.maturita.database.models.enums.GroupType;
import sk.majo.maturita.repositories.GroupMembershipRepository;
import sk.majo.maturita.repositories.GroupRepository;
import sk.majo.maturita.repositories.rest.RestGroupMembershipRepository;
import sk.majo.maturita.repositories.rest.RestGroupRepository;
import sk.majo.maturita.security.Permission;
import sk.majo.maturita.util.Sets;

/**
 * @author Marian Lorinc
 */
@Service("groupService")
public class GroupService {

	@Autowired
	private GroupMembershipRepository memberRepo;
	
	@Autowired
	private GroupRepository groupRepo;

	public Group findByName(String name) {
		return groupRepo.findByName(name).orElseThrow(NotFound::new);
	}

	public Group findById(Long id) {
		return groupRepo.findById(id).orElseThrow(NotFound::new);
	}

	/**
	 * Returns primary group of uer
	 * @param user
	 * @return primary group of user
	 */
	public Group getClassGroup(SchoolUser user) {
		GroupMembership membership = memberRepo.findOneByUserAndGroupType(user, GroupType.CLASS_GROUP);
		
		if (membership == null) {
			throw new NotFound("Trieda nebola najdena");
		}
		
		return membership.getGroup();
	}
	
	public boolean hasMember(SchoolUser user, Group group) {
		return memberRepo.findOneByUserIdAndGroupName(user.getId(), group.getName()) != null;
	}
	
	public GroupMembership addMember(SchoolUser user, Group group, Permission ...permissions) {
		return memberRepo.save(new GroupMembership(user, group, Sets.of(permissions)));
	}
	
	public GroupMembership addSuperUser(SchoolUser user, Group group) {
		return memberRepo.save(new GroupMembership(user, group, Sets.of(Permission.ROOT)));
	}
	
	public void removeMember(SchoolUser user, Group group) {
		GroupMembership memShip = memberRepo.findOneByUserAndGroup(user, group);
		user.getGroupMemberships().remove(memShip);		
		group.getMembers().remove(memShip);
		user.getGroupMemberships().remove(memShip);
		memShip.setGroup(null);
		memShip.setUser(null);
		groupRepo.save(group);
	}


	/**
	 * Returns all memberships except the user.
	 * @param user excluded user from search
	 * @param group where we are looking for
	 * @param pageable page metadata
	 * @return group memberships
	 */
	public Page<GroupMembership> getMembersBut(SchoolUser user, Group group, Pageable pageable) {
		return memberRepo.findAllByGroupAndUserNot(group, user, pageable);
	}
	
	public List<SchoolUser> getAdmins(Group group) {
		return memberRepo.findAllByGroupAndPermissionsIn(group, Arrays.asList(Permission.ADMIN))
				.stream()
				.map(GroupMembership::getUser)
				.collect(Collectors.toList());
	}

	public List<SchoolUser> getMembers(Group group) {
		return memberRepo.findAllByGroup(group)
				.stream()
				.map(GroupMembership::getUser)
				.collect(Collectors.toList());
	}

	public SchoolUser getOwner(Group group) throws IllegalStateException {
		return memberRepo.findAllByGroupAndPermissionsIn(group, Arrays.asList(Permission.ROOT))
				.stream()
				.findFirst()
				.map(GroupMembership::getUser)
				.orElseThrow(() -> new IllegalStateException("nasli sa viaceri rootovia v skupine " + group.getName()));
	}
}
