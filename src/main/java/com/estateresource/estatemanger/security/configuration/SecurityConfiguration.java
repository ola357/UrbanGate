package com.estateresource.estatemanger.security.configuration;

import com.estateresource.estatemanger.security.jwt.JwtFilter;
import com.estateresource.estatemanger.security.repository.RoleRepository;
import com.estateresource.estatemanger.shared.entity.User;
import com.estateresource.estatemanger.shared.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private final PasswordEncoder passwordEncoder;
    private final JwtFilter jwtRequestFilter;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration auth) throws Exception {
        return auth.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Stateless API → no CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // No HTTP session
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Authorization rules
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/.well-known/jwks.json",
                                "/.well-known/openid-configuration",
                                "/api/auth/signup",
                                "/api/auth/authenticate",
                                "/api/auth/verify"
                        ).permitAll()
                        .requestMatchers("/auth/**").permitAll()
//                        .requestMatchers("/estate/**").hasRole("ADMIN")
//                        .requestMatchers("/resident/**").hasRole("RESIDENT")
                        .anyRequest().authenticated()
                )

                // JWT filter
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }

//    @PostConstruct
    public void init() {
        User newUser = new User();
        newUser.setId("GVAEST-7000");
        newUser.setPhoneNumber("09055589964");
        newUser.setPassword(passwordEncoder.encode("password"));
        newUser.setEstateId(1L);

        userRepository.insert(newUser);
    }

}
