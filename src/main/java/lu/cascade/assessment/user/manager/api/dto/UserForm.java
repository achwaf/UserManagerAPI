package lu.cascade.assessment.user.manager.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lu.cascade.assessment.user.manager.api.utils.Utils;
import org.springframework.util.StringUtils;

@Builder
@Getter
@Setter
public class UserForm {

    private String userName;

    private String password;

    private Boolean passwordShouldBeChanged;

    public boolean isUserNameValid(){
        return StringUtils.hasText(userName) && userName.length() < Utils.ALLOWED_LENGTH_USERNAME && Utils.isEmailValid(userName);
    }

    public boolean isPasswordValid(){
        return !StringUtils.hasText(password) || password.length() < Utils.ALLOWED_LENGTH_PASSWORD;
    }


}
