package sk.majo.maturita.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import sk.majo.maturita.database.models.ArticleComment;
import sk.majo.maturita.database.models.projections.PreviewArticleComment;
import sk.majo.maturita.security.rules.Superusers;
import sk.majo.maturita.security.rules.policies.AuthorablePolicy;

import java.util.Optional;

@RepositoryRestResource(excerptProjection = PreviewArticleComment.class)
public interface ArticleCommentRepository extends PagingAndSortingRepository<ArticleComment, Long> {

	@Override
	@PostAuthorize("@permissions.isMember(principal, returnObject.get()) or hasRole('ROLE_SPSJM_ADMIN')")
	Optional<ArticleComment> findById(@Param("id") Long id);

	@Override
	@Superusers
	Page<ArticleComment> findAll(Pageable pageable);
			
	@Override
	@PreAuthorize(AuthorablePolicy.SAVE)
	<S extends ArticleComment> S save(@P("authorable") @Param("comment") S entity);

	@Override
	@PreAuthorize("@articleCommentRepository.findById(#id).get().isAuthor(principal) or @permissions.canDeleteAuthorable(principal, @articleCommentRepository.findById(#id).get()) or hasRole('ROLE_SPSJM_ADMIN')")
	void deleteById(@Param("id") Long id);

	@Override
	@PreAuthorize(AuthorablePolicy.DELETE)
	void delete(@Param("authorable") ArticleComment entity);

	@RestResource(exported = false)
	@PreAuthorize("@permissions.isMember(principal, @articleRepository.findById(#id).get().group) or hasRole('ROLE_SPSJM_ADMIN')")
    Page<ArticleComment> findAllByArticleId(@Param("id") Long id, Pageable pageable);	
}
