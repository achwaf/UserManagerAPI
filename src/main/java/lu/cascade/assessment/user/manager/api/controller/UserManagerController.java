package lu.cascade.assessment.user.manager.api.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import lu.cascade.assessment.user.manager.api.dto.*;
import lu.cascade.assessment.user.manager.api.service.UserActionService;
import lu.cascade.assessment.user.manager.api.service.UserManagerService;
import lu.cascade.assessment.user.manager.api.utils.UserManagerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping(path = "/assessment", produces = "application/json")
@Slf4j
public class UserManagerController {

    private final UserManagerService userManagerService;
    private final UserActionService userActionService;
    private final AtomicLong errorTracker = new AtomicLong();

    public UserManagerController(UserManagerService userManagerService, UserActionService userActionService) {
        this.userManagerService = userManagerService;
        this.userActionService = userActionService;
    }

    @PostMapping("/register")
    public ResponseEntity<Boolean> register(@RequestBody UserForm userForm) {
        log.info("Registering a new user");
        userManagerService.register(userForm);
        log.info("Registration done");
        return ResponseEntity.ok(true);
    }

    @GetMapping("/form/check")
    public Boolean checkUserNameExistence(@RequestParam(name = "username") String userNameBase64){
        log.info("Checking existence of userName [{}]", userNameBase64);
        Boolean result = userManagerService.isUserNameBase64AlreadyUsed(userNameBase64);
        log.info("Check result [{}]", result);
        return result;
    }

    /**
     * login the user
     * @param userForm the credentials
     * @param  appId a random ID generated by the front to allow a session per tab within a browser (practical for evaluator to test different use cases)
     * @return
     */
    @PostMapping("/login")
    public UserLoginResult login(@RequestParam String appId, @RequestBody UserForm userForm){
        log.info("Performing login");
        UserLoginResult userResult = userManagerService.login(appId, userForm);
        log.info("login success");
        return userResult;
    }

    /**
     *
     * @return
     */
    @GetMapping("/auth/users")
    public List<UserStatus> getUsers(HttpServletRequest request){
        log.info("Getting list of Users");
        // get UserId
        long idUserPerformer = Long.parseLong(request.getAttribute("userId").toString());
        // get users
        List<UserStatus> userStatusList = userManagerService.getUsers(idUserPerformer);
        log.info("Returning list with [{}] users", userStatusList.size());
        return userStatusList;
    }

    /**
     *
     * @return
     */
    @GetMapping("/auth/user")
    public UserLoginResult getUser(HttpServletRequest request){
        log.info("Getting the user from session (case of Refresh page)");
        // get UserId
        long idUserPerformer = Long.parseLong(request.getAttribute("userId").toString());
        // get user from session
        UserLoginResult userResult  = userManagerService.getUser(idUserPerformer);
        log.info("Returning user from session");
        return userResult;
    }

    /**
     * keeping authorization simple by using header to pass token
     * @param userAction
     * @return
     */
    @PostMapping("/auth/manage")
    public ResponseEntity<Boolean> manage(@RequestBody @Valid UserAction userAction, HttpServletRequest request) {
        log.info("Managing user by performing action [{}]", userAction.getAction());
        // get UserId
        long idUserPerformer =  Long.parseLong(request.getAttribute("userId").toString());
        // handle the action
        userActionService.process(userAction, idUserPerformer);
        log.info("Action performed");
        return ResponseEntity.ok(true);
    }

    /**
     * Exception handler here since we only have one contoller
     */
    @ExceptionHandler({ Throwable.class})
    public ResponseEntity<Object> handleException(Throwable ex) {
        String tracker = "ERR" + errorTracker.addAndGet(1);
        log.error("ERROR [{}] ",tracker,ex);
        if(ex instanceof UserManagerException){
            UserManagerException appException = (UserManagerException) ex;
            return ResponseEntity
                    .badRequest().body(ApiError.builder()
                            .message("Exception raised during the processing of the request")
                            .details(appException.getMessage())
                            .tracker(tracker)
                            .build());
        }else{
            return ResponseEntity
                    .internalServerError().body(ApiError.builder()
                            .message("Error occurred during the processing of the request")
                            .details("See logs for more details")
                            .tracker(tracker)
                            .build());
        }

    }

}
