package com.tinqinacademy.authentication.core.processors;

import com.tinqinacademy.authentication.api.exceptionmodel.ErrorWrapper;
import com.tinqinacademy.authentication.api.operations.authenticateuser.AuthenticateUser;
import com.tinqinacademy.authentication.api.operations.authenticateuser.AuthenticateUserInput;
import com.tinqinacademy.authentication.api.operations.authenticateuser.AuthenticateUserOutput;
import com.tinqinacademy.authentication.api.operations.changepassword.ChangePassword;
import com.tinqinacademy.authentication.api.operations.changepassword.ChangePasswordInput;
import com.tinqinacademy.authentication.api.operations.changepassword.ChangePasswordOutput;
import com.tinqinacademy.authentication.core.base.BaseOperationProcessor;
import com.tinqinacademy.authentication.core.exception.ErrorMapper;
import com.tinqinacademy.authentication.core.exception.exceptions.NotFoundException;
import com.tinqinacademy.authentication.persistence.entity.UserEntity;
import com.tinqinacademy.authentication.persistence.repository.UserRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
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
public class ChangePasswordOperationProcessor extends BaseOperationProcessor implements ChangePassword {

    private final AuthenticateUser authenticateUser;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    protected ChangePasswordOperationProcessor(Validator validator, ConversionService conversionService, ErrorMapper errorMapper,
                                               AuthenticateUser authenticateUser, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        super(validator, conversionService, errorMapper);
        this.authenticateUser = authenticateUser;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Either<ErrorWrapper, ChangePasswordOutput> process(ChangePasswordInput input) {
        log.info("Start changePassword input {}", input);
        return validateInput(input).flatMap(validated->changePassword(input));
    }

    private Either<ErrorWrapper, ChangePasswordOutput> changePassword(ChangePasswordInput input) {
        return Try.of(() -> {
                checkIfUserIsAuthenticated(input.getToken());
                UserEntity existingUser = getUser(input.getEmail());
                checkIfInputPasswordMatchesUserPassword(input, existingUser);
                changePasswordOperation(input, existingUser);
                userRepository.save(existingUser);
                ChangePasswordOutput output = ChangePasswordOutput.builder()
                    .build();
                log.info("End changePassword output {}", output);
                return output;


            })
            .toEither()
            .mapLeft(throwable -> Match(throwable).of(
                Case($(instanceOf(AuthenticationException.class)), errorMapper.handleError(throwable, HttpStatus.FORBIDDEN)),
                Case($(instanceOf(NotFoundException.class)), errorMapper.handleError(throwable, HttpStatus.NOT_FOUND)),
                Case($(), errorMapper.handleError(throwable, HttpStatus.BAD_REQUEST))));
    }

    private void checkIfInputPasswordMatchesUserPassword(ChangePasswordInput input, UserEntity existingUser) {
        if (!passwordEncoder.matches(input.getOldPassword(), existingUser.getPassword())) {
            throw new NotFoundException("Old password entered is wrong!");
        }
    }

    private void changePasswordOperation(ChangePasswordInput input, UserEntity existingUser) {
        existingUser.setPassword(passwordEncoder.encode(input.getNewPassword()));
    }

    private void checkIfUserIsAuthenticated(String token) throws AuthenticationException {
        AuthenticateUserInput inputForAuthentication = AuthenticateUserInput.builder()
            .token(token)
            .build();

        Either<ErrorWrapper, AuthenticateUserOutput> output = authenticateUser.process(inputForAuthentication);
        if(output.isLeft()) {
            throw new AuthenticationException("Invalid token! User not authenticated, please login.");
        }
    }

    private UserEntity getUser (String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("User not found"));
    }

}
