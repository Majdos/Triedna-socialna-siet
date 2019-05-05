package sk.majo.maturita.database.models.projections;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import sk.majo.maturita.database.models.ArticleComment;
import sk.majo.maturita.database.models.SchoolUser;

@Projection(name = "preview", types = { ArticleComment.class })
public interface PreviewArticleComment {
	SchoolUser getAuthor();
	Timestamp getPostedTime();
	String getContent();
	
	@Value("#{@permissions.isAuthor(target)}")
	boolean getShowAuthorControls();
}
