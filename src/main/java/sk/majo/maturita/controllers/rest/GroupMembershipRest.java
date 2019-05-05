package sk.majo.maturita.controllers.rest;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.PagedResources;
import org.springframework.web.bind.annotation.*;

import sk.majo.maturita.controllers.exceptions.ConflictException;
import sk.majo.maturita.controllers.rest.assemblers.GroupMembershipAssembler;
import sk.majo.maturita.controllers.rest.resources.GroupMembershipResource;
import sk.majo.maturita.controllers.rest.wrappers.AdminViewForm;
import sk.majo.maturita.database.RangePageRequest;
import sk.majo.maturita.database.models.Group;
import sk.majo.maturita.database.models.GroupMembership;
import sk.majo.maturita.database.services.GroupMembershipService;
import sk.majo.maturita.repositories.GroupMembershipRepository;
import sk.majo.maturita.repositories.handlers.GroupMembershipEventHandler;
import sk.majo.maturita.security.Permission;

/**
 * Manages group memberships and paging.
 *
 * @author Marian Lorinc
 */
@RepositoryRestController
@ExposesResourceFor(GroupMembership.class)
@RequestMapping("/api/groups/{groupId}")
public class GroupMembershipRest {

    @Autowired
    private GroupMembershipService memberService;

    @Autowired
    private GroupMembershipRepository membershipRepository;

    @Autowired
    private GroupMembershipAssembler assembler;

    @Autowired
    private PagedResourcesAssembler<GroupMembership> pagedAssembler;

    @Autowired
    private GroupMembershipEventHandler membershipEventHandler;

    /**
     * Pages group memberships and sends it back to client
     *
     * @param groupId  of group whose memberships will be filtered
     * @param pageable page metadata
     * @return json response
     */
    @GetMapping(value = "/members", produces = "application/hal+json")
    public @ResponseBody
    PagedResources<GroupMembershipResource> get(@PathVariable Long groupId, Pageable pageable) {
        return pagedAssembler.toResource(memberService.findAllByGroupId(groupId, pageable), assembler);
    }

    /**
     * Pages specific range of memberships
     *
     * @param groupId of group which memberships will be filtered
     * @param offset  bottom range of page
     * @param limit   number of elements to get fetched
     * @return number of memberships specified by limit
     */
    @GetMapping(value = "/members", produces = "application/hal+json", params = {"offset", "limit"})
    public @ResponseBody
    PagedResources<GroupMembershipResource> get(@PathVariable Long groupId,
                                                @RequestParam int offset, @RequestParam int limit) {
        Pageable pageable = new RangePageRequest(offset, limit);
        return pagedAssembler.toResource(memberService.findAllByGroupId(groupId, pageable), assembler);
    }

    /**
     * Return all memberships of groups
     *
     * @param groupId of group whose memberships will be filtered
     * @return all memberships of group identified by groupId
     */
    @GetMapping(value = "/members/all", produces = "application/hal+json")
    public @ResponseBody
    List<GroupMembershipResource> get(@PathVariable Long groupId) {
        return assembler.toResources(memberService.findAllByGroupId(groupId));
    }

    /**
     * Adds new member to group. If member already exists, the server return 409 status code
     *
     * @param groupId of group whose memberships will be filtered
     * @param member  new member to be added
     * @return json response, where we can find added member
     * @throws ConflictException if member is already in group
     */
    @PostMapping(value = "/members", produces = "application/hal+json")
    public @ResponseBody
    GroupMembershipResource post(@PathVariable Long groupId, @RequestBody AdminViewForm member) throws ConflictException {
        Set<Permission> perms = member.getPermissions();
        String email = member.getEmail();

        if (memberService.exists(email, groupId)) {
            throw new ConflictException("Clenstvo uz existuje");
        }

        GroupMembership membership = memberService.createByUserEmailAndGroupId(email, groupId, perms);
        membership.setInvited(member.isInvited());
        membership = memberService.save(membership);
        membershipEventHandler.afterCreate(membership);
        return assembler.toResource(membership);
    }
}
