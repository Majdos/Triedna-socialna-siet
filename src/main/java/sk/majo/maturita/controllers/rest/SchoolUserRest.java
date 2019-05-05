package sk.majo.maturita.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sk.majo.maturita.controllers.rest.assemblers.GroupMembershipAssembler;
import sk.majo.maturita.controllers.rest.resources.GroupMembershipResource;
import sk.majo.maturita.controllers.rest.wrappers.PrincipalMemberships;
import sk.majo.maturita.database.RangePageRequest;
import sk.majo.maturita.database.models.GroupMembership;
import sk.majo.maturita.database.models.SchoolUser;
import sk.majo.maturita.database.services.GroupMembershipService;
import sk.majo.maturita.repositories.GroupMembershipRepository;
import sk.majo.maturita.repositories.rest.RestGroupMembershipRepository;
import sk.majo.maturita.security.rules.GroupMembers;

/**
 * Endpoint which holds principal memberships data
 * @author Marian Lorinc
 *
 */
@RestController
public class SchoolUserRest {

	@Autowired
	private GroupMembershipAssembler assembler;

	@Autowired
	private PagedResourcesAssembler<GroupMembership> pagedAssembler;

	@Autowired
	private GroupMembershipService service;

	@Autowired
	private GroupMembershipRepository repo;

	/**
	 * Returns all memberships of logged in user
	 * @param pageable page metadata
	 * @param user logged in user
	 * @return memberships of logged in user
	 */
	@GetMapping(value = "/api/principal/memberships", produces = "application/hal+json")
	public PagedResources<GroupMembershipResource> getMemberships(Pageable pageable,
			@AuthenticationPrincipal SchoolUser user) {
		return pagedAssembler.toResource(service.findByUser(user, pageable), assembler);
	}

	@GetMapping(value = "/api/principal/memberships", produces = "application/hal+json", params = { "offset", "limit" })
	public Resource<PrincipalMemberships> getMemberships(@RequestParam int offset, @RequestParam int limit,
			@AuthenticationPrincipal SchoolUser user) {
			RangePageRequest page = new RangePageRequest(offset, limit);
			Page<GroupMembership> memberships = repo.findByUser(user, page);
			Resource<PrincipalMemberships> resource = new Resource<>(new PrincipalMemberships(assembler.toResources(memberships), memberships.getTotalElements()));
			return resource;
		
	}
}
