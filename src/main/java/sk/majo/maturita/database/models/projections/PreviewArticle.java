package sk.majo.maturita.database.models.projections;

import java.sql.Timestamp;

import org.springframework.data.rest.core.config.Projection;
import sk.majo.maturita.database.models.Article;
import sk.majo.maturita.database.models.SchoolUser;

@Projection(name = "preview", types = { Article.class })
public interface PreviewArticle {
	SchoolUser getAuthor();
	String getHeader();
	String getText();
	Timestamp getPublicationDate();
	long getCommentsCount();
	boolean getShowAuthorControls();
}
