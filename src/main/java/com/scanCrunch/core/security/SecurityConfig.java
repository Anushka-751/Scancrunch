package com.scanCrunch.core.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsServiceImpl userDetailsService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    // Provided by BCryptConfig
    private final BCryptPasswordEncoder passwordEncoder;

    // Provided by CorsConfig
    private final CorsConfigurationSource corsConfigurationSource;

    @PostConstruct
    public void init() {
        System.out.println("========== SECURITY CONFIG LOADED ==========");
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return userDetailsService;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors
                        -> cors.configurationSource(corsConfigurationSource)
                )
                .csrf(csrf
                        -> csrf.disable()
                )
                .headers(headers
                        -> headers.frameOptions(frame -> frame.disable())
                )
                .sessionManagement(session
                        -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .exceptionHandling(exceptions
                        -> exceptions.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .authorizeHttpRequests(auth -> auth
                // ===========================
                // Public Authentication APIs
                // ===========================
                .requestMatchers(
                        "/api/v1/auth/login",
                        "/api/v1/auth/logout",
                        "/api/v1/auth/check-email",
                        "/api/v1/auth/validate-token",
                        "/api/v1/auth/register/send-otp",
                        "/api/v1/auth/register/verify-otp",
                        "/api/v1/auth/register/resend-otp",
                        "/api/v1/auth/forgot-password",
                        "/api/v1/auth/forgot-password/verify-otp",
                        "/api/v1/auth/forgot-password/resend-otp",
                        "/api/v1/auth/reset-password"
                ).permitAll()
                // OAuth2
                .requestMatchers(
                        "/oauth2/**",
                        "/login/**"
                ).permitAll()
                // Swagger
                .requestMatchers(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**"
                ).permitAll()
                // H2 Console
                .requestMatchers("/h2-console/**")
                .permitAll()
                // Actuator Metrics
                .requestMatchers("/actuator/**")
                .permitAll()
                .anyRequest()
                .authenticated()
                )
                .oauth2Login(oauth
                        -> oauth.successHandler(oAuth2SuccessHandler)
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

}
