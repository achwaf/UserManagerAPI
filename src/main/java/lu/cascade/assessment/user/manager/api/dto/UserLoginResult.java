package lu.cascade.assessment.user.manager.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UserLoginResult {

    private String token;
    private boolean passwordShouldBeChanged;


}
