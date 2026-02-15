package com.estateresource.estatemanger.resident.service;

import com.estateresource.estatemanger.resident.dto.request.PasswordRequest;
import com.estateresource.estatemanger.resident.dto.response.PasswordResponse;

public interface PasswordResetService {

    PasswordResponse resetPassword(PasswordRequest passwordRequest);
}
