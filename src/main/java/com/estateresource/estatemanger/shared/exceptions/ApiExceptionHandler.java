package com.estateresource.estatemanger.shared.exceptions;

import com.estateresource.estatemanger.shared.dto.ApiResponse;
import com.estateresource.estatemanger.shared.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;
import java.util.stream.Collectors;

@ResponseBody
@ControllerAdvice
public class ApiExceptionHandler {


    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(DataBaseOperationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleDatabaseOperationException(DataBaseOperationException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setErrorCode(ex.getCode());
        error.setErrorDescription(ex.getMessage());
        error.setStatus("Failed");
        return error;
    }


    @ExceptionHandler(UserNameNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserNameNotFoundException(UserNameNotFoundException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setErrorCode(ex.getCode());
        error.setErrorDescription(ex.getMessage());
        error.setStatus("Failed");
        return error;
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception ex) {
        ErrorResponse error = new ErrorResponse();
//        error.setErrorCode(ex.get());
        error.setErrorDescription("Error Processing this request");
        error.setStatus("Failed");
        return error;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {

        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        DefaultMessageSourceResolvable::getDefaultMessage,
                        (msg1, msg2) -> msg1
                ));


        ErrorResponse response = new ErrorResponse();
        response.setStatus("Validation Failed");
        response.setFieldsErrors(errors);
        return response;
    }
}
