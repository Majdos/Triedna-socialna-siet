package sk.majo.maturita.controllers.rest.resources;

import java.sql.Timestamp;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.springframework.hateoas.core.Relation;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import sk.majo.maturita.database.models.Article;
import sk.majo.maturita.database.models.ArticleComment;
import sk.majo.maturita.database.models.SchoolUser;
import sk.majo.maturita.helpers.AuthenticationHelper;

/**
 * Creates ArticleComment resource.
 * @author Marian Lorinc
 */
@Data
@EqualsAndHashCode(of = { "id" }, callSuper = false)
@ToString(of = {})
@NoArgsConstructor
@Relation(collectionRelation = "articleComments")
public class ArticleCommentResource extends AbstractResource<ArticleComment> {
	
	@NotNull
	@Getter(AccessLevel.NONE)
	private Long id;
	
	@NotNull
	private SchoolUser author;
	
	@JsonIgnore
	@NotNull
	private Article article;
	
	@NotNull
	private Timestamp postedTime;
	
	@NotNull
	private String content;

	/**
	 *
	 * @return true if user is allowed to modify comment, otherwise false
	 */
	public boolean getShowAuthorControls() {
		Optional<SchoolUser> user = AuthenticationHelper.getUserOptional();
		return user.isPresent() && user.get().equals(author);
	}	
	
	public void init(ArticleComment comment) {
		author = comment.getAuthor();
		article = comment.getArticle();
		postedTime = comment.getPostedTime();
		content = comment.getContent();
	}
}
