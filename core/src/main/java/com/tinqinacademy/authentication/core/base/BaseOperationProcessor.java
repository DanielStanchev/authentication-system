package com.tinqinacademy.authentication.core.base;

import com.tinqinacademy.authentication.api.base.OperationInput;
import com.tinqinacademy.authentication.api.exceptionmodel.ErrorWrapper;
import com.tinqinacademy.authentication.core.exception.ErrorMapper;
import io.vavr.control.Either;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;

import java.util.Set;

@Slf4j
public abstract class BaseOperationProcessor {

    protected final Validator validator;
    protected final ConversionService conversionService;
    protected final ErrorMapper errorMapper;

    protected BaseOperationProcessor(Validator validator, ConversionService conversionService, ErrorMapper errorMapper) {
        this.validator = validator;
        this.conversionService = conversionService;
        this.errorMapper = errorMapper;
    }

    public Either<ErrorWrapper, ? extends OperationInput> validateInput(OperationInput input){

        Set<ConstraintViolation<OperationInput>> validationResponse = validator.validate(input);

        if(validationResponse.isEmpty()){
            return Either.right(input);
        }
        ErrorWrapper validationErrors = errorMapper.handleValidationViolation(validationResponse, HttpStatus.BAD_REQUEST);
        return Either.left(validationErrors);
    }
}
