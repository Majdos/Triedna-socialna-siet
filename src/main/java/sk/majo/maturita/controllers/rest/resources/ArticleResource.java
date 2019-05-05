package sk.majo.maturita.controllers.rest.resources;

import java.sql.Timestamp;
import javax.validation.constraints.NotNull;

import org.springframework.hateoas.core.Relation;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import sk.majo.maturita.database.models.Article;
import sk.majo.maturita.database.models.Group;
import sk.majo.maturita.database.models.SchoolUser;
import sk.majo.maturita.helpers.AuthenticationHelper;

/**
 * Creates article resources
 * @author Marian Lorinc
 */
@Data
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(of = {})
@Relation(collectionRelation = "articles")
public class ArticleResource extends AbstractResource<Article> {

	@NotNull
	@Getter(AccessLevel.NONE)
	private Long id;
		
	@NotNull
	private SchoolUser author;

	@JsonIgnore
	@NotNull
	private Group group;
	
	@NotNull
	private Timestamp publicationDate;

	@NotNull
	private String header;
		
	@NotNull
	private String text;
			
	@NotNull
	private long commentsCount;
		
	@NotNull
	private boolean showAuthorControls;
	
	public void init(Article article) {
		id = article.getId();
		author = article.getAuthor();
		group = article.getGroup();
		publicationDate = article.getPublicationDate();
		header = article.getHeader();
		text = article.getText();
		commentsCount = article.getCommentsCount();
		showAuthorControls = article.isAuthor(AuthenticationHelper.getUserOptional().get());
	}	
}
