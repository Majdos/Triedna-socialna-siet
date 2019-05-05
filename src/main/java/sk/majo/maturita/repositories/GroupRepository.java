package sk.majo.maturita.repositories;

import org.springframework.data.repository.CrudRepository;
import sk.majo.maturita.database.models.Group;

import java.util.Optional;

public interface GroupRepository extends CrudRepository<Group, Long> {
    Optional<Group> findByName(String groupName);
}
