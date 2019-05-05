package sk.majo.maturita.database.models;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.*;
import sk.majo.maturita.database.models.markers.Authorable;
import sk.majo.maturita.database.models.markers.Groupable;
import sk.majo.maturita.helpers.AuthenticationHelper;

import org.hibernate.validator.constraints.Length;
import org.springframework.hateoas.Identifiable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import static javax.persistence.CascadeType.*;


/**
 * @author Marian Lorinc
 */
@Data
@EqualsAndHashCode(of = { "id" })
@ToString(of = { "author", "header", "publicationDate" })
@NoArgsConstructor

@Entity(name = "Articles")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Article implements Authorable<SchoolUser, Article>, Identifiable<Long>, Groupable {

	public static final int MAX_VIEWABLE_TEXT = 400;
	public static final int MIN_ARTICLE_TEXT = 5;
	public static final int MIN_HEADER_TEXT = 5;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
		
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private SchoolUser author;

	/**
	 * Group where article is shown
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Group group;
	
	@Column
	@NotNull
	private Timestamp publicationDate;

	@Column(length = 40)
	@NotBlank
	@Length(min = MIN_HEADER_TEXT, message = "Názov musí obsahovať aspoň " + MIN_HEADER_TEXT + " znakov")
	private String header;
		
	@Column(columnDefinition = "TEXT", nullable = false)
	@NotBlank
	@Length(min = MIN_ARTICLE_TEXT, message = "Príspevok musí obsahovať aspoň " + MIN_ARTICLE_TEXT + " znakov")
	private String text;
	
	@OrderBy("postedTime ASC")
	@OneToMany(mappedBy = "article", cascade = ALL, fetch = FetchType.LAZY)
	private List<ArticleComment> comments = new ArrayList<>();

	public Article(Timestamp publicationDate, String header, String text) {
		this.publicationDate = publicationDate;
		this.header = header;
		this.text = text;
	}

	public Article(SchoolUser author, Group group, Timestamp publicationDate, String header, String text) {
		this(publicationDate, header, text);
		author.addArticle(this);
		group.addArticle(this);
	}
			
	public void addComment(ArticleComment comment) {
		comments.add(comment);
		comment.setArticle(this);
	}
	
	public void removeComment(ArticleComment comment) {
		comments.remove(comment);
		comment.setArticle(null);
	}	
	
	@Override
	@JsonIgnore
	public boolean isAuthor(SchoolUser user) {
		return user != null && user.equals(author);
	}

	/**
	 *
	 * @return true if logged in user can manage article, otherwise false
	 */
	public boolean getShowAuthorControls() {
		return isAuthor(AuthenticationHelper.getUserOptional().get());
	}
	
	@JsonIgnore
	public long getCommentsCount() {
		return comments.size();
	}

	@JsonIgnore
	@Override
	public Article getAuthorable() {
		return this;
	}
	
}
