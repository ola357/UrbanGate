package com.estateresource.estatemanger.security.configuration;

import com.estateresource.estatemanger.security.model.CustomUserDetails;
import com.estateresource.estatemanger.security.model.entity.Role;
import com.estateresource.estatemanger.security.repository.RoleRepository;
import com.estateresource.estatemanger.shared.entity.User;
import com.estateresource.estatemanger.shared.enums.ExceptionResponse;
import com.estateresource.estatemanger.shared.exceptions.UserNameNotFoundException;
import com.estateresource.estatemanger.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading user by username: {}", username);

        User user =  userRepository.findUserByPhoneNumber(username)
                .orElseThrow(()-> new UserNameNotFoundException(ExceptionResponse.USER_NAME_NOTFOUND));
        Set<Role> roles = roleRepository.findRolesByUserPhoneNumber(username);

        log.info(String.format("Found user %s", username));
        if(Objects.nonNull(user)){
            log.debug("User : {}", user);
            user.setRoles(roles);
            return new CustomUserDetails(user);
        }
        log.error("This Username was Not Found");
        return new CustomUserDetails(user);
    }



    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
