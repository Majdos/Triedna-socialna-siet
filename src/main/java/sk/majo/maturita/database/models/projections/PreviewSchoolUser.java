package sk.majo.maturita.database.models.projections;

import org.springframework.data.rest.core.config.Projection;

import sk.majo.maturita.database.models.SchoolUser;

@Projection(name = "preview", types = { SchoolUser.class })
public interface PreviewSchoolUser {
	String getFirstname();
	String getLastname();
}
