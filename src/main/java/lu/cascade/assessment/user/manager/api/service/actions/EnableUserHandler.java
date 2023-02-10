package lu.cascade.assessment.user.manager.api.service.actions;

import lu.cascade.assessment.user.manager.api.dto.UserAction;
import lu.cascade.assessment.user.manager.api.dto.UserForm;
import lu.cascade.assessment.user.manager.api.model.UserEntity;
import lu.cascade.assessment.user.manager.api.utils.UserManagerException;

public class EnableUserHandler extends ActionHandler {
    @Override
    public void validate(UserAction userAction) {
        UserForm actionDetails = userAction.getActionDetails();
        // check username
        validateUsername(actionDetails);
    }


    @Override
    public void perform(UserAction userAction, long idUserPerformer) {
        // check username exists
        UserEntity userToEnable = userRepository.findByUsername(userAction.getActionDetails().getUsername())
                .orElseThrow(() -> UserManagerException.builder().message("Cannot enable a non existing user").build());

        // disable the user
        userToEnable.setDisabled(false);
        userRepository.save(userToEnable);
    }
}
