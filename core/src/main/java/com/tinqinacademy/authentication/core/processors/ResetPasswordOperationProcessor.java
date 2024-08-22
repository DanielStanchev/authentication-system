package com.tinqinacademy.authentication.core.processors;

import com.tinqinacademy.authentication.api.exceptionmodel.ErrorWrapper;
import com.tinqinacademy.authentication.api.operations.resetpassword.ResetPassword;
import com.tinqinacademy.authentication.api.operations.resetpassword.ResetPasswordInput;
import com.tinqinacademy.authentication.api.operations.resetpassword.ResetPasswordOutput;
import com.tinqinacademy.authentication.core.base.BaseOperationProcessor;
import com.tinqinacademy.authentication.core.exception.ErrorMapper;
import com.tinqinacademy.authentication.core.exception.exceptions.NotFoundException;
import com.tinqinacademy.authentication.persistence.entity.RecoverPasswordEntity;
import com.tinqinacademy.authentication.persistence.entity.UserEntity;
import com.tinqinacademy.authentication.persistence.repository.RecoverPasswordRepository;
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
public class ResetPasswordOperationProcessor extends BaseOperationProcessor implements ResetPassword {

    private final RecoverPasswordRepository recoverPasswordRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    protected ResetPasswordOperationProcessor(Validator validator, ConversionService conversionService, ErrorMapper errorMapper,
                                              RecoverPasswordRepository recoverPasswordRepository, PasswordEncoder passwordEncoder,
                                              UserRepository userRepository) {
        super(validator, conversionService, errorMapper);
        this.recoverPasswordRepository = recoverPasswordRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public Either<ErrorWrapper, ResetPasswordOutput> process(ResetPasswordInput input) {
        log.info("Start resetPassword input{}",input);
        return validateInput(input).flatMap(validated->resetPassword(input));
    }

    private Either<ErrorWrapper, ResetPasswordOutput> resetPassword(ResetPasswordInput input) {
        return Try.of(() -> {
                RecoverPasswordEntity recoveryPassword = getRecoveryCode(input.getRecoveryCode());
                UserEntity existingUser = getUser(recoveryPassword.getEmail());
                setAndSaveUserNewPassword(input, existingUser);
                deleteRecoveryCodeFromDB(recoveryPassword);
                ResetPasswordOutput output = ResetPasswordOutput.builder()
                    .build();
                log.info("End resetPassword output{}", output);
                return output;
            })
            .toEither()
            .mapLeft(throwable -> Match(throwable).of(
                Case($(instanceOf(NotFoundException.class)), errorMapper.handleError(throwable, HttpStatus.NOT_FOUND)),
                Case($(), errorMapper.handleError(throwable, HttpStatus.BAD_REQUEST))));
    }

    private void setAndSaveUserNewPassword(ResetPasswordInput input, UserEntity existingUser) {
        existingUser.toBuilder().password(passwordEncoder.encode(input.getNewPassword())).build();
        userRepository.save(existingUser);
    }

    private void deleteRecoveryCodeFromDB(RecoverPasswordEntity recoveryCode) {
        recoverPasswordRepository.delete(recoveryCode);
    }


    private UserEntity getUser(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("User with such email do not exists."));
    }

    private RecoverPasswordEntity getRecoveryCode(String code) {
        return recoverPasswordRepository.findByRecoveryCode(code)
            .orElseThrow(() -> new NotFoundException("No such recovery code exists.Please try again."));
    }
}
