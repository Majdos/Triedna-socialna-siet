package sk.majo.maturita.database.models.markers;

import sk.majo.maturita.database.models.SchoolUser;

/**
 * Entity which implements this interface can be created by user
 * @author Marian Lorinc
 *
 * @param <T> entity type of author
 * @param <D> entity which can be created by user
 */
public interface Authorable<T extends SchoolUser, D> extends Groupable {
	boolean isAuthor(T user);

	/**
	 *
	 * @return author
	 */
	T getAuthor();

	/**
	 *
	 * @return object, which was created by author
	 */
	D getAuthorable();
}
