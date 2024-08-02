package com.tinqinacademy.authentication.core.exception;

import com.tinqinacademy.authentication.api.base.OperationInput;
import com.tinqinacademy.authentication.api.exceptionmodel.ErrorResponseInfo;
import com.tinqinacademy.authentication.api.exceptionmodel.ErrorWrapper;
import jakarta.validation.ConstraintViolation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class ErrorMapper {
    public ErrorWrapper handleError(Throwable ex, HttpStatus httpStatus) {
        return ErrorWrapper.builder()
            .errorResponseInfoList(List.of(ErrorResponseInfo.builder()
                                               .message(ex.getMessage())
                                               .httpStatus(httpStatus)
                                               .build()))
            .build();
    }

    public ErrorWrapper handleValidationViolation(Set<ConstraintViolation<OperationInput>> violations, HttpStatus httpStatus) {
        List<ErrorResponseInfo> responses = violations.stream()
            .map(v -> ErrorResponseInfo.builder()
                .message(v.getMessage())
                .httpStatus(httpStatus)
                .build())
            .toList();

        return ErrorWrapper.builder()
            .errorResponseInfoList(responses)
            .build();
    }
}