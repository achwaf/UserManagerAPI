package lu.cascade.assessment.user.manager.api.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lu.cascade.assessment.user.manager.api.dto.UserAction;
import lu.cascade.assessment.user.manager.api.dto.UserForm;
import lu.cascade.assessment.user.manager.api.repository.UserRepository;
import lu.cascade.assessment.user.manager.api.service.actions.ActionHandler;
import lu.cascade.assessment.user.manager.api.utils.UserManagerException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class UserActionService
{

    private UserRepository userRepository;

    public void process(UserAction userAction, long idUserPerformer){
        log.info("Processing User action");
        if(userAction == null || userAction.getActionDetails() == null || userAction.getAction() == null){
            throw UserManagerException.builder().message("Action details are not valid").build();
        }
        log.info("Current action  [{}]", userAction.getAction());
        // Setup Handler with userRepository
        ActionHandler actionHandler = userAction.getAction().setUpHandler(userRepository);

        // validate and perform action
        actionHandler.process(userAction, idUserPerformer);

    }

}
