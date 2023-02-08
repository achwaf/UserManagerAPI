package lu.cascade.assessment.user.manager.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@Entity
public class UserEntity {
    @Id
    @GeneratedValue
    private long id;

    private String userName;

    private String name;

    private String passwordHash;

    private boolean passwordShouldBeChanged;

    private boolean disabled;

}
