package sk.majo.maturita.repositories.rest;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import sk.majo.maturita.database.models.Group;
import sk.majo.maturita.database.models.GroupMembership;
import sk.majo.maturita.database.models.SchoolUser;
import sk.majo.maturita.database.models.enums.GroupType;
import sk.majo.maturita.database.models.pk.GroupMembershipPk;
import sk.majo.maturita.security.Permission;
import sk.majo.maturita.security.rules.Superusers;

import static sk.majo.maturita.security.rules.policies.GroupMembershipPolicy.*;

@RepositoryRestResource(collectionResourceRel = "groupMemberships", itemResourceRel = "groupMemberships")
public interface RestGroupMembershipRepository extends CrudRepository<GroupMembership, GroupMembershipPk> {

	@Override
	@PreAuthorize(DELETE)
	void delete(@Param("membership") GroupMembership entity);
	
	@Override
	@Superusers
	void deleteById(GroupMembershipPk id);
	
	@Override
	@PreAuthorize(SAVE)
	<S extends GroupMembership> S save(@Param("membership") S entity);
	
	@Override
	@PostAuthorize("principal == returnObject.get().user or @permissions.isMember(principal, returnObject.get()) or hasRole('ROLE_SPSJM_ADMIN')")
	Optional<GroupMembership> findById(@Param("id") GroupMembershipPk id);
	
	@Override
	@PostFilter("filterObject?.user == principal")
	Iterable<GroupMembership> findAll();

}
