package sk.majo.maturita.repositories.rest;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import sk.majo.maturita.database.models.SchoolUser;
import sk.majo.maturita.database.models.projections.PreviewSchoolUser;
import sk.majo.maturita.security.rules.Superusers;

@RepositoryRestResource(path = "users", collectionResourceRel = "users", excerptProjection = PreviewSchoolUser.class)
public interface RestSchoolUserRepository extends CrudRepository<SchoolUser, Long> {

	@Override
	@RestResource(exported = false)
	<S extends SchoolUser> S save(S entity);

	@Override
	@PreAuthorize("principal.id == #id or hasRole('ROLE_SPSJM_ADMIN')")
	Optional<SchoolUser> findById(Long id);

	@Override
	@PostFilter("hasRole('ROLE_SPSJM_ADMIN') or filterObject == principal")
	Iterable<SchoolUser> findAll();
	
	@Override
	@Superusers
	void deleteById(Long id);

	@Override
	@Superusers
	void delete(SchoolUser entity);

	@Override
	@Superusers
	void deleteAll(Iterable<? extends SchoolUser> entities);

	@Override
	@Superusers
	void deleteAll();
}
