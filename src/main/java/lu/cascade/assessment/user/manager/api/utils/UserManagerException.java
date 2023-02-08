package lu.cascade.assessment.user.manager.api.utils;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UserManagerException extends RuntimeException{

    private String message;

    private String code;


}
