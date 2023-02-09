package lu.cascade.assessment.user.manager.api.service.actions;

import lombok.extern.slf4j.Slf4j;
import lu.cascade.assessment.user.manager.api.repository.UserRepository;
import lu.cascade.assessment.user.manager.api.service.actions.*;
import lu.cascade.assessment.user.manager.api.utils.UserManagerException;
import lu.cascade.assessment.user.manager.api.utils.UserManagerTechnicalException;

@Slf4j
public enum UserActionEnum {

    DISABLE_USER(new DisableUserHandler()),
    DELETE_USER(new DeleteUserHandler()),
    CREATE_USER(new CreateUserHandler()),
    CHANGE_USER_PASS(new ChangeUserPassHandler()),
    CHANGE_OWN_PASS(new ChangeOwnPassHandler()),
    ENABLE_USER(new EnableUserHandler());

    private ActionHandler actionHandler;

    private UserActionEnum(ActionHandler actionHandler){
        this.actionHandler = actionHandler;
    }

    public ActionHandler setUpHandler(UserRepository userRepository){
        if(userRepository == null){
            log.error("Setting up ActionHandler with null value");
            throw UserManagerTechnicalException.technicalbuilder().message("UserRepository is null").build();
        }
        this.actionHandler.setUserRepository(userRepository);
        return this.actionHandler;
    }
}
