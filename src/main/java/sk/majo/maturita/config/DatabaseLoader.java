package sk.majo.maturita.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import sk.majo.maturita.database.models.Group;
import sk.majo.maturita.database.models.SchoolUser;
import sk.majo.maturita.database.models.enums.GroupType;
import sk.majo.maturita.repositories.ArticleRepository;
import sk.majo.maturita.repositories.GroupRepository;
import sk.majo.maturita.repositories.SchoolUserRepository;
import sk.majo.maturita.repositories.rest.RestGroupRepository;
import sk.majo.maturita.repositories.rest.RestSchoolUserRepository;

/**
 * Creates default groups
 *
 * @author Marian Lorinc
 */
@Component
public class DatabaseLoader implements CommandLineRunner {

    @Autowired
    private GroupRepository groupRepository;


    @Override
    public void run(String... strings) throws Exception {
        if (!groupRepository.findByName(Group.GLOBAL_GROUP).isPresent()) {
            Group group = new Group(Group.GLOBAL_GROUP);
            group.setType(GroupType.PUBLIC_GROUP);
            groupRepository.save(group);
        }
    }
}
