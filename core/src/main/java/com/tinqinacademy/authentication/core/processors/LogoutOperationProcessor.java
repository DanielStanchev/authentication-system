package com.tinqinacademy.authentication.core.processors;

import com.tinqinacademy.authentication.api.exceptionmodel.ErrorWrapper;
import com.tinqinacademy.authentication.api.operations.authenticateuser.AuthenticateUser;
import com.tinqinacademy.authentication.api.operations.authenticateuser.AuthenticateUserInput;
import com.tinqinacademy.authentication.api.operations.authenticateuser.AuthenticateUserOutput;
import com.tinqinacademy.authentication.api.operations.logoutuser.LogoutUser;
import com.tinqinacademy.authentication.api.operations.logoutuser.LogoutUserInput;
import com.tinqinacademy.authentication.api.operations.logoutuser.LogoutUserOutput;
import com.tinqinacademy.authentication.core.base.BaseOperationProcessor;
import com.tinqinacademy.authentication.core.exception.ErrorMapper;
import com.tinqinacademy.authentication.core.exception.exceptions.NotFoundException;
import com.tinqinacademy.authentication.persistence.entity.BlacklistedToken;
import com.tinqinacademy.authentication.persistence.repository.BlacklistedTokenRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@Service
public class LogoutOperationProcessor extends BaseOperationProcessor implements LogoutUser {

    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final AuthenticateUser authenticateUser;

    public LogoutOperationProcessor(Validator validator, ConversionService conversionService, ErrorMapper errorMapper,
                                    BlacklistedTokenRepository blacklistedTokenRepository, AuthenticateUser authenticateUser) {
        super(validator, conversionService, errorMapper);
        this.blacklistedTokenRepository = blacklistedTokenRepository;
        this.authenticateUser = authenticateUser;
    }

    @Override
    public Either<ErrorWrapper, LogoutUserOutput> process(LogoutUserInput input) {
        log.info("Start logoutUser input: {}", input);
        return validateInput(input).flatMap(validated->logoutUser(input));
    }

    private Either<ErrorWrapper, LogoutUserOutput> logoutUser(LogoutUserInput input) {
        return Try.of(() -> {
                checkIfUserIsAuthenticated(input.getToken());
                saveBlackListedToken(input);
                LogoutUserOutput output = LogoutUserOutput.builder()
                    .build();
                log.info("End logoutUser output: {}", output);
                return output;
            })
            .toEither()
            .mapLeft(throwable -> Match(throwable).of(
                Case($(instanceOf(NotFoundException.class)), errorMapper.handleError(throwable, HttpStatus.NOT_FOUND)),
                Case($(), errorMapper.handleError(throwable, HttpStatus.BAD_REQUEST))));
    }

    private void saveBlackListedToken(LogoutUserInput input) {
        BlacklistedToken token = BlacklistedToken.builder()
            .token(input.getToken())
            .build();
        blacklistedTokenRepository.save(token);
    }

    private void checkIfUserIsAuthenticated(String token) throws AuthenticationException {
        AuthenticateUserInput inputForAuthentication = AuthenticateUserInput.builder()
            .token(token)
            .build();

        Either<ErrorWrapper, AuthenticateUserOutput> output = authenticateUser.process(inputForAuthentication);
        if(output.isLeft()) {
            throw new AuthenticationException("Invalid token! User not authenticated.");
        }
    }
}
