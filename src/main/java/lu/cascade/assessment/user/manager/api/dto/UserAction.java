package lu.cascade.assessment.user.manager.api.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lu.cascade.assessment.user.manager.api.service.actions.UserActionEnum;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Data
public class UserAction {
    @NotNull
    private UserActionEnum action;
    @NotNull
    private UserForm actionDetails;

}
