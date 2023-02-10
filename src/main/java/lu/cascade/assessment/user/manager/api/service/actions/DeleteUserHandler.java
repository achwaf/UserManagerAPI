package lu.cascade.assessment.user.manager.api.service.actions;

import lu.cascade.assessment.user.manager.api.dto.UserAction;
import lu.cascade.assessment.user.manager.api.dto.UserForm;
import lu.cascade.assessment.user.manager.api.model.UserEntity;
import lu.cascade.assessment.user.manager.api.utils.UserManagerException;
import lu.cascade.assessment.user.manager.api.utils.Utils;

import javax.swing.text.html.parser.Entity;

public class DeleteUserHandler extends ActionHandler {
    @Override
    public void validate(UserAction userAction) {
        UserForm actionDetails = userAction.getActionDetails();
        // check username
        validateUsername(actionDetails);
    }


    @Override
    public void perform(UserAction userAction, long idUserPerformer) {
        // check username exists
        UserEntity userToDelete = userRepository.findByUsername(userAction.getActionDetails().getUsername())
                .orElseThrow(() -> UserManagerException.builder().message("Cannot delete a non existing user").build());

        // delete the user
        userRepository.delete(userToDelete);
    }
}
