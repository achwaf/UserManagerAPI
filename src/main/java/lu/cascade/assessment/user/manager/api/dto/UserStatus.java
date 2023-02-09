package lu.cascade.assessment.user.manager.api.dto;

import lombok.Getter;
import lombok.Setter;

public interface UserStatus {

    String getUsername();

    boolean isDisabled();

}
