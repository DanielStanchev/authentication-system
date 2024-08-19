package com.tinqinacademy.authentication.core.processors;

import com.tinqinacademy.authentication.api.exceptionmodel.ErrorWrapper;
import com.tinqinacademy.authentication.api.operations.confirmregistration.ConfirmRegistration;
import com.tinqinacademy.authentication.api.operations.confirmregistration.ConfirmRegistrationInput;
import com.tinqinacademy.authentication.api.operations.confirmregistration.ConfirmRegistrationOutput;
import com.tinqinacademy.authentication.core.base.BaseOperationProcessor;
import com.tinqinacademy.authentication.core.exception.ErrorMapper;
import com.tinqinacademy.authentication.core.exception.exceptions.NotFoundException;
import com.tinqinacademy.authentication.persistence.entity.ActivationCode;
import com.tinqinacademy.authentication.persistence.entity.UserEntity;
import com.tinqinacademy.authentication.persistence.repository.ActivationCodeRepository;
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
public class ConfirmRegistrationOperationProcessor extends BaseOperationProcessor implements ConfirmRegistration {

    private final UserRepository userRepository;
    private final ActivationCodeRepository activationCodeRepository;

    protected ConfirmRegistrationOperationProcessor(Validator validator, ConversionService conversionService, ErrorMapper errorMapper,
                                                    UserRepository userRepository, ActivationCodeRepository activationCodeRepository) {
        super(validator, conversionService, errorMapper);
        this.userRepository = userRepository;
        this.activationCodeRepository = activationCodeRepository;
    }

    @Override
    public Either<ErrorWrapper, ConfirmRegistrationOutput> process(ConfirmRegistrationInput input) {
        log.info("Start confirmRegistration input{}",input);
        return validateInput(input).flatMap(validated->confirmRegistration(input));
    }

    private Either<ErrorWrapper, ConfirmRegistrationOutput> confirmRegistration(ConfirmRegistrationInput input) {
        return Try.of(() -> {
                ActivationCode activationCode = getCode(input.getConfirmationCode());
                UserEntity registeredUser = getUser(activationCode.getUserEmail());
                activateAccount(registeredUser);
                deleteUsedActivationCode(activationCode);
                userRepository.save(registeredUser);
                ConfirmRegistrationOutput output = ConfirmRegistrationOutput.builder()
                    .build();
                log.info("Finish confirmRegistration output{}", output);
                return output;

            })
            .toEither()
            .mapLeft(throwable -> Match(throwable).of(
                Case($(instanceOf(NotFoundException.class)), errorMapper.handleError(throwable, HttpStatus.NOT_FOUND)),
                Case($(), errorMapper.handleError(throwable, HttpStatus.BAD_REQUEST))));
    }

    private void deleteUsedActivationCode(ActivationCode activationCode) {
        activationCodeRepository.delete(activationCode);
    }

    private static void activateAccount(UserEntity registeredUser) {
        registeredUser.setIsAccountActivated(true);
    }

    private UserEntity getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(
            () -> new NotFoundException("User with such email not found!"));
    }

    private ActivationCode getCode(String code){
        return activationCodeRepository.findByActivationCode(code)
            .orElseThrow(() -> new NotFoundException("Such activation code is not existing!"));
    }
}
