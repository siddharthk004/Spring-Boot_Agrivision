package com.agri.vision.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.agri.vision.Model.myAppUserService;

@Configuration
@EnableWebSecurity
public class securityConfig {

    // Inject the custom UserDetailsService
    private final myAppUserService appUserService;

    public securityConfig(myAppUserService appUserService) {
        this.appUserService = appUserService;
    }

    // Define a PasswordEncoder bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Define the custom UserDetailsService bean
    @Bean
    public UserDetailsService userDetailsService() {
        return appUserService;
    }

    // Use the default AuthenticationManager from Spring Security
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Define an AuthenticationProvider to authenticate users using the UserDetailsService
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(appUserService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // Define the SecurityFilterChain bean for configuring HTTP security
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disabling CSRF protection (not recommended for production)
                .authorizeRequests(auth -> auth
                        .requestMatchers("/user/profile").authenticated() // Require authentication for /user/profile
                        .anyRequest().permitAll() // Allow all other requests
                )
                .formLogin(login -> login
                        .loginPage("/user/login") // Custom login page
                        .permitAll())
                .logout(logout -> logout
                        .permitAll());
        return http.build();
    }

    
}
