package lu.cascade.assessment.user.manager.api.dto;

/**
 * Projection for UserEntity
 */
public interface UserStatus {

    String getUsername();
    boolean isDisabled();
    int getAvatar();


}
