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
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    public AuthenticationOperationProcessor(ConversionService conversionService, Validator validator, ErrorMapper errorMapper,
                                            JwtUtil jwtUtil, UserRepository userRepository, BlacklistedTokenRepository blacklistedTokenRepository,
                                            BlacklistedTokenRepository blacklistedTokenRepository1) {
        super(validator, conversionService,errorMapper);
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.blacklistedTokenRepository = blacklistedTokenRepository1;
    }

    @Override
    public Either<ErrorWrapper, AuthenticateUserOutput> process(AuthenticateUserInput input) {
        log.info("Start authenticateUser input{}",input);
        return validateInput(input).flatMap(validated ->authenticateUser(input));
    }

    private Either<ErrorWrapper, AuthenticateUserOutput> authenticateUser(AuthenticateUserInput input) {
        return Try.of(() -> {
                String token = jwtUtil.getToken(input.getToken());
                JwtTokenInfo tokenInfo = jwtUtil.retrieveTokenClaims(token);
                validateToken(String.valueOf(token));
                UserEntity user = getUserEntity(tokenInfo);
                checkIfTokenRoleMatchesUserRole(user, tokenInfo);
                AuthenticateUserOutput output = AuthenticateUserOutput.builder()
                    .id(String.valueOf(user.getId()))
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

    private void validateToken(String token) throws IllegalAccessException {
        if (blacklistedTokenRepository.existsByToken(token) ||
            !jwtUtil.validateToken(token)) {
            throw new IllegalAccessException (String.format("Invalid token: %s", token));
        }
    }

    private static void checkIfTokenRoleMatchesUserRole(UserEntity user, JwtTokenInfo jwtTokenInfo) throws IllegalAccessException {
        if (!user.getRole()
            .equals(jwtTokenInfo.getRole())) {
            throw new IllegalAccessException("Invalid role.");
        }
    }

    private UserEntity getUserEntity(JwtTokenInfo jwtTokenInfo) {
       return userRepository.findById(jwtTokenInfo.getId())
            .orElseThrow(() -> new NotFoundException("No such User found"));
    }
}

