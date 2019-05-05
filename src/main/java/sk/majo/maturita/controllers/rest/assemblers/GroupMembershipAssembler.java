package sk.majo.maturita.controllers.rest.assemblers;

import org.springframework.stereotype.Component;

import sk.majo.maturita.controllers.rest.GroupMembershipRest;
import sk.majo.maturita.controllers.rest.resources.GroupMembershipResource;
import sk.majo.maturita.database.models.Group;
import sk.majo.maturita.database.models.GroupMembership;
import sk.majo.maturita.database.models.SchoolUser;

/**
 * Assembles GroupMembership entity to resource and links required entities
 * @author Marian Lorinc
 */
@Component
public class GroupMembershipAssembler extends AbstractResourceAssembler<GroupMembership, GroupMembershipResource> {

	public GroupMembershipAssembler() {
		super(GroupMembershipRest.class, GroupMembershipResource.class);
	}

	@Override
	public GroupMembershipResource toResource(GroupMembership entity) {
		GroupMembershipResource resource = createResourceWithId(entity);
		resource.add(
				linkToSingleResource(SchoolUser.class, entity.getUser()).withRel("user"),
				linkToSingleResource(Group.class, entity.getGroup())
		);
		return resource;
	}

}
