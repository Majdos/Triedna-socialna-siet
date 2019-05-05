package sk.majo.maturita.repositories.rest;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import sk.majo.maturita.database.models.Group;

@RepositoryRestResource(itemResourceRel = "groups", collectionResourceRel = "groups")
public interface RestGroupRepository extends CrudRepository<Group, Long> {
	
	@Override
	@PreAuthorize("#group.id == null or @permissions.isOwner(principal, #group) or hasRole('ROLE_SPSJM_ADMIN')")
	<S extends Group> S save(@P("group") S entity);

	@Override
	@PostAuthorize("@permissions.isMember(principal, returnObject.get()) or hasRole('ROLE_SPSJM_ADMIN')")
	Optional<Group> findById(Long id);

	@Override
	@PostFilter("@permissions.isMember(principal, filterObject) or hasRole('ROLE_SPSJM_ADMIN')")
	Iterable<Group> findAll();

	@Override
	@PostFilter("@permissions.isMember(principal, filterObject) or hasRole('ROLE_SPSJM_ADMIN')")
	Iterable<Group> findAllById(Iterable<Long> ids);

	@Override
	@PreAuthorize("@permissions.isOwner(principal, @restGroupRepository.findById(#id).get()) or hasRole('ROLE_SPSJM_ADMIN')")
	void deleteById(@P("id") Long id);

	@Override
	@PreAuthorize("@permissions.isOwner(principal, #group) or #group?.members.isEmpty() or hasRole('ROLE_SPSJM_ADMIN')")
	void delete(@P("group") Group entity);

	@RestResource(exported = false)
	@PostAuthorize("returnObject.isPresent() and @permissions.isMember(principal, returnObject.get()) or hasRole('ROLE_SPSJM_ADMIN')")
	Optional<Group> findByName(@Param("name") String name);
}
