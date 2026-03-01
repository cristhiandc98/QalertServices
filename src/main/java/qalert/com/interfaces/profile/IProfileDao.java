package qalert.com.interfaces.profile;

import java.util.List;

import qalert.com.models.profile.ProfileRequest;
import qalert.com.models.profile.ProfileResponse;

public interface IProfileDao {

    Long insert (ProfileRequest request);

    void updateProfile (ProfileRequest request);

    List<ProfileResponse> listProfiles(Long userId);

    void deleteProfile (Long profileId);

}
