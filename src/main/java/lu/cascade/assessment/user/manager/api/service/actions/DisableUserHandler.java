package lu.cascade.assessment.user.manager.api.service.actions;

import lu.cascade.assessment.user.manager.api.dto.UserAction;
import lu.cascade.assessment.user.manager.api.dto.UserForm;
import lu.cascade.assessment.user.manager.api.model.UserEntity;
import lu.cascade.assessment.user.manager.api.utils.UserManagerException;

public class DisableUserHandler extends ActionHandler {
    @Override
    public void validate(UserAction userAction) {
        UserForm actionDetails = userAction.getActionDetails();
        // check username
        validateUsername(actionDetails);
    }


    @Override
    public void perform(UserAction userAction, long idUserPerformer) {
        // check username exists
        UserEntity userToDisable = userRepository.findByUsername(userAction.getActionDetails().getUsername())
                .orElseThrow(() -> UserManagerException.builder().message("Cannot disable a non existing user").build());

        // disable the user
        userToDisable.setDisabled(true);
        userRepository.save(userToDisable);
    }
}
