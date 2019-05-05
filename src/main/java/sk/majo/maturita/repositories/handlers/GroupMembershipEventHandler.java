package sk.majo.maturita.repositories.handlers;

import static sk.majo.maturita.config.WebSocketConfiguration.MESSAGE_PREFIX;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.hateoas.EntityLinks;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import sk.majo.maturita.controllers.exceptions.BadRequest;
import sk.majo.maturita.database.models.Group;
import sk.majo.maturita.database.models.GroupMembership;
import sk.majo.maturita.database.models.SchoolUser;
import sk.majo.maturita.repositories.rest.RestGroupRepository;

/**
 * @author Marian Lorinc
 */
@Component
@RepositoryEventHandler(GroupMembership.class)
public class GroupMembershipEventHandler {
	
	@Autowired
	private RestGroupRepository repo;
	
	@Autowired
	private SimpMessagingTemplate websocket;
	
	@Autowired
	private EntityLinks entityLinks;
	
	@HandleBeforeCreate
	public void addMember(GroupMembership member) {						
		if (member.getGroup() == null) {
			throw new BadRequest("Nevyplnili ste skupinu");
		}
	}
	
	@HandleAfterCreate
	public void afterCreate(GroupMembership member) {
		websocket.convertAndSend(getChannel(member.getUser()) + "/newMembership", getPath(member));
	}
	
	/**
	 * Notifies user about being kicked out of group. If the user is last member of the group, the group deletes.
	 * @param member of the group
	 */
	@HandleAfterDelete
	public void afterDelete(GroupMembership member) {
		Group group = member.getGroup();
		if (group.getMembersCount() == 0) {
			repo.delete(group);
		}
		websocket.convertAndSend(getChannel(member.getUser()) + "/deleteMembership", getPath(member));
	}
	
	private String getPath(GroupMembership membership) {
		return entityLinks.linkForSingleResource(membership).toUri().toString();
	}
	
	/**
	 * Creates url for user websocket
	 * @param user who will be listening to websocket
	 * @return url url of websocket
	 */
	public static String getChannel(SchoolUser user) {
		return String.format("%s/user/%d", MESSAGE_PREFIX, user.getId());
	}
}
