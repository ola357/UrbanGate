package com.estateresource.estatemanger.estate.rest;


import com.estateresource.estatemanger.estate.dto.request.ActivationRequest;

import com.estateresource.estatemanger.estate.dto.response.ActivationResponse;
import com.estateresource.estatemanger.estate.service.impl.ActivationCodeServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/estate")
public class ActivationCodeController {

   private final ActivationCodeServiceImpl activationService;


    @PostMapping("activate")
    public ActivationResponse activate(@RequestBody ActivationRequest activationRequest) {
        log.info("Activation request: {}", activationRequest);
        return activationService.retrieveUserByActivationCode(activationRequest);
    }

}
