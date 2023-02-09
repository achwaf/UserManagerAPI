package lu.cascade.assessment.user.manager.api.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lu.cascade.assessment.user.manager.api.dto.UserForm;
import lu.cascade.assessment.user.manager.api.dto.UserStatus;
import lu.cascade.assessment.user.manager.api.model.UserEntity;
import lu.cascade.assessment.user.manager.api.repository.UserRepository;
import lu.cascade.assessment.user.manager.api.utils.UserManagerTechnicalException;
import lu.cascade.assessment.user.manager.api.utils.Utils;
import lu.cascade.assessment.user.manager.api.utils.UserManagerException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
@Slf4j
public class UserManagerService {

    private UserRepository userRepository;


    public boolean isUserNameAlreadyUsed(String userNameBase64){
        // decode the userNameBase64
        log.info("Decoding Username");
        String userName = Utils.fromBase64(userNameBase64);
        log.info("Username decoded to [{}]", userName);

        // check in the DB
        log.info("Checking existence in DB");
        return userRepository.findByUserName(userName).isPresent();
    }

    public void register(UserForm userForm){
        // validate inputs
        log.info("Validating User input");
        validateForRegistration(userForm);

        // check userName is not used
        log.info("Checking username is not used");
        if(userRepository.findByUserName(userForm.getUsername()).isPresent()){
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

    public void login(UserForm userForm){

    }

    public List<UserStatus> getUsers(){
        Iterable<UserStatus> usersIterable = userRepository.findAllById();
        return StreamSupport.stream(usersIterable.spliterator(), false).toList();
    }

    private void validateForRegistration(UserForm userForm){
        if(userForm == null){
            throw UserManagerException.builder().message("Registration details are empty").build();
        }else if(!userForm.isUserNameValid()){
            throw UserManagerException.builder().message("UserName is not valid").build();
        }else if(!userForm.isPasswordValid()){
            throw UserManagerException.builder().message("password is not valid").build();
        }
    }

}
