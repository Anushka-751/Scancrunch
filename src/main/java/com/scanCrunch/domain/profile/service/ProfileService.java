package com.scanCrunch.domain.profile.service;

import com.scanCrunch.domain.profile.dto.ChangePasswordRequest;
import com.scanCrunch.domain.profile.dto.EmailOtpRequest;
import com.scanCrunch.domain.profile.dto.MobileOtpRequest;
import com.scanCrunch.domain.profile.dto.ProfileResponse;
import com.scanCrunch.domain.profile.dto.SendPasswordOtpRequest;
import com.scanCrunch.domain.profile.dto.UpdateProfileRequest;
import com.scanCrunch.domain.profile.dto.VerifyEmailOtpRequest;
import com.scanCrunch.domain.profile.dto.VerifyMobileOtpRequest;
import com.scanCrunch.domain.profile.dto.VerifyPasswordOtpRequest;

public interface ProfileService {

    ProfileResponse getProfile();

    void updateProfile(UpdateProfileRequest request);

    void sendEmailOtp(EmailOtpRequest request);

    void verifyEmailOtp(VerifyEmailOtpRequest request);

    void sendMobileOtp(MobileOtpRequest request);

    void verifyMobileOtp(VerifyMobileOtpRequest request);

    void sendChangePasswordOtp(SendPasswordOtpRequest request);

    void verifyChangePasswordOtp(VerifyPasswordOtpRequest request);

    void changePassword(ChangePasswordRequest request);
}
