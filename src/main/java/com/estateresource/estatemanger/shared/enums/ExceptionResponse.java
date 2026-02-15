package com.estateresource.estatemanger.shared.enums;

import lombok.Getter;

public enum ExceptionResponse {

    USER_NAME_NOTFOUND("1000", "The Username is not found"),
    UNABLE_TO_INSERT_RECORD("1001", "Database Insertion failed"),
    UNABLE_TO_FETCH_RECORD("1002", "Unable to fetch record"),
    UNABLE_TO_REVOKE_ACTIVATION_CODE("1003", "Could not revoke activation code"),
    INVALID_ACTIVATION_CODE("1004", "Invalid activation code"),
    ACTIVATION_CODE_REVOKED("1005", "Activation code revoked" ),;

    @Getter
    private String code;
    @Getter
    private String message;

    ExceptionResponse(String message, String code) {
        this.message = message;
        this.code = code;
    }


}
