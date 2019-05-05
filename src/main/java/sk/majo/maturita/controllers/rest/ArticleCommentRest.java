package sk.majo.maturita.controllers.rest;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import sk.majo.maturita.controllers.rest.assemblers.ArticleCommentResourceAssembler;
import sk.majo.maturita.controllers.rest.resources.ArticleCommentResource;
import sk.majo.maturita.database.models.ArticleComment;
import sk.majo.maturita.repositories.ArticleCommentRepository;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Modified rest endpoint. The endpoint returns sorted comments by time from recent to older.
 * @author Marian Lorinc
 *
 */
@RepositoryRestController
@ExposesResourceFor(ArticleComment.class)
@RequestMapping("/api/articles/{id}")
public class ArticleCommentRest {

	@Autowired
	private ArticleCommentRepository repo;

	@Autowired
	private ArticleCommentResourceAssembler assembler;

	@Autowired
	private PagedResourcesAssembler<ArticleComment> pagedAssembler;

	@GetMapping(value = "/comments", produces = "application/hal+json")
	public @ResponseBody PagedResources<ArticleCommentResource> getComments(@PathVariable Long id, Pageable pageable) {
		pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), new Sort(Sort.Direction.ASC, "postedTime"));
		return pagedAssembler.toResource(repo.findAllByArticleId(id, pageable), assembler);
	}
	
	@DeleteMapping("/comments")
	public ResponseEntity<Void> deleteComments(@PathVariable Long id) {
		if (repo.existsById(id)) {
			repo.deleteById(id);
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.noContent().build();
	}
}
