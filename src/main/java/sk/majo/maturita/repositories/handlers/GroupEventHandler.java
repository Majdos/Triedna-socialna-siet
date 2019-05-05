package sk.majo.maturita.repositories.handlers;

import java.nio.file.AccessDeniedException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;
import sk.majo.maturita.database.models.Group;
import sk.majo.maturita.database.models.SchoolUser;
import sk.majo.maturita.database.models.enums.GroupType;
import sk.majo.maturita.database.services.GroupService;
import sk.majo.maturita.helpers.AuthenticationHelper;

/**
 * @author Marian Lorinc
 */
@Component
@RepositoryEventHandler(Group.class)
public class GroupEventHandler {

	@Autowired
	private GroupService groupService;

	@HandleBeforeCreate
	public void beforeCreate(Group group) throws AccessDeniedException {
		Optional<SchoolUser> owner = AuthenticationHelper.getUserOptional();

		if (!owner.isPresent()) {
			throw new AccessDeniedException("Iba clenovia mozu vytvarat skupiny");
		}

		if (group.getType() == null) {
			group.setType(GroupType.PUBLIC_GROUP);
		}
	}

	/**
	 * Gives permissions to owner after the group was create
	 * @param group to be created
	 * @throws AccessDeniedException
	 */
	@HandleAfterCreate
	public void afterCreate(Group group) {
		SchoolUser owner = AuthenticationHelper.getUser();
		groupService.addSuperUser(owner, group);
	}

}
