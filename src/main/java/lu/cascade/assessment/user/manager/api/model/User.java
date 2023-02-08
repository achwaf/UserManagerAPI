package lu.cascade.assessment.user.manager.api.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class User {

    private String userName;

    private String name;

    private String passwordHash;

    private boolean passwordShouldBeChanged;

    private boolean disabled;

}
