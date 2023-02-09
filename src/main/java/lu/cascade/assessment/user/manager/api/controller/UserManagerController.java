package lu.cascade.assessment.user.manager.api.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lu.cascade.assessment.user.manager.api.dto.ApiError;
import lu.cascade.assessment.user.manager.api.dto.UserAction;
import lu.cascade.assessment.user.manager.api.dto.UserForm;
import lu.cascade.assessment.user.manager.api.dto.UserStatus;
import lu.cascade.assessment.user.manager.api.service.UserActionService;
import lu.cascade.assessment.user.manager.api.service.UserManagerService;
import lu.cascade.assessment.user.manager.api.utils.UserManagerException;
import lu.cascade.assessment.user.manager.api.utils.Utils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping(path = "/assessment", produces = "application/json")
@AllArgsConstructor
@Slf4j
public class UserManagerController {

    private UserManagerService userManagerService;
    private UserActionService userActionService;
    private AtomicLong errorTracker;

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

    /**
     * don't forget to add the front end random seed
     * @return
     */
    @GetMapping("/auth/users")
    public List<UserStatus> getUsers(){
        log.info("Performing login");
        List<UserStatus> userStatusList = userManagerService.getUsers();
        log.info("login success");
        return userStatusList;
    }

    /**
     * keeping authorization simple by using header to pass token
     * @param userAction
     * @param accessToken
     * @param sessionID
     * @return
     */
    @PostMapping("/auth/manage")
    public ResponseEntity<String> manage(@RequestBody @Valid UserAction userAction, HttpServletRequest request) {
        log.info("Managing user by performing action [{}]", userAction.getAction());
        // check and validate accesstoken
        long idUserPerformer = Utils.checkSecurityAccess(request.getAttribute(), sessionID);
        // handle the action
        userActionService.process(userAction, idUserPerformer);
        log.info("Action performed");
        return ResponseEntity.ok("done");
    }

    /**
     * Exception handler here since we only have one contoller
     */
    @ExceptionHandler({ Throwable.class})
    public ResponseEntity<Object> handleException(Throwable ex) {
        log.error("Error : ",ex);
        if(ex instanceof UserManagerException){
            UserManagerException appException = (UserManagerException) ex;
            return ResponseEntity
                    .badRequest().body(ApiError.builder()
                            .message("Exception raised during the processing of the request")
                            .details(appException.getMessage())
                            .tracker("ERR" + errorTracker.addAndGet(1))
                            .build());
        }else{
            return ResponseEntity
                    .internalServerError().body(ApiError.builder()
                            .message("Error occurred during the processing of the request")
                            .details(ex.getMessage())
                            .tracker("ERR" + errorTracker.addAndGet(1))
                            .build());
        }
        HttpServletRequest a;a.se

    }

}
