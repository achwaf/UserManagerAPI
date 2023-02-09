package lu.cascade.assessment.user.manager.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ApiError {
    private String message;

    private String details;

    private String tracker;

}
