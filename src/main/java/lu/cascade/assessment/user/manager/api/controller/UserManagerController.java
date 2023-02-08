package lu.cascade.assessment.user.manager.api.controller;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lu.cascade.assessment.user.manager.api.dto.UserAction;
import lu.cascade.assessment.user.manager.api.dto.UserForm;
import lu.cascade.assessment.user.manager.api.service.UserActionService;
import lu.cascade.assessment.user.manager.api.service.UserManagerService;
import lu.cascade.assessment.user.manager.api.utils.Utils;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.http.HttpRequest;

@RestController
@RequestMapping(path = "/cascade/assessment", produces = "application/json")
@AllArgsConstructor
@Slf4j
public class UserManagerController {

    private UserManagerService userManagerService;
    private UserActionService userActionService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserForm userForm) {
        log.info("Registering a new user");
        userManagerService.register(userForm);
        log.info("Registration done");
        return ResponseEntity.ok("done");
    }

    @GetMapping("/form/check")
    public Boolean checkUserNameExistence(@RequestParam(name = "username") String userNameBase64){
        log.info("Checking existence of userName [{}]", userNameBase64);
        Boolean result = userManagerService.isUserNameAlreadyUsed(userNameBase64);
        log.info("Check result [{}]", result);
        return result;
    }

    /**
     * don't forget to add the front end random seed
     * @param userForm
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserForm userForm){
        log.info("Performing login");
        userManagerService.login(userForm);
        log.info("login success");
        return ResponseEntity.ok("done");
    }

    @PostMapping("/manage")
    public ResponseEntity<String> manage(@RequestBody @Valid UserAction userAction,
                                         @RequestHeader(name="accesstoken") String accessToken,
                                         @RequestHeader(name="sessionuuid") String appRandomSeed) {
        log.info("Managing user by performing action [{}]", userAction.getAction());
        // check and validate accesstoken
        long idUserPerformer = Utils.checkSecurityAccess(accessToken, appRandomSeed);
        // handle the action
        userActionService.process(userAction, idUserPerformer);
        log.info("Action performed");
        return ResponseEntity.ok("done");
    }

}
