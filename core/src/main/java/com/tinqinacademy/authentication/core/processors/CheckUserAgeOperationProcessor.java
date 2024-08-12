package com.tinqinacademy.authentication.core.processors;

import com.tinqinacademy.authentication.api.exceptionmodel.ErrorWrapper;
import com.tinqinacademy.authentication.api.operations.checkuserage.CheckUserAge;
import com.tinqinacademy.authentication.api.operations.checkuserage.CheckUserAgeInput;
import com.tinqinacademy.authentication.api.operations.checkuserage.CheckUserAgeOutput;
import com.tinqinacademy.authentication.core.base.BaseOperationProcessor;
import com.tinqinacademy.authentication.core.exception.ErrorMapper;
import com.tinqinacademy.authentication.core.exception.exceptions.NotFoundException;
import com.tinqinacademy.authentication.persistence.entity.UserEntity;
import com.tinqinacademy.authentication.persistence.repository.UserRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@Service
public class CheckUserAgeOperationProcessor extends BaseOperationProcessor implements CheckUserAge {

    private final UserRepository userRepository;

    public CheckUserAgeOperationProcessor(ConversionService conversionService, Validator validator, ErrorMapper errorMapper,
                                          UserRepository userRepository) {
        super(validator, conversionService,errorMapper);
        this.userRepository = userRepository;
    }

    @Override
    public Either<ErrorWrapper, CheckUserAgeOutput> process(CheckUserAgeInput input) {
        log.info("Start checkUserAge input{}", input);
        return validateInput(input).flatMap(validated->checkUserAge(input));
    }

    private Either<ErrorWrapper,CheckUserAgeOutput> checkUserAge(CheckUserAgeInput input) {
        return Try.of(()->{
            UserEntity userToCheck = getUser(input);
            LocalDate birthDate = userToCheck.getBirthDate();
            int age = Period.between(birthDate, LocalDate.now()).getYears();

            CheckUserAgeOutput output = CheckUserAgeOutput.builder()
                .age(age)
                .build();

            log.info("End checkUserAge output{}",output);
            return output;

        }).toEither().mapLeft(throwable -> Match(throwable).of(
            Case($(instanceOf(NotFoundException.class)), errorMapper.handleError(throwable, HttpStatus.NOT_FOUND))));
    }

    private UserEntity getUser(CheckUserAgeInput input) {
        return userRepository.findById(UUID.fromString(input.getUserId()))
            .orElseThrow(()-> new NotFoundException("Not User with such ID found."));
    }
}

