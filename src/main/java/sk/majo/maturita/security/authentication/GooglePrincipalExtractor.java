package sk.majo.maturita.security.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.stereotype.Component;
import sk.majo.maturita.database.models.Group;
import sk.majo.maturita.database.models.GroupMembership;
import sk.majo.maturita.database.models.SchoolUser;
import sk.majo.maturita.database.models.definitions.GoogleUser;
import sk.majo.maturita.repositories.GroupMembershipRepository;
import sk.majo.maturita.repositories.GroupRepository;
import sk.majo.maturita.repositories.SchoolUserRepository;
import sk.majo.maturita.repositories.rest.RestSchoolUserRepository;
import sk.majo.maturita.security.Permission;
import sk.majo.maturita.util.Sets;

import java.util.Map;
import java.util.Optional;

/**
 * @author Marian Lorinc
 */
@Component
public class GooglePrincipalExtractor implements PrincipalExtractor {

    public static final String TRUSTED_USER = "TRUSTED";

    @Autowired
    private SchoolUserRepository repo;

    @Autowired
    private GroupMembershipRepository membershipRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private DomainConfiguration domainConfiguration;

    /**
     * Adds user to global group
     * @param user to be added to the global group
     */
    private void joinDefaultGroups(SchoolUser user) {
        membershipRepository.save(new GroupMembership(user, groupRepository.findByName(Group.GLOBAL_GROUP).get(), Sets.of(Permission.READ)));
    }

    /**
     * Handles the log in. If user is already registered there is not much to do. On other side if user visits for
     * first time, the user is saved to database and joins global group by default.
     * @param map
     * @return SchoolUser user
     */
    @Override
    public Object extractPrincipal(Map<String, Object> map) {
        GoogleUser googleUser = GoogleUser.fromUserInfoMap(map);
        Optional<SchoolUser> user = repo.findByEmail(googleUser.getEmail());
        SchoolUser principal;

        if (!user.isPresent()) {
            principal = new SchoolUser(googleUser);
            principal = repo.save(principal);
            joinDefaultGroups(principal);
        }
        else {
            principal = user.get();
        }

        if (domainConfiguration.getAllowedDomains().contains(googleUser.getHostDomain())) {
            principal.setEnabled(true);
            principal = repo.save(principal);
        }

        return principal;
    }
}
