package sk.majo.maturita.database.services;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import sk.majo.maturita.controllers.exceptions.NotFound;
import sk.majo.maturita.database.models.Group;
import sk.majo.maturita.database.models.GroupMembership;
import sk.majo.maturita.database.models.SchoolUser;
import sk.majo.maturita.database.models.pk.GroupMembershipPk;
import sk.majo.maturita.repositories.GroupMembershipRepository;
import sk.majo.maturita.repositories.GroupRepository;
import sk.majo.maturita.repositories.SchoolUserRepository;
import sk.majo.maturita.repositories.rest.RestGroupMembershipRepository;
import sk.majo.maturita.repositories.rest.RestGroupRepository;
import sk.majo.maturita.repositories.rest.RestSchoolUserRepository;
import sk.majo.maturita.security.Permission;
import sk.majo.maturita.util.Sets;

/**
 * @author Marian Lorinc
 */
@Service
public class GroupMembershipService {
	
	@Autowired 
	private GroupMembershipRepository memberRepo;
	
	@Autowired
	private GroupRepository groupRepo;
	
	@Autowired
	private SchoolUserRepository userRepo;
	
	public GroupMembership save(GroupMembership membership) {
		return memberRepo.save(membership);
	}
	
	public GroupMembership save(String userEmail, Long groupId, Set<Permission> permissions) {
		return save(createByUserEmailAndGroupId(userEmail, groupId, permissions));
	}
	
	public boolean exists(String userEmail, Long groupId) {
		SchoolUser user = userRepo.findByEmail(userEmail).orElseThrow(NotFound::new);
		return memberRepo.existsById(new GroupMembershipPk(user.getId(), groupId));
	}
	
	public Page<GroupMembership> findAllByGroupId(Long groupId, Pageable pageable) {
		return memberRepo.findAllByGroupId(groupId, pageable);
	}
	
	public Page<GroupMembership> findByUser(SchoolUser user, Pageable pageable) {
		return memberRepo.findByUser(user, pageable);
	}
	
	public Stream<GroupMembership> readAllByGroupId(Long groupId) {
		return memberRepo.readAllByGroupId(groupId);
	}
	public Stream<GroupMembership> readAllByUser(SchoolUser user) {
		return memberRepo.readAllByUser(user);
	}
	
	public List<GroupMembership> findAllByGroupId(Long groupId) {
		return memberRepo.findAllByGroupId(groupId);
	}
	
	/**
	 * Creates GroupMembership object but it doesn't save to database
	 * @param userEmail email of user
	 * @param groupId group id
	 * @param permissions of user in group
	 * @return new membership
	 */
	public GroupMembership createByUserEmailAndGroupId(String userEmail, Long groupId, Set<Permission> permissions) {
		SchoolUser user = userRepo.findByEmail(userEmail).orElseThrow(NotFound::new);
		Group group = groupRepo.findById(groupId).orElseThrow(NotFound::new);
		return new GroupMembership(user, group, permissions);
	}
	
	/**
	 * @see {@link #createByUserEmailAndGroupId(String, Long, Set)}
	 */
	public GroupMembership createByUserEmailAndGroupId(String userEmail, Long groupId, Permission ...permissions) {
		return createByUserEmailAndGroupId(userEmail, groupId, Sets.of(permissions));
	}
	
}
