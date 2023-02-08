package lu.cascade.assessment.user.manager.api.repository;

import lu.cascade.assessment.user.manager.api.model.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {


    Optional<UserEntity> findByUserName(String userName);
}
