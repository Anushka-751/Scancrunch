package com.scanCrunch.core.security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.scanCrunch.domain.user.entity.User;
import com.scanCrunch.domain.user.enums.Role;
import com.scanCrunch.domain.user.repository.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {


    private final UserRepository userRepository;
    private final JwtService jwtService;


    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();



    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {


        OAuth2User oauthUser =
                (OAuth2User) authentication.getPrincipal();



        String email =
                oauthUser.getAttribute("email");


        Optional<User> existingUser =
                userRepository.findByEmail(email);



        User user;



        if(existingUser.isPresent()){


            user = existingUser.get();


        }else{


            user = new User();


            String firstName =
                    oauthUser.getAttribute("given_name");


            String lastName =
                    oauthUser.getAttribute("family_name");


            String fullName =
                    oauthUser.getAttribute("name");



            if(firstName == null){
                firstName = "Google";
            }


            if(lastName == null){
                lastName = "";
            }


            if(fullName == null){
                fullName = firstName + " " + lastName;
            }



            user.setEmail(email);

            user.setFirstName(firstName);

            user.setLastName(lastName);

            user.setFullName(fullName);



            // Google does not provide phone
            // keep null
            user.setPhone(null);



            // dummy encrypted password
            user.setPassword(
                    passwordEncoder.encode(
                            "GOOGLE_AUTH_USER"
                    )
            );



            user.setProvider("GOOGLE");

            user.setRole(Role.CUSTOMER);

            user.setVerified(true);

            user.setActive(true);



            user =
              userRepository.save(user);

        }



       String token =
        jwtService.generateToken(user.getEmail());



        response.sendRedirect(
                "http://localhost:5173/oauth-success?token="
                + token
        );


    }

}