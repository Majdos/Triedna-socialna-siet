package sk.majo.maturita.repositories;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;

import org.springframework.security.core.parameters.P;
import sk.majo.maturita.database.models.Article;
import sk.majo.maturita.database.models.Group;
import sk.majo.maturita.database.models.projections.PreviewArticle;
import sk.majo.maturita.security.rules.GroupMembers;
import sk.majo.maturita.security.rules.Superusers;
import sk.majo.maturita.security.rules.policies.AuthorablePolicy;

@RepositoryRestResource(excerptProjection = PreviewArticle.class)
public interface ArticleRepository extends PagingAndSortingRepository<Article, Long> {

	@Override
	@PreAuthorize(AuthorablePolicy.SAVE)
	<S extends Article> S save(@P("authorable") @Param("article") S entity);

	@Override
	@PostAuthorize("@permissions.isMember(principal, returnObject.get()) or hasRole('ROLE_SPSJM_ADMIN')")
	Optional<Article> findById(@Param("id") Long id);

	@Override
	@Superusers
	Page<Article> findAll(Pageable pageable);

	@Override
	@PreAuthorize("@permissions.canDeleteAuthorable(principal, @articleRepository.findById(#id).get()) or hasRole('ROLE_SPSJM_ADMIN')")
	void deleteById(@Param("id") Long id);

	@Override
	@PreAuthorize(AuthorablePolicy.DELETE)
	void delete(@P("authorable") @Param("article") Article article);

	@Override
	@PreFilter(AuthorablePolicy.DELETE_FILTER)
	void deleteAll(Iterable<? extends Article> entities);

	@RestResource(exported = false)
	@GroupMembers
	Page<Article> findAllByGroup(@P("group") Group group, Pageable page);

	@RestResource(exported = false)
	@GroupMembers
	Page<Article> findAllByGroupName(@P("group") String groupName, Pageable page);

	@RestResource(exported = false)
	@GroupMembers
	Page<Article> findAllByGroupId(@P("group") Long id, Pageable page);

	@RestResource(exported = false)
	@GroupMembers
	Page<Article> findAllByGroupNameAndPublicationDateLessThanEqual(@P("group") String groupName, Timestamp timestamp,
			Pageable page);

	@RestResource(exported = false)
	@GroupMembers
	Page<Article> findAllByGroupIdAndPublicationDateLessThanEqual(@P("group") Long groupId, Timestamp timestamp,
			Pageable page);
	
}
