package sk.majo.maturita.controllers.rest.assemblers;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import sk.majo.maturita.controllers.rest.ArticleCommentRest;
import sk.majo.maturita.controllers.rest.ArticleRest;
import sk.majo.maturita.controllers.rest.resources.ArticleResource;
import sk.majo.maturita.database.models.Article;
import sk.majo.maturita.database.models.Group;
import sk.majo.maturita.database.models.SchoolUser;

/**
 * Assembles Article entity to resource and links required entities
 * @author Marian Lorinc
 */
@Component
public class ArticleResourceAssembler extends AbstractResourceAssembler<Article, ArticleResource> {
	
	public ArticleResourceAssembler() {
		super(ArticleRest.class, ArticleResource.class);
	}

	@Override
	public ArticleResource toResource(Article entity) {
		ArticleResource resource = createResourceWithId(entity.getId(), entity);
		resource.add(
				linkToSingleResource(Article.class, entity),
				linkToSingleResource(SchoolUser.class, resource.getAuthor()).withRel("author"),
				linkToCommentResource(entity),
				linkToSingleResource(Group.class, resource.getGroup())
		);
		return resource;
	}

	/**
	 * Links comments to article
	 * @param entity - an Article entity
	 * @return link to comments
	 */
	private Link linkToCommentResource(Article entity) {
		return linkTo(methodOn(ArticleCommentRest.class).getComments(entity.getId(), null)).withRel("comments");
	}	
}
