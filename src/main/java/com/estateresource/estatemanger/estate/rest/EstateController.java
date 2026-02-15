package com.estateresource.estatemanger.estate.rest;

import com.estateresource.estatemanger.estate.dto.request.ResidentOnboardingRequest;
import com.estateresource.estatemanger.estate.dto.response.ResidentOnboardingResponse;
import com.estateresource.estatemanger.estate.service.impl.ResidentOnboardingServiceImpl;
import com.estateresource.estatemanger.security.model.CustomUserDetails;
import com.estateresource.estatemanger.shared.aspect.AuthorizedEstate;
import com.estateresource.estatemanger.shared.aspect.OnboardedEstate;
import com.estateresource.estatemanger.shared.entity.Estate;
import com.estateresource.estatemanger.shared.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/estate")
public class EstateController {

    private final ResidentOnboardingServiceImpl residentOnboardingService;

    @AuthorizedEstate
    @PostMapping("/resident")
    @PreAuthorize("hasRole('ADMIN')")
    public ResidentOnboardingResponse createResident(@OnboardedEstate Estate estate,
                                                     @RequestBody ResidentOnboardingRequest residentOnboardingRequest,
                                                     @AuthenticationPrincipal CustomUserDetails user) {

        return residentOnboardingService.onboardResident(residentOnboardingRequest, estate);
    }
}
