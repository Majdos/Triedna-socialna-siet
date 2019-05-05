package sk.majo.maturita.repositories.handlers;

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
import sk.majo.maturita.database.models.Group;
import sk.majo.maturita.helpers.AuthenticationHelper;

import static sk.majo.maturita.config.WebSocketConfiguration.MESSAGE_PREFIX;

import java.sql.Timestamp;

/**
 * Modifies entities and updates clients
 * @author Marian Lorinc
 *
 */
@Component
@RepositoryEventHandler(Article.class)
public class ArticleEventHandler {
	
	private final SimpMessagingTemplate websocket;
	private final EntityLinks entityLinks;
	
	@Autowired
	public ArticleEventHandler(SimpMessagingTemplate websocket, EntityLinks entityLinks) {
		this.websocket = websocket;
		this.entityLinks = entityLinks;
	}

	@HandleAfterCreate
	public void createdArticle(Article article) {
		this.websocket.convertAndSend(
				MESSAGE_PREFIX  + "/" + getPath(article.getGroup()) + "/newArticle", getPath(article));
	}

	@HandleAfterDelete
	public void deletedArticle(Article article) {
		this.websocket.convertAndSend(
				MESSAGE_PREFIX + "/" + getPath(article.getGroup()) + "/deleteArticle", getPath(article));
	}

	@HandleAfterSave
	public void updatedArticle(Article article) {
		this.websocket.convertAndSend(
				MESSAGE_PREFIX + "/" + getPath(article.getGroup()) + "/updateArticle", getPath(article));
	}

	@HandleBeforeCreate
	public void createArticle(Article article) {						
		if (article.getGroup() == null) {
			throw new BadRequest("Nevyplnili ste skupinu");
		}
		
		if(article.getAuthor() == null) {
			article.setAuthor(AuthenticationHelper.getUser());
		}
		if(article.getPublicationDate() == null) {
			article.setPublicationDate(new Timestamp(System.currentTimeMillis()));
		}
	}
	
	@HandleBeforeSave
	public void updateArticle(Article article) {			
		if(article.getAuthor() == null) {
			article.setAuthor(AuthenticationHelper.getUser());
		}
		if(article.getPublicationDate() == null) {
			article.setPublicationDate(new Timestamp(System.currentTimeMillis()));
		}
	}
			
	private String getPath(Group group) {
		return entityLinks.linkForSingleResource(Group.class, group).toUri().toString();
	}
	
	private String getPath(Article article) {
		return entityLinks.linkForSingleResource(article).toUri().toString();
	}
}
