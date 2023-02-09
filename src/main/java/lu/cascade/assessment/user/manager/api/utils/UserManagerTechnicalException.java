package lu.cascade.assessment.user.manager.api.utils;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserManagerTechnicalException extends UserManagerException{

    private String code;

    @Builder(builderMethodName = "technicalbuilder")
    public UserManagerTechnicalException(String message, String code) {
        super(code, message);
    }

}
