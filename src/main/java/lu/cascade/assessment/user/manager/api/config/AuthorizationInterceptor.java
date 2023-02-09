package lu.cascade.assessment.user.manager.api.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lu.cascade.assessment.user.manager.api.service.UserManagerService;
import lu.cascade.assessment.user.manager.api.utils.UserManagerException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@AllArgsConstructor
public class AuthorizationInterceptor implements HandlerInterceptor {

    private UserManagerService userManagerService;

    private static final String HEADER_TOKEN = "accesstoken";
    private static final String HEADER_SESSION = "sessionid";

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        // check existence of security headers
        String accesstoken = request.getHeader(HEADER_TOKEN);
        String sessionId = request.getHeader(HEADER_SESSION);

        if(!(StringUtils.hasText(accesstoken) && StringUtils.hasText(sessionId))){
            throw UserManagerException.builder().message("No authorization found, request denied").build();
        }



        return true;
    }


}
