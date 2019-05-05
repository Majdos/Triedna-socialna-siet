package sk.majo.maturita.repositories.handlers;

import static sk.majo.maturita.config.WebSocketConfiguration.MESSAGE_PREFIX;

import java.sql.Timestamp;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.hateoas.EntityLinks;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import sk.majo.maturita.controllers.exceptions.BadRequest;
import sk.majo.maturita.database.models.Article;
import sk.majo.maturita.database.models.ArticleComment;
import sk.majo.maturita.database.models.SchoolUser;
import sk.majo.maturita.helpers.AuthenticationHelper;

/**
 * Modifies entities and updates clients
 * @author Marian Lorinc
 *
 */
@Component
@RepositoryEventHandler(ArticleComment.class)
public class ArticleCommentEventHandler {

	private final SimpMessagingTemplate websocket;
	private final EntityLinks entityLinks;

	@Autowired
	public ArticleCommentEventHandler(SimpMessagingTemplate websocket, EntityLinks entityLinks) {
		this.websocket = websocket;
		this.entityLinks = entityLinks;
	}

	@HandleAfterCreate
	public void createdArticleComment(ArticleComment comment) {
		this.websocket.convertAndSend(
				MESSAGE_PREFIX + "/" + getPath(comment.getArticle(), "newComment"), getPath(comment));
	}

	@HandleAfterDelete
	public void deletedArticleComment(ArticleComment comment) {
		this.websocket.convertAndSend(
				MESSAGE_PREFIX + "/" + getPath(comment.getArticle(), "deleteComment"), getPath(comment));
	}

	@HandleAfterSave
	public void updatedArticleComment(ArticleComment comment) {
		this.websocket.convertAndSend(
				MESSAGE_PREFIX + "/" + getPath(comment.getArticle(), "updateComment"), getPath(comment));
	}

	@HandleBeforeCreate
	public void createArticleComment(ArticleComment comment) {
		Optional<SchoolUser> user = AuthenticationHelper.getUserOptional();
		if(comment.getAuthor() == null && user.isPresent()) {
			comment.setAuthor(user.get());
		}
		if(comment.getPostedTime() == null) {
			comment.setPostedTime(new Timestamp(System.currentTimeMillis()));
		}
	}
	
	@HandleBeforeSave
	public void updateArticleComment(ArticleComment comment) {						
		if(comment.getAuthor() == null) {
			throw new BadRequest("Missing author attribute");
		}
		if(comment.getPostedTime() == null) {
			comment.setPostedTime(new Timestamp(System.currentTimeMillis()));
		}
	}

	private String getPath(ArticleComment comment) {
		return entityLinks.linkForSingleResource(comment).toUri().toString();
	}

	private String getPath(Article article, String event) {
		return entityLinks.linkForSingleResource(Article.class, article).slash(event).toUri().toString();
	}
}
