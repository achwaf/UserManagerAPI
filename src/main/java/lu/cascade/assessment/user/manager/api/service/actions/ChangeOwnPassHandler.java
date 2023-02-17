package lu.cascade.assessment.user.manager.api.service.actions;

import lu.cascade.assessment.user.manager.api.dto.UserAction;
import lu.cascade.assessment.user.manager.api.dto.UserForm;
import lu.cascade.assessment.user.manager.api.model.UserEntity;
import lu.cascade.assessment.user.manager.api.utils.UserManagerException;
import lu.cascade.assessment.user.manager.api.utils.Utils;

public class ChangeOwnPassHandler extends ActionHandler {
    @Override
    public void validate(UserAction userAction) {
        UserForm actionDetails = userAction.getActionDetails();
        // check password
        validatePassword(actionDetails);
    }

    @Override
    public void perform(UserAction userAction, long idUserPerformer) {
        UserEntity user = userRepository.findById(idUserPerformer)
                .orElseThrow(() -> UserManagerException.builder().message("Your user no longer exists").build());
        // change the password
        user.setPasswordHash(Utils.hash(userAction.getActionDetails().getPassword()));
        // change the avatar too
        user.setAvatar(userAction.getActionDetails().getAvatar());
        // save the user
        userRepository.save(user);
    }

}
