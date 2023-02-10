package lu.cascade.assessment.user.manager.api.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lu.cascade.assessment.user.manager.api.dto.UserForm;
import lu.cascade.assessment.user.manager.api.dto.UserStatus;
import lu.cascade.assessment.user.manager.api.model.UserEntity;
import lu.cascade.assessment.user.manager.api.repository.UserRepository;
import lu.cascade.assessment.user.manager.api.security.AESHelper;
import lu.cascade.assessment.user.manager.api.utils.UserManagerTechnicalException;
import lu.cascade.assessment.user.manager.api.utils.Utils;
import lu.cascade.assessment.user.manager.api.utils.UserManagerException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
@Slf4j
public class UserManagerService extends UserValidationService{
    private AESHelper aesHelper;
    private UserRepository userRepository;

    public void register(UserForm userForm){
        // validate inputs
        log.info("Validating User input");
        validateForRegistrationAndLogin(userForm);

        // check userName is not used
        log.info("Checking username is not used");
        if(userRepository.findByUsername(userForm.getUsername()).isPresent()){
            throw UserManagerException.builder().message("UserName already used").build();
        }

        // register the new user
        log.info("Registering new user");
        try{
            UserEntity newUser = UserEntity.builder()
                    .username(userForm.getUsername())
                    .passwordHash(Utils.hash(userForm.getPassword()))
                    .build();
            userRepository.save(newUser);
        }catch (UserManagerTechnicalException ex){
            throw UserManagerException.builder().message("Couldn't register the user").build();
        }
    }

    public String login(String appId, UserForm userForm){
        // validate inputs
        log.info("Validating User input");
        validateForRegistrationAndLogin(userForm);
        validateAppId(appId);

        // check userName exists in DB
        log.info("Checking username in DB");
        Optional<UserEntity> userOptional = userRepository.findByUsername(userForm.getUsername());
        if(userOptional.isEmpty()){
            throw UserManagerException.builder().message("Bad credentials").build();
        }

        // check password is matching
        log.info("Checking credentials");
        String passwordHashCalculated = Utils.hash(userForm.getPassword());
        String passwordHashInDB = userOptional.get().getPasswordHash();
        if(!passwordHashInDB.equals(passwordHashCalculated)){
            throw UserManagerException.builder().message("Bad credentials").build();
        }

        // the user is legit, let's generate an accessToken : appID + userID
        // this will allow to have multiple sessions within a browser
        log.info("Generating simple accessToken");
        String accessToken = appId + ":" + userOptional.get().getId();

        // encrypting the accessToken
        String encryptedAccessToken = aesHelper.encrypt(accessToken);

        log.debug("accessToken [{}] encrypted to [{}]", accessToken,encryptedAccessToken);
        return encryptedAccessToken;
    }

    public List<UserStatus> getUsers(long idUserPerformer){
        // check user can still get users
        userRepository.findById(idUserPerformer).filter(u -> !u.isDisabled())
                .orElseThrow(() -> UserManagerException.builder().message("User disabled or not found").build());
        // list of users
        Iterable<UserStatus> usersIterable = userRepository.findBy();
        return StreamSupport.stream(usersIterable.spliterator(), false).toList();
    }

    private void validateForRegistrationAndLogin(UserForm userForm){
        if(userForm == null){
            throw UserManagerException.builder().message("Registration details are empty").build();
        }else if(!userForm.isUserNameValid()){
            throw UserManagerException.builder().message("UserName is not valid").build();
        }else if(!userForm.isPasswordValid()){
            throw UserManagerException.builder().message("password is not valid").build();
        }
    }

    private void validateAppId(String appId){
        if(!StringUtils.hasText(appId)){
            throw UserManagerException.builder().message("AppId not provided").build();
        }else if(StringUtils.trimAllWhitespace(appId).length() < 5){
            throw UserManagerException.builder().message("AppId not valid").build();
        }
    }

}
