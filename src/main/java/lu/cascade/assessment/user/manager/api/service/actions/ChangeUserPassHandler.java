package lu.cascade.assessment.user.manager.api.service.actions;

import lu.cascade.assessment.user.manager.api.dto.UserAction;
import lu.cascade.assessment.user.manager.api.dto.UserForm;

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

    }
}
