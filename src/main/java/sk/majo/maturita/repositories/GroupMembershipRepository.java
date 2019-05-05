package sk.majo.maturita.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;
import sk.majo.maturita.database.models.Group;
import sk.majo.maturita.database.models.GroupMembership;
import sk.majo.maturita.database.models.SchoolUser;
import sk.majo.maturita.database.models.enums.GroupType;
import sk.majo.maturita.database.models.pk.GroupMembershipPk;
import sk.majo.maturita.security.Permission;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Repository
public interface GroupMembershipRepository extends CrudRepository<GroupMembership, GroupMembershipPk> {
    Page<GroupMembership> findByUser(SchoolUser user, Pageable pageable);

    GroupMembership findOneByUserAndGroup(SchoolUser user, Group group);

    GroupMembership findOneByUserIdAndGroupName(Long id, String groupName);

    GroupMembership findOneByUserIdAndGroupId(Long userId, Long groupId);

    GroupMembership findOneByUserAndGroupType(SchoolUser user, GroupType type);

    List<GroupMembership> findAllByGroup(Group group);

    List<GroupMembership> findAllByGroupAndPermissionsIn(Group group, Collection<Permission> permission);

    List<GroupMembership> findAllByUserAndPermissionsIn(SchoolUser user, Collection<Permission> permission);

    List<GroupMembership> findAllByGroupId(Long id);

    Page<GroupMembership> findAllByGroupId(Long id, Pageable pageable);

    Page<GroupMembership> findAllByGroupAndUserNot(Group group, SchoolUser user, Pageable pageable);

    Stream<GroupMembership> readAllByGroupId(Long groupId);

    Stream<GroupMembership> readAllByUser(SchoolUser user);
}
