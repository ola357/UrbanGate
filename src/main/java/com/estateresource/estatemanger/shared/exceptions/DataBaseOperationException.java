package com.estateresource.estatemanger.shared.exceptions;

import com.estateresource.estatemanger.shared.enums.ExceptionResponse;
import lombok.Getter;

public class DataBaseOperationException extends RuntimeException
{
    @Getter
    private String code;

    public DataBaseOperationException(String message) {
        super(message);
    }


    public DataBaseOperationException(ExceptionResponse response){
        super(response.getMessage());
        this.code = response.getCode();
    }
}
