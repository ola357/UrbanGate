package com.estateresource.estatemanger.estate.service;

import com.estateresource.estatemanger.backoffice.dto.response.EstateOnboardingResponse;
import com.estateresource.estatemanger.estate.dto.request.ResidentOnboardingRequest;
import com.estateresource.estatemanger.estate.dto.response.ResidentOnboardingResponse;
import com.estateresource.estatemanger.shared.entity.Estate;

public interface ResidentOnboardingService {

    ResidentOnboardingResponse onboardResident(ResidentOnboardingRequest residentOnboardingRequest, Estate estate);
}
