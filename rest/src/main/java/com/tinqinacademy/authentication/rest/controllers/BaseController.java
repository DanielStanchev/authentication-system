package com.tinqinacademy.authentication.rest.controllers;

import com.tinqinacademy.authentication.api.base.OperationOutput;
import com.tinqinacademy.authentication.api.exceptionmodel.ErrorResponseInfo;
import com.tinqinacademy.authentication.api.exceptionmodel.ErrorWrapper;
import io.vavr.control.Either;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class BaseController {
    protected  ResponseEntity<?> handleResult(Either<ErrorWrapper,? extends OperationOutput> result, HttpStatus httpStatus) {

        if (result.isLeft()) {
            ErrorWrapper error = result.getLeft();
            HttpStatus errorStatus = error.getErrorResponseInfoList().stream()
                .findFirst()
                .map(ErrorResponseInfo::getHttpStatus)
                .orElse(HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(error, errorStatus);

        }
            OperationOutput output = result.get();
            return new ResponseEntity<>(output, httpStatus);
    }
}
