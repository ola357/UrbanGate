package com.estateresource.estatemanger.resident.rest;

import com.estateresource.estatemanger.resident.dto.request.PasswordRequest;
import com.estateresource.estatemanger.resident.dto.response.PasswordResponse;
import com.estateresource.estatemanger.resident.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/resident")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PreAuthorize("hasRole('RESIDENT')")
    @PostMapping("/password")
    public PasswordResponse resetPassword(@RequestBody PasswordRequest passwordRequest) {
        return passwordResetService.resetPassword(passwordRequest);
    }


}
