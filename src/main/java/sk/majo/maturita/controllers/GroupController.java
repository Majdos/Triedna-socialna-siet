package sk.majo.maturita.controllers;

import java.security.Principal;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Identifiable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import sk.majo.maturita.controllers.exceptions.NotFound;
import sk.majo.maturita.controllers.rest.GroupMembershipRest;
import sk.majo.maturita.database.models.Group;
import sk.majo.maturita.database.models.SchoolUser;
import sk.majo.maturita.database.services.GroupService;
import sk.majo.maturita.repositories.handlers.GroupMembershipEventHandler;
import sk.majo.maturita.security.Authority;
import sk.majo.maturita.security.Permission;
import sk.majo.maturita.security.rules.GroupAdmins;
import sk.majo.maturita.security.rules.GroupMembers;
import sk.majo.maturita.security.services.PermissionService;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

/**
 * @author Marian Lorinc
 *
 */
@Controller
public class GroupController {

	@Autowired
	private PermissionService permissions;

	@Autowired
	private GroupService groupService;

	@Autowired
	private EntityLinks entityLinks;

	/**
	 * Adds parameters to model which will be used in view
	 * @param model where parameters will be added
	 * @param user logged in user whose link will be in view
	 * @param group which user has recently requested. group link will be in view as well
	 */
	private void setupModel(Model model, SchoolUser user, Group group) {
		if (group == null) {
			throw new NotFound("Skupina neexistuje");
		}

		model.addAttribute("group", group);
		model.addAttribute("groupLink", getPath(group));
		
		// Workaround. Pole sa nedalo vygenerovat pomocou thymeleafu, tak sa pouzil velky string s delimiterom ','
		model.addAttribute("permissions", permissions.getPermissions(user, group).stream().map(Permission::name)
				.collect(Collectors.joining(", ")));
		model.addAttribute("user", user);
		model.addAttribute("userLink", getPath(user));
	}

	/**
	 * Renders view to user, if the user has sufficient permissions to view group content.
	 * @param model
	 * @param groupName which user is about to visit
	 * @param user logged in user
	 * @return view name
	 */
	@GroupMembers
	@GetMapping("/group/{groupName}")
	public String index(Model model, @P("group") @PathVariable("groupName") String groupName,
			@AuthenticationPrincipal SchoolUser user) {
		Group group = groupService.findByName(groupName);
		setupModel(model, user, group);
		return "group";
	}

	/**
	 * Renders group list view of logged in user.
	 * @param model
	 * @param user logged in user
	 * @return view name
	 */
	@GetMapping(value = "/groups")
	public String browseGroups(Model model, @AuthenticationPrincipal SchoolUser user, Principal principal) {
		model.addAttribute("user", user);
		model.addAttribute("channel", GroupMembershipEventHandler.getChannel(user));
		return "groupBrowser";
	}

	/**
	 * Renders primary page of user
	 * @param model
	 * @param user logged in user
	 * @return view name
	 */
	@GetMapping("/trieda")
	public String getClassView(Model model, @AuthenticationPrincipal SchoolUser user) {
		setupModel(model, user, groupService.getClassGroup(user));
		return "group";
	}

	/**
	 * Renders groups's admin view to user if the user is either admin or superuser.
	 * @param model
	 * @param groupName of group
	 * @param user logged in user
	 * @return view name
	 */
	@GroupAdmins
	@GetMapping("/group/{groupName}/admin")
	public String adminPage(Model model, @P("group") @PathVariable("groupName") String groupName,
			@AuthenticationPrincipal SchoolUser user) {
		Group group = groupService.findByName(groupName);
		setupModel(model, user, group);

		model.addAttribute("apiLink",
				linkTo(methodOn(GroupMembershipRest.class).get(group.getId(), null)).toUri().toString());
		model.addAttribute("allPermissions", Authority.listPermissionsToString());
		return "group_admin";
	}

	/**
	 * Returns api link to entity
	 * @param identifiable entity which has id {@link Identifiable}
	 * @return url to entity
	 */
	private String getPath(Identifiable<?> identifiable) {
		return entityLinks.linkToSingleResource(identifiable).getHref();
	}

}
