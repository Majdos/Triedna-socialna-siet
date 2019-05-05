package sk.majo.maturita.database.models.markers;

import sk.majo.maturita.database.models.Group;

/**
 * Entity which implements this interface can be part of group
 * @author Marian Lorinc
 *
 */
public interface Groupable {
	Group getGroup();
}
