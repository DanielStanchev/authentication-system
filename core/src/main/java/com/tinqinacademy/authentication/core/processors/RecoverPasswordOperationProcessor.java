package com.tinqinacademy.authentication.core.processors;

import com.tinqinacademy.authentication.api.exceptionmodel.ErrorWrapper;
import com.tinqinacademy.authentication.api.operations.recoverpassword.RecoverPassword;
import com.tinqinacademy.authentication.api.operations.recoverpassword.RecoverPasswordInput;
import com.tinqinacademy.authentication.api.operations.recoverpassword.RecoverPasswordOutput;
import com.tinqinacademy.authentication.core.base.BaseOperationProcessor;
import com.tinqinacademy.authentication.core.emailsender.EmailSenderService;
import com.tinqinacademy.authentication.core.exception.ErrorMapper;
import com.tinqinacademy.authentication.core.exception.exceptions.NotFoundException;
import com.tinqinacademy.authentication.persistence.entity.RecoverPasswordEntity;
import com.tinqinacademy.authentication.persistence.repository.RecoverPasswordRepository;
import com.tinqinacademy.authentication.persistence.repository.UserRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@Service
public class RecoverPasswordOperationProcessor extends BaseOperationProcessor implements RecoverPassword {

    private final UserRepository userRepository;
    private final RecoverPasswordRepository recoverPasswordRepository;
    private final EmailSenderService emailSenderService;

    protected RecoverPasswordOperationProcessor(Validator validator, ConversionService conversionService, ErrorMapper errorMapper,
                                                UserRepository userRepository, RecoverPasswordRepository recoverPasswordRepository,
                                                EmailSenderService emailSenderService) {
        super(validator, conversionService, errorMapper);
        this.userRepository = userRepository;
        this.recoverPasswordRepository = recoverPasswordRepository;
        this.emailSenderService = emailSenderService;
    }

    @Override
    public Either<ErrorWrapper, RecoverPasswordOutput> process(RecoverPasswordInput input) {
        log.info("Start recoverPassword input{}",input);
        return validateInput(input).flatMap(validated->recoverPassword(input));
    }

    private Either<ErrorWrapper, RecoverPasswordOutput> recoverPassword(RecoverPasswordInput input) {
        return Try.of(() -> {
                checkIfUserEmailExists(input);
                String generatePasswordRecoveryCode = generatePasswordRecoveryCode();
                generatePasswordRecoveryCode = checkIfGeneratedCodeAlreadyExists(generatePasswordRecoveryCode);
                RecoverPasswordEntity recoverPasswordCode = getRecoverCode(generatePasswordRecoveryCode, input.getEmail());
                recoverPasswordRepository.save(recoverPasswordCode);
                sendRecoverPasswordCode(input.getEmail(), recoverPasswordCode.toString());
                RecoverPasswordOutput output = RecoverPasswordOutput.builder()
                    .build();
                log.info("End recoverPassword output{}", output);
                return output;
            })
            .toEither()
            .mapLeft(throwable -> Match(throwable).of(
                Case($(instanceOf(NotFoundException.class)), errorMapper.handleError(throwable, HttpStatus.NOT_FOUND)),
                Case($(), errorMapper.handleError(throwable, HttpStatus.BAD_REQUEST))));
    }

    private String checkIfGeneratedCodeAlreadyExists(String generatePasswordRecoveryCode) {
        if(recoverPasswordRepository.existsByRecoveryCode(generatePasswordRecoveryCode)){
            generatePasswordRecoveryCode =generatePasswordRecoveryCode();
        }
        return generatePasswordRecoveryCode;
    }

    private void sendRecoverPasswordCode(String email, String code) {
        emailSenderService.sendEmail(email, "Recover password Code:",
                                     String.format("The registration code is: %s", code));
    }

    private static RecoverPasswordEntity getRecoverCode(String generatePasswordRecoveryCode, String email) {
        return RecoverPasswordEntity.builder()
            .recoveryCode(generatePasswordRecoveryCode)
            .email(email)
            .build();
    }

    private static @NotNull String generatePasswordRecoveryCode() {
        return RandomStringUtils.randomAlphanumeric(12);
    }

    private void checkIfUserEmailExists(RecoverPasswordInput input) {
        boolean checkIfUserEmailExists =userRepository.existsByEmail(input.getEmail());
        if(!checkIfUserEmailExists){
            throw new NotFoundException("User with such email do not exists");
        }
    }
}
