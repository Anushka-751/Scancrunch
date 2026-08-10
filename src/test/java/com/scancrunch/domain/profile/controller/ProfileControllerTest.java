package com.scancrunch.domain.profile.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scanCrunch.core.security.CustomUserDetails;
import com.scanCrunch.core.security.SecurityUtils;
import com.scanCrunch.domain.forgotpassword.entity.PasswordResetOtp;
import com.scanCrunch.domain.forgotpassword.repository.PasswordResetOtpRepository;
import com.scanCrunch.domain.profile.dto.ChangePasswordRequest;
import com.scanCrunch.domain.profile.dto.EmailOtpRequest;
import com.scanCrunch.domain.profile.dto.MobileOtpRequest;
import com.scanCrunch.domain.profile.dto.SendPasswordOtpRequest;
import com.scanCrunch.domain.profile.dto.UpdateProfileRequest;
import com.scanCrunch.domain.profile.dto.VerifyEmailOtpRequest;
import com.scanCrunch.domain.profile.dto.VerifyMobileOtpRequest;
import com.scanCrunch.domain.profile.dto.VerifyPasswordOtpRequest;
import com.scanCrunch.domain.user.entity.User;
import com.scanCrunch.domain.user.enums.Role;
import com.scanCrunch.domain.user.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Disable security filters to simplify testing authenticated calls
@ActiveProfiles("h2")
@Transactional
public class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetOtpRepository passwordResetOtpRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private SecurityUtils securityUtils;

    @MockBean
    private com.scanCrunch.core.util.EmailSenderUtil emailSenderUtil;

    @MockBean
    private com.scanCrunch.domain.email.service.EmailService emailService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    public void setup() {
        // Clear db
        userRepository.deleteAll();
        passwordResetOtpRepository.deleteAll();

        // Create test user
        testUser = new User();
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPhone("1234567890");
        testUser.setPassword(passwordEncoder.encode("Password@123"));
        testUser.setRole(Role.CUSTOMER);
        testUser.setActive(true);
        testUser.setVerified(true);
        testUser.setVerifiedEmail(true);
        testUser.setVerifiedPhone(true);
        testUser = userRepository.save(testUser);

        // Mock current authenticated user
        Mockito.when(securityUtils.getCurrentUser()).thenReturn(testUser);
        Mockito.when(securityUtils.getCurrentUserId()).thenReturn(testUser.getId());

        CustomUserDetails userDetails = new CustomUserDetails(testUser);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void testGetProfile() throws Exception {
        mockMvc.perform(get("/api/v1/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    public void testUpdateProfile() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest("Jane", "Smith");

        mockMvc.perform(put("/api/v1/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Profile updated successfully"));

        User updated = userRepository.findById(testUser.getId()).orElseThrow();
        assert updated.getFirstName().equals("Jane");
        assert updated.getLastName().equals("Smith");
    }

    @Test
    public void testSendEmailOtp_Success() throws Exception {
        EmailOtpRequest request = new EmailOtpRequest("jane.doe@example.com");

        mockMvc.perform(post("/api/v1/profile/email/send-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OTP sent successfully"));
    }

    @Test
    public void testSendEmailOtp_Duplicate() throws Exception {
        EmailOtpRequest request = new EmailOtpRequest("john.doe@example.com");

        mockMvc.perform(post("/api/v1/profile/email/send-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()); // Handled as IllegalArgumentException -> BAD_REQUEST
    }

    @Test
    public void testSendMobileOtp_Success() throws Exception {
        MobileOtpRequest request = new MobileOtpRequest("9876543210");

        mockMvc.perform(post("/api/v1/profile/mobile/send-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OTP sent successfully"));
    }

    @Test
    public void testSendChangePasswordOtp_And_Verify_And_Change() throws Exception {
        // Send OTP
        SendPasswordOtpRequest sendRequest = new SendPasswordOtpRequest("EMAIL");
        mockMvc.perform(post("/api/v1/profile/change-password/send-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sendRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OTP sent successfully"));

        // Retrieve the generated OTP from repo to simulate user entry
        PasswordResetOtp savedOtp = passwordResetOtpRepository.findByIdentifier(testUser.getEmail()).orElseThrow();
        String rawOtp = savedOtp.getOtp(); // The raw OTP is actually generated and printed/sent, in test environment we set it

        // Verify OTP
        VerifyPasswordOtpRequest verifyRequest = new VerifyPasswordOtpRequest(rawOtp);
        mockMvc.perform(post("/api/v1/profile/change-password/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OTP verified successfully"));

        // Change Password
        ChangePasswordRequest changeRequest = new ChangePasswordRequest("NewSecure@123", "NewSecure@123");
        mockMvc.perform(put("/api/v1/profile/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password changed successfully"));
    }
}
