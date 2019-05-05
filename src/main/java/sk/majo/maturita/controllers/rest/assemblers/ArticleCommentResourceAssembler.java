package sk.majo.maturita.controllers.rest.assemblers;

import org.springframework.stereotype.Component;
import sk.majo.maturita.controllers.rest.ArticleCommentRest;
import sk.majo.maturita.controllers.rest.resources.ArticleCommentResource;
import sk.majo.maturita.database.models.Article;
import sk.majo.maturita.database.models.ArticleComment;
import sk.majo.maturita.database.models.SchoolUser;

/**
 * Assembles ArticleComment entity to resource and links required entities
 * @author Marian Lorinc
 */
@Component
public class ArticleCommentResourceAssembler extends AbstractResourceAssembler<ArticleComment, ArticleCommentResource> {

	public ArticleCommentResourceAssembler() {
		super(ArticleCommentRest.class, ArticleCommentResource.class);
	}

	@Override
	public ArticleCommentResource toResource(ArticleComment entity) {
		ArticleCommentResource resource = createResourceWithId(entity.getId(), entity);
		resource.add(
				linkToSingleResource(Article.class, resource.getArticle()),
				linkToSingleResource(ArticleComment.class, entity),
				linkToSingleResource(SchoolUser.class, resource.getAuthor()).withRel("comments")
		);
		return resource;
	}
}
