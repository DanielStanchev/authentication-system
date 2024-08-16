package com.tinqinacademy.authentication.core.processors;

import com.tinqinacademy.authentication.api.exceptionmodel.ErrorWrapper;
import com.tinqinacademy.authentication.api.operations.registeruser.RegisterUser;
import com.tinqinacademy.authentication.api.operations.registeruser.RegisterUserInput;
import com.tinqinacademy.authentication.api.operations.registeruser.RegisterUserOutput;
import com.tinqinacademy.authentication.core.base.BaseOperationProcessor;
import com.tinqinacademy.authentication.core.exception.ErrorMapper;
import com.tinqinacademy.authentication.persistence.entity.UserEntity;
import com.tinqinacademy.authentication.persistence.enums.Role;
import com.tinqinacademy.authentication.persistence.repository.UserRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@Service
public class RegisterUserOperationProcessor extends BaseOperationProcessor implements RegisterUser {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserOperationProcessor(ConversionService conversionService, Validator validator, ErrorMapper errorMapper, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        super(validator, conversionService,errorMapper);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Either<ErrorWrapper,RegisterUserOutput> process(RegisterUserInput input) {
        log.info("Start register input:{}.", input);
        return validateInput(input).flatMap(validated-> registerUser(input));
    }

    private Either<ErrorWrapper, RegisterUserOutput> registerUser(RegisterUserInput input) {
        return Try.of(()->{
            UserEntity registerUserEntity = getConvertedUserByInput(input);
            registerUserEntity.setRole(Role.USER);
            checkIfUserIsUnderAged(registerUserEntity);
            userRepository.save(registerUserEntity);
            RegisterUserOutput result = RegisterUserOutput.builder()
                .id(String.valueOf(registerUserEntity.getId()))
                .build();
            log.info("End register output:{}.", result);
            return result;

        }).toEither().mapLeft(throwable -> Match(throwable).of(
            Case($(instanceOf(IllegalArgumentException.class)), errorMapper.handleError(throwable, HttpStatus.BAD_REQUEST)),
            Case($(), errorMapper.handleError(throwable, HttpStatus.BAD_REQUEST))
        ));
    }

    private static void checkIfUserIsUnderAged(UserEntity registerUserEntity) {
        LocalDate birthDate = registerUserEntity.getBirthDate();
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        if (age < 18) {
            throw new IllegalArgumentException("User is underaged and cannot register");
        }
    }

    private UserEntity getConvertedUserByInput(RegisterUserInput input) {
       return conversionService.convert(input, UserEntity.UserEntityBuilder.class)
            .password(passwordEncoder.encode(input.getPassword()))
            .build();
    }
}
