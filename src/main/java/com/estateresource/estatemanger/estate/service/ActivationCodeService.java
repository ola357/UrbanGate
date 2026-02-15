package com.estateresource.estatemanger.estate.service;

import com.estateresource.estatemanger.estate.dto.request.ActivationRequest;
import com.estateresource.estatemanger.estate.dto.response.ActivationResponse;

public interface ActivationCodeService {

    ActivationResponse retrieveUserByActivationCode(ActivationRequest activationRequest);
}
