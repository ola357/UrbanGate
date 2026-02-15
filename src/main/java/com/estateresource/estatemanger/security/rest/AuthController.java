package com.estateresource.estatemanger.security.rest;


import com.estateresource.estatemanger.security.model.AuthRequest;
import com.estateresource.estatemanger.security.model.response.TokenResponse;
import com.estateresource.estatemanger.security.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("api/auth")
@RequiredArgsConstructor
@Slf4j
@RestController
public class AuthController {


    private final AuthService authService;

    @PostMapping("/authenticate")
    public TokenResponse generateToken(@RequestBody AuthRequest authRequest) throws Exception {
        log.debug("User Logging in....");
        return authService.authenticateUser(authRequest);
    }
}