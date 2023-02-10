package lu.cascade.assessment.user.manager.api.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lu.cascade.assessment.user.manager.api.service.UserManagerService;
import lu.cascade.assessment.user.manager.api.utils.UserManagerException;
import lu.cascade.assessment.user.manager.api.utils.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@AllArgsConstructor
public class AuthorizationInterceptor implements HandlerInterceptor {

    private UserManagerService userManagerService;
    private AESHelper aesHelper;
    private static final String HEADER_TOKEN = "access-token";
    private static final String HEADER_SESSION = "session-id";



    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        // check existence of security headers
        String encryptedAccessToken = request.getHeader(HEADER_TOKEN);
        String sessionId = request.getHeader(HEADER_SESSION);

        if(!(StringUtils.hasText(encryptedAccessToken) && StringUtils.hasText(sessionId))){
            throw UserManagerException.builder().message("No authorization found, request denied").build();
        }

        // validate authorization
        try{
            String accessToken = aesHelper.decrypt(encryptedAccessToken);
            String userId = accessToken.split(":")[1];
            request.setAttribute("userId", userId);
        }catch(Exception e){
            throw UserManagerException.builder().message("Authorization compromised, request denied").build();
        }
        return true;
    }


}
