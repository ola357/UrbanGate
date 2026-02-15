package com.estateresource.estatemanger.security.service;

import com.estateresource.estatemanger.security.jwt.JwtUtils;
import com.estateresource.estatemanger.security.model.AuthRequest;
import com.estateresource.estatemanger.security.model.CustomUserDetails;
import com.estateresource.estatemanger.security.model.response.TokenResponse;
import com.estateresource.estatemanger.shared.entity.User;
import com.estateresource.estatemanger.shared.enums.ExceptionResponse;
import com.estateresource.estatemanger.shared.exceptions.UserNameNotFoundException;
import com.estateresource.estatemanger.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtil;
    private final UserRepository userRepository;


    public TokenResponse authenticateUser(AuthRequest request) throws Exception {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.phoneNumber(), request.password())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = userRepository.findUserByPhoneNumber(request.phoneNumber())
                    .orElseThrow(() -> new UserNameNotFoundException(ExceptionResponse.USER_NAME_NOTFOUND));
            log.info("Authenticated user: {}", user);
            String accessToken =  jwtUtil.generateToken(user);
            String refreshToken = jwtUtil.generateRefreshToken(new CustomUserDetails(user));
            return new TokenResponse(accessToken, refreshToken);
        } catch (Exception ex) {
            throw new Exception(ex);
        }
    }
}
