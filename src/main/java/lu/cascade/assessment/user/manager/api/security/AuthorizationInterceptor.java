package lu.cascade.assessment.user.manager.api.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lu.cascade.assessment.user.manager.api.service.UserManagerService;
import lu.cascade.assessment.user.manager.api.utils.UserManagerException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@AllArgsConstructor
@Slf4j
public class AuthorizationInterceptor implements HandlerInterceptor {

    private UserManagerService userManagerService;
    private AESHelper aesHelper;
    private static final String HEADER_TOKEN = "access-token";
    private static final String HEADER_SESSION = "session-id";

    private static final String AC_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    private static final String AC_ALLOW_METHODS = "Access-Control-Allow-Methods";
    private static final String AC_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        // allows CORS for front
        if( "OPTIONS".equals(request.getMethod())) {
            response.setHeader(AC_ALLOW_ORIGIN, "http://localhost:4200");
            response.setHeader(AC_ALLOW_METHODS, "*");
            response.setHeader(AC_ALLOW_HEADERS, "*");
            return false;
        }

        // check existence of security headers
        String encryptedAccessToken = request.getHeader(HEADER_TOKEN);
        String sessionId = request.getHeader(HEADER_SESSION);

        if(!(StringUtils.hasText(encryptedAccessToken) && StringUtils.hasText(sessionId))){
            throw UserManagerException.builder().message("Authorization compromised, request denied").build();
        }

        // validate authorization
        // More secure checks could be implemented (time validation, random seed validation)
        // or just use the standard JWT or Oauth
        // but here the accessToken is just valid forever
        try{
            String accessToken = aesHelper.decrypt(encryptedAccessToken);
            String decryptedSessionId = accessToken.split(":")[0];
            // check the sessionId is matching with the accessToken
            if(!decryptedSessionId.equals(sessionId)){
                log.error("non matching sessionId : sessionID in the header [{}] vs sessionId in the accessToken [{}]", sessionId,decryptedSessionId);
                throw UserManagerException.builder().message("Authorization not valid").build();
            }
            // pass the userId in the request
            String decryptedUserId = accessToken.split(":")[1];
            request.setAttribute("userId", decryptedUserId);
        }catch(Exception e){
            throw UserManagerException.builder().message("Authorization compromised, request denied").build();
        }
        return true;
    }


}
