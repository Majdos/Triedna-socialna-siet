package sk.majo.maturita.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;
import sk.majo.maturita.database.models.SchoolUser;

import java.util.Optional;

@Repository
public interface SchoolUserRepository extends CrudRepository<SchoolUser, Long> {
    Optional<SchoolUser> findByEmail(@Param("email") String email);
}
