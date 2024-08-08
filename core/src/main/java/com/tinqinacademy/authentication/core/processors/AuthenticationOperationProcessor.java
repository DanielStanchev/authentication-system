package com.tinqinacademy.authentication.core.processors;

import com.tinqinacademy.authentication.api.exceptionmodel.ErrorWrapper;
import com.tinqinacademy.authentication.api.operations.authenticateuser.AuthenticateUser;
import com.tinqinacademy.authentication.api.operations.authenticateuser.AuthenticateUserInput;
import com.tinqinacademy.authentication.api.operations.authenticateuser.AuthenticateUserOutput;
import com.tinqinacademy.authentication.core.base.BaseOperationProcessor;
import com.tinqinacademy.authentication.core.security.JwtTokenInfo;
import com.tinqinacademy.authentication.core.security.JwtUtil;
import com.tinqinacademy.authentication.core.exception.ErrorMapper;
import com.tinqinacademy.authentication.core.exception.exceptions.NotFoundException;
import com.tinqinacademy.authentication.persistence.entity.UserEntity;
import com.tinqinacademy.authentication.persistence.repository.BlacklistedTokenRepository;
import com.tinqinacademy.authentication.persistence.repository.UserRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@Service
public class AuthenticationOperationProcessor extends BaseOperationProcessor implements AuthenticateUser {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public AuthenticationOperationProcessor(ConversionService conversionService, Validator validator, ErrorMapper errorMapper,
                                            JwtUtil jwtUtil, UserRepository userRepository, BlacklistedTokenRepository blacklistedTokenRepository) {
        super(validator, conversionService,errorMapper);
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public Either<ErrorWrapper, AuthenticateUserOutput> process(AuthenticateUserInput input) {
        log.info("Start authenticateUser input{}",input);
        return validateInput(input).flatMap(validated ->authenticateUser(input));
    }

    private Either<ErrorWrapper, AuthenticateUserOutput> authenticateUser(AuthenticateUserInput input) {
        return Try.of(() -> {
                jwtUtil.validateToken(input.getToken());
                JwtTokenInfo jwtTokenInfo = jwtUtil.extractToken(input.getToken());
                UserEntity user = getUserEntity(jwtTokenInfo);
                checkIfTokenRoleMatchesUserRole(user, jwtTokenInfo);

                AuthenticateUserOutput output = AuthenticateUserOutput.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .role(user.getRole().toString())
                    .build();

                log.info("End authenticateUser output {}", output);
                return output;

            })
            .toEither()
            .mapLeft(throwable -> Match(throwable).of(
                Case($(instanceOf(NotFoundException.class)), errorMapper.handleError(throwable, HttpStatus.NOT_FOUND)),
                Case($(instanceOf(IllegalArgumentException.class)), errorMapper.handleError(throwable, HttpStatus.BAD_REQUEST)),
                Case($(), errorMapper.handleError(throwable, HttpStatus.BAD_REQUEST))));
    }

    private static void checkIfTokenRoleMatchesUserRole(UserEntity user, JwtTokenInfo jwtTokenInfo) throws IllegalAccessException {
        if (!user.getRole()
            .equals(jwtTokenInfo.getRole())) {
            throw new IllegalAccessException("Invalid role");
        }
    }

    private UserEntity getUserEntity(JwtTokenInfo jwtTokenInfo) {
       return userRepository.findById(jwtTokenInfo.getId())
            .orElseThrow(() -> new NotFoundException("No such User found"));
    }
}

