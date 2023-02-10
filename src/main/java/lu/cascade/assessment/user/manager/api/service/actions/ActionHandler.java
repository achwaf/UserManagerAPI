package lu.cascade.assessment.user.manager.api.service.actions;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lu.cascade.assessment.user.manager.api.dto.UserAction;
import lu.cascade.assessment.user.manager.api.dto.UserForm;
import lu.cascade.assessment.user.manager.api.model.UserEntity;
import lu.cascade.assessment.user.manager.api.repository.UserRepository;
import lu.cascade.assessment.user.manager.api.service.UserValidationService;
import lu.cascade.assessment.user.manager.api.utils.UserManagerException;

@Setter
@Slf4j
public abstract class ActionHandler extends UserValidationService {

    public void process(UserAction userAction, long idUserPerformer){
        try{
            validate(userAction);
            validateUserPerformer(idUserPerformer);
            perform(userAction, idUserPerformer);
        }catch (UserManagerException ex){
            throw ex;
        }catch (Exception e){
            String message = "Exception raised during the process of action [{"+ userAction.getAction() + "}]";
            log.error(message);
            throw UserManagerException.builder().message(message).build();
        }
    }

    abstract void validate(UserAction userAction);

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

}
