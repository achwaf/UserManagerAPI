package lu.cascade.assessment.user.manager.api.service.actions;

import lu.cascade.assessment.user.manager.api.dto.UserAction;
import lu.cascade.assessment.user.manager.api.dto.UserForm;
import lu.cascade.assessment.user.manager.api.model.UserEntity;
import lu.cascade.assessment.user.manager.api.utils.UserManagerException;
import lu.cascade.assessment.user.manager.api.utils.Utils;

public class ChangeUserPassHandler extends ActionHandler {
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
        UserEntity user = userRepository.findByUsername(userAction.getActionDetails().getUsername())
                .orElseThrow(() -> UserManagerException.builder().message("Cannot edit a non existing user").build());
        // change the password
        user.setPasswordHash(Utils.hash(userAction.getActionDetails().getPassword()));
        // change the avatar too
        user.setAvatar(userAction.getActionDetails().getAvatar());
        // and if the user should change pass on first login
        user.setPasswordShouldBeChanged(userAction.getActionDetails().isPasswordShouldBeChanged());
        // save the user
        userRepository.save(user);
    }
}
