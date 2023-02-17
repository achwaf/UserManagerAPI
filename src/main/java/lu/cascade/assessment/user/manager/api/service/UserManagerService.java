package lu.cascade.assessment.user.manager.api.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lu.cascade.assessment.user.manager.api.dto.UserForm;
import lu.cascade.assessment.user.manager.api.dto.UserLoginResult;
import lu.cascade.assessment.user.manager.api.dto.UserStatus;
import lu.cascade.assessment.user.manager.api.model.UserEntity;
import lu.cascade.assessment.user.manager.api.repository.UserRepository;
import lu.cascade.assessment.user.manager.api.security.AESHelper;
import lu.cascade.assessment.user.manager.api.utils.UserManagerTechnicalException;
import lu.cascade.assessment.user.manager.api.utils.Utils;
import lu.cascade.assessment.user.manager.api.utils.UserManagerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class UserManagerService extends UserValidationService{
    private AESHelper aesHelper;

    @Autowired
    public UserManagerService(UserRepository userRepository, AESHelper aesHelper) {
        super(userRepository);
        this.aesHelper = aesHelper;
    }

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
                    .avatar(userForm.getAvatar())
                    .build();
            userRepository.save(newUser);
        }catch (UserManagerTechnicalException ex){
            throw UserManagerException.builder().message("Couldn't register the user").build();
        }
    }

    public UserLoginResult login(String appId, UserForm userForm){
        // validate inputs
        log.info("Validating User input");
        validateForRegistrationAndLogin(userForm);
        validateAppId(appId);

        // check userName exists in DB
        log.info("Checking username in DB");
        Optional<UserEntity> userOptional = userRepository.findByUsername(userForm.getUsername());
        if(userOptional.isEmpty()){
            // we don't tell user that username is not found: maybe the user did a typo in his username
            throw UserManagerException.builder().message("Bad credentials").build();
        }

        // check user is not disbaled
        log.info("Checking username is not disabled");
        if(userOptional.get().isDisabled()){
            // we don't tell user that username is not found: maybe the user did a typo in his username
            throw UserManagerException.builder().message("User is disabled").build();
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
        return UserLoginResult.builder()
                .token(encryptedAccessToken)
                .passwordShouldBeChanged(userOptional.get().isPasswordShouldBeChanged())
                .avatar(userOptional.get().getAvatar())
                .build();
    }

    public UserLoginResult getUser(long userId){
        log.info("getting user from DB");
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        if(userOptional.isEmpty()){
            throw UserManagerException.builder().message("User from session no longer exists").build();
        }
        return UserLoginResult.builder()
                .username(userOptional.get().getUsername())
                .passwordShouldBeChanged(userOptional.get().isPasswordShouldBeChanged())
                .avatar(userOptional.get().getAvatar())
                .build();
    }

    public List<UserStatus> getUsers(long idUserPerformer){
        log.debug("UserID [{}]", idUserPerformer);
        // check user can still get users
        validateUserPerformer(idUserPerformer);
        // list of users
        Iterable<UserStatus> usersIterable = userRepository.findByIdIsNot(idUserPerformer);
        // remove the request owner and return result
        return StreamSupport.stream(usersIterable.spliterator(), false).toList();
    }

    private void validateForRegistrationAndLogin(UserForm userForm){
        if(userForm == null){
            throw UserManagerException.builder().message("Registration details are empty").build();
        }else if(!userForm.isUserNameValid()){
            throw UserManagerException.builder().message("Username is not valid").build();
        }else if(!userForm.isPasswordValid()){
            throw UserManagerException.builder().message("Password is not valid").build();
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
