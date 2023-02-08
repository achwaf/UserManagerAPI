package lu.cascade.assessment.user.manager.api.service.actions;

import lombok.Setter;
import lu.cascade.assessment.user.manager.api.dto.UserAction;
import lu.cascade.assessment.user.manager.api.dto.UserForm;
import lu.cascade.assessment.user.manager.api.model.UserEntity;
import lu.cascade.assessment.user.manager.api.repository.UserRepository;
import lu.cascade.assessment.user.manager.api.utils.UserManagerException;

@Setter
public abstract class ActionHandler {

    private UserRepository userRepository;

    abstract public void validate(UserAction userAction);

    abstract public void perform(UserAction userAction, long idUserPerformer);

    protected void validateUsername(UserForm details){
        if(!details.isUserNameValid()){
            throw UserManagerException.builder().message("Username in action details is not valid").build();
        }
    }

    protected void validatePassword(UserForm details){
        if(!details.isPasswordValid()){
            throw UserManagerException.builder().message("password in action details is not valid").build();
        }
    }

    public boolean isUserDisabled(long id){
        return  userRepository.findById(id).map(UserEntity::isDisabled)
                .orElseThrow(() -> UserManagerException.builder().message("User not found").build());
    }
}
