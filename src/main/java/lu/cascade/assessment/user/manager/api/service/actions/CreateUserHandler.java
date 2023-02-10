package lu.cascade.assessment.user.manager.api.service.actions;

import lu.cascade.assessment.user.manager.api.dto.UserAction;
import lu.cascade.assessment.user.manager.api.dto.UserForm;
import lu.cascade.assessment.user.manager.api.model.UserEntity;
import lu.cascade.assessment.user.manager.api.utils.UserManagerException;
import lu.cascade.assessment.user.manager.api.utils.Utils;

public class CreateUserHandler extends ActionHandler {
    @Override
    public void validate(UserAction userAction) {
        UserForm actionDetails = userAction.getActionDetails();
        // check username
        validateUsername(actionDetails);
        // check password
        validatePassword(actionDetails);

    }


    @Override
    public void perform(UserAction userAction, long idUserPerformer) {
        // check username is not used (even though the check is also done in the front)
        if(isUserNameAlreadyUsed(userAction.getActionDetails().getUsername())){
            throw UserManagerException.builder().message("Username already used").build();
        }

        // create the user
        UserEntity newUser = UserEntity.builder()
                .username(userAction.getActionDetails().getUsername())
                .passwordHash(Utils.hash(userAction.getActionDetails().getPassword()))
                .passwordShouldBeChanged(userAction.getActionDetails().getPasswordShouldBeChanged())
                .build();
        userRepository.save(newUser);
    }
}
