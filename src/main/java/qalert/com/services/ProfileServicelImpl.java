package qalert.com.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import qalert.com.interfaces.profile.IProfileDao;
import qalert.com.interfaces.profile.IProfileService;
import qalert.com.models.profile.ProfileRequest;
import qalert.com.models.profile.ProfileResponse;

@Service
public class ProfileServicelImpl implements IProfileService {

    @Autowired
    private IProfileDao profileDao;

    @Override
    public Long insert(ProfileRequest request) {
       return profileDao.insert(request);
    }

    @Override
    public List<ProfileResponse> listProfiles(Long userId) {
        return profileDao.listProfiles(userId);
    }

    @Override
    public void updateProfile(ProfileRequest request) {
        profileDao.updateProfile(request);
    }

    @Override
    public void deleteProfile(Long profileId) {
        profileDao.deleteProfile(profileId);
    }

}
