package lu.cascade.assessment.user.manager.api.repository;

import lu.cascade.assessment.user.manager.api.dto.UserStatus;
import lu.cascade.assessment.user.manager.api.model.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {


    /**
     * find a user by his username
     * @param userName the username to look for
     * @return the user matching the username
     */
    Optional<UserEntity> findByUsername(String userName);

    /**
     * using JPA projection to return only the username and disabled flag
     * @return list of user statuses
     */
    Collection<UserStatus> findByIdIsNot(long id);
}
