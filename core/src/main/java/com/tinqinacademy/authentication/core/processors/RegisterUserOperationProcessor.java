package com.tinqinacademy.authentication.core.processors;

import com.tinqinacademy.authentication.api.exceptionmodel.ErrorWrapper;
import com.tinqinacademy.authentication.api.operations.registeruser.RegisterUser;
import com.tinqinacademy.authentication.api.operations.registeruser.RegisterUserInput;
import com.tinqinacademy.authentication.api.operations.registeruser.RegisterUserOutput;
import com.tinqinacademy.authentication.core.base.BaseOperationProcessor;
import com.tinqinacademy.authentication.core.exception.ErrorMapper;
import com.tinqinacademy.authentication.persistence.entity.User;
import com.tinqinacademy.authentication.persistence.repository.UserRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

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
        User registerUser = getConvertedUserByInput(input);
        userRepository.save(registerUser);
        RegisterUserOutput result = RegisterUserOutput.builder()
                .id(String.valueOf(registerUser.getId()))
                .build();

        log.info("End register output:{}.", result);
        return result;
        }).toEither().mapLeft(throwable -> Match(throwable).of(
            Case($(), errorMapper.handleError(throwable, HttpStatus.BAD_REQUEST))
        ));
    }

    private User getConvertedUserByInput(RegisterUserInput input) {
       return conversionService.convert(input, User.UserBuilder.class)
            .password(passwordEncoder.encode(input.getPassword()))
            .build();
    }
}
