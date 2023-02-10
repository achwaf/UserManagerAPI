package lu.cascade.assessment.user.manager.api.service;

import lombok.extern.slf4j.Slf4j;
import lu.cascade.assessment.user.manager.api.repository.UserRepository;
import lu.cascade.assessment.user.manager.api.utils.UserManagerException;
import lu.cascade.assessment.user.manager.api.utils.Utils;

@Slf4j
public abstract class UserValidationService {

    protected UserRepository userRepository;

    public boolean isUserNameBase64AlreadyUsed(String userNameBase64){
        // decode the userNameBase64
        log.info("Decoding Username");
        String userName = Utils.fromBase64(userNameBase64);
        log.info("Username decoded to [{}]", userName);
        return isUserNameAlreadyUsed(userName);
    }

    public boolean isUserNameAlreadyUsed(String username){
        // check in the DB
        log.info("Checking existence in DB");
        return userRepository.findByUsername(username).isPresent();
    }

    protected void validateUserPerformer(long id){
        userRepository.findById(id).filter(u -> !u.isDisabled())
                .orElseThrow(() -> UserManagerException.builder().message("User disabled or not found").build());
    }

}
