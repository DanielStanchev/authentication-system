package com.tinqinacademy.authentication.core.processors;

import com.tinqinacademy.authentication.api.exceptionmodel.ErrorWrapper;
import com.tinqinacademy.authentication.api.operations.loginuser.LoginUser;
import com.tinqinacademy.authentication.api.operations.loginuser.LoginUserInput;
import com.tinqinacademy.authentication.api.operations.loginuser.LoginUserOutput;
import com.tinqinacademy.authentication.core.base.BaseOperationProcessor;
import com.tinqinacademy.authentication.core.security.JwtUtil;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@Service
public class LoginUserOperationProcessor extends BaseOperationProcessor implements LoginUser {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginUserOperationProcessor(ConversionService conversionService, Validator validator, ErrorMapper errorMapper, UserRepository userRepository,
                                       PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        super(validator, conversionService,errorMapper);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Either<ErrorWrapper, LoginUserOutput> process(LoginUserInput input) {
        log.info("Start login input:{}.", input);
        return validateInput(input).flatMap(validated->getLoginUserOutputs(input));
    }

    private Either<ErrorWrapper, LoginUserOutput> getLoginUserOutputs(LoginUserInput input) {
        return Try.of(() -> {
                UserEntity userToCheck = getUserEntity(input);
                checkIfPasswordMatches(input, userToCheck);
                String tokenCreated = jwtUtil.createToken(userToCheck.getId()
                                                       .toString(), userToCheck.getRole()
                                                       .toString());

                LoginUserOutput result = LoginUserOutput.builder()
                    .token(tokenCreated)
                    .build();

                log.info("End login result:{}.", result);
                return result;
            })
            .toEither()
            .mapLeft(throwable -> Match(throwable).of(
                Case($(instanceOf(NotFoundException.class)), errorMapper.handleError(throwable, HttpStatus.NOT_FOUND)),
                Case($(instanceOf(IllegalArgumentException.class)), errorMapper.handleError(throwable, HttpStatus.BAD_REQUEST)),
                Case($(), errorMapper.handleError(throwable, HttpStatus.BAD_REQUEST))));
    }

    private UserEntity getUserEntity(LoginUserInput input) {
        return userRepository.findByUsername(input.getUsername())
            .orElseThrow(() -> new NotFoundException("User not found."));
    }

    private void checkIfPasswordMatches(LoginUserInput input, UserEntity userToCheck) {
        if (userToCheck != null && !passwordEncoder.matches(input.getPassword(), userToCheck.getPassword())) {
            throw new IllegalArgumentException("Wrong password");
        }
    }
}
