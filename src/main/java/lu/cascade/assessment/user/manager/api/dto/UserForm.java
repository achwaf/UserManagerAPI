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

    private String username;

    private String password;

    private Boolean passwordShouldBeChanged;

    public boolean isUserNameValid(){
        return StringUtils.hasText(username) && username.length() < Utils.ALLOWED_LENGTH_USERNAME && Utils.isEmailValid(username);
    }

    public boolean isPasswordValid(){
        // valid if empty or length < allowed_length
        return !StringUtils.hasText(password) || password.length() < Utils.ALLOWED_LENGTH_PASSWORD;
    }


}
