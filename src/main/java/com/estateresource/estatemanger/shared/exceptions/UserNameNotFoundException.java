package com.estateresource.estatemanger.shared.exceptions;

import com.estateresource.estatemanger.shared.enums.ExceptionResponse;
import lombok.Getter;

public class UserNameNotFoundException extends RuntimeException
{
    @Getter
    private String code;

    public UserNameNotFoundException(String message) {
        super(message);
    }


    public UserNameNotFoundException(ExceptionResponse response){
        super(response.getMessage());
        this.code = response.getCode();
    }
}
