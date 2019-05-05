package sk.majo.maturita.controllers.rest;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.PagedResources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import sk.majo.maturita.controllers.exceptions.NotFound;
import sk.majo.maturita.controllers.rest.assemblers.ArticleResourceAssembler;
import sk.majo.maturita.controllers.rest.resources.ArticleResource;
import sk.majo.maturita.database.models.Article;
import sk.majo.maturita.repositories.ArticleRepository;
import sk.majo.maturita.repositories.rest.RestGroupRepository;

/**
 * Modified rest endpoint. The endpoint returns articles by group and sorts them by publication date.
 * @author Majo
 *
 */
@RepositoryRestController
@ExposesResourceFor(Article.class)
@RequestMapping("/api/groups/{groupId}")
public class ArticleRest {

	@Autowired
	private ArticleRepository articleRepo;

	@Autowired
	RestGroupRepository groupRepo;
	
	@Autowired
	private ArticleResourceAssembler assembler;

	@Autowired
	private PagedResourcesAssembler<Article> pagedAssembler;
		
	@GetMapping("/articles/{id}")
	public @ResponseBody ArticleResource getArticle(@PathVariable Long groupId, @PathVariable Long id) {
		return assembler.toResource(articleRepo.findById(id).orElseThrow(NotFound::new));
	}
	
	@GetMapping(value = "/articles", produces = "application/hal+json")
	public @ResponseBody PagedResources<ArticleResource> getArticles(
			@PathVariable Long groupId, 
			@RequestParam(value = "timestamp", required = false) Optional<Timestamp> timestamp,
			Pageable pageable
			) {
		
		pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), new Sort(Sort.Direction.DESC, "publicationDate"));
		
		if (timestamp.isPresent()) {
			return pagedAssembler.toResource(articleRepo.findAllByGroupIdAndPublicationDateLessThanEqual(groupId, timestamp.get(), pageable), assembler);			
		}
		else {
			return pagedAssembler.toResource(articleRepo.findAllByGroupId(groupId, pageable), assembler);
		}	
	}	
}
