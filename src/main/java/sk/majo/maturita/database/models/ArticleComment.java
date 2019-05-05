package sk.majo.maturita.database.models;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.springframework.hateoas.Identifiable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import sk.majo.maturita.database.models.markers.Authorable;

/**
 * @author Marian Lorinc
 */
@Data
@EqualsAndHashCode(exclude = { "content" })
@NoArgsConstructor

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArticleComment implements Authorable<SchoolUser, ArticleComment>, Identifiable<Long> {
	
	public static final int MIN_COMMENT_LENGTH = 2;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private SchoolUser author;

	/**
	 * Related article
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Article article;
	
	@NotNull
	@Column(nullable = false)
	private Timestamp postedTime;
	
	@Column(columnDefinition = "TEXT", nullable = false)
	@NotBlank
	@Length(min = MIN_COMMENT_LENGTH, message = "Komentár musí obsahovať aspoň " + MIN_COMMENT_LENGTH + " znakov")
	private String content;
	
	@Override
	@JsonIgnore
	public boolean isAuthor(SchoolUser user) {
		return user != null && user.equals(author);
	}

	@JsonIgnore
	@Override
	public Group getGroup() {
		return article.getGroup();
	}

	@JsonIgnore
	@Override
	public ArticleComment getAuthorable() {
		return this;
	}
}
