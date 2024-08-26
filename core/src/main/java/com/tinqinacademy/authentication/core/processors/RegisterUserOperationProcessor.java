package com.tinqinacademy.authentication.core.processors;

import com.tinqinacademy.authentication.api.exceptionmodel.ErrorWrapper;
import com.tinqinacademy.authentication.api.operations.registeruser.RegisterUser;
import com.tinqinacademy.authentication.api.operations.registeruser.RegisterUserInput;
import com.tinqinacademy.authentication.api.operations.registeruser.RegisterUserOutput;
import com.tinqinacademy.authentication.core.base.BaseOperationProcessor;
import com.tinqinacademy.authentication.core.exception.ErrorMapper;
import com.tinqinacademy.authentication.kafka.model.EmailMessage;
import com.tinqinacademy.authentication.kafka.producer.KafkaEmailProducer;
import com.tinqinacademy.authentication.persistence.entity.ActivationCode;
import com.tinqinacademy.authentication.persistence.entity.UserEntity;
import com.tinqinacademy.authentication.persistence.enums.Role;
import com.tinqinacademy.authentication.persistence.repository.ActivationCodeRepository;
import com.tinqinacademy.authentication.persistence.repository.UserRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
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
    private final ActivationCodeRepository activationCodeRepository;
    private final KafkaEmailProducer kafkaEmailProducer;

    public RegisterUserOperationProcessor(ConversionService conversionService, Validator validator, ErrorMapper errorMapper, UserRepository userRepository, PasswordEncoder passwordEncoder,
                                          ActivationCodeRepository activationCodeRepository, KafkaEmailProducer kafkaEmailProducer) {
        super(validator, conversionService,errorMapper);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.activationCodeRepository = activationCodeRepository;
        this.kafkaEmailProducer = kafkaEmailProducer;
    }

    @Override
    public Either<ErrorWrapper,RegisterUserOutput> process(RegisterUserInput input) {
        log.info("Start register input:{}.", input);
        return validateInput(input).flatMap(validated-> registerUser(input));
    }

    private Either<ErrorWrapper, RegisterUserOutput> registerUser(RegisterUserInput input) {
        return Try.of(()->{
            UserEntity registerUserEntity = getConvertedUserByInput(input);
            checkIfUserAlreadyExists(input);
            checkIfUserIsUnderAged(registerUserEntity);
            setInitialRoleAsUser(registerUserEntity);
            UserEntity savedUser = userRepository.save(registerUserEntity);
            String generateActivationCode = getGenerateActivationCode();
            ActivationCode activationCode = getActivationCode(generateActivationCode, savedUser);
            saveActivationCodeInDB(activationCode);
            sendActivationCode(savedUser, activationCode);

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

    private void sendActivationCode(UserEntity savedUser, ActivationCode activationCode) {
        String subject = ("Activation Code: ");
        EmailMessage message = EmailMessage.builder()
            .to(savedUser.getEmail())
            .subject(subject)
            .content(activationCode.getActivationCode())
            .build();
        kafkaEmailProducer.sendEmailMessage(message);
    }

    private static void setInitialRoleAsUser(UserEntity registerUserEntity) {
        registerUserEntity.setRole(Role.USER);
    }

    private void saveActivationCodeInDB(ActivationCode activationCode) {
        activationCodeRepository.save(activationCode);
    }

    private static @NotNull String getGenerateActivationCode() {
        return RandomStringUtils.randomAlphanumeric(12);
    }

    private static ActivationCode getActivationCode(String generateActivationCode, UserEntity savedUser) {
        return ActivationCode.builder()
            .activationCode(generateActivationCode)
            .userEmail(savedUser.getEmail())
            .build();
    }

    private static void checkIfUserIsUnderAged(UserEntity registerUserEntity) {
        LocalDate birthDate = registerUserEntity.getBirthDate();
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        if (age < 18) {
            throw new IllegalArgumentException("User is underage and cannot register");
        }
    }

    private void checkIfUserAlreadyExists(RegisterUserInput input) {
        if(userRepository.existsByUsername(input.getUsername())){
            throw new IllegalArgumentException("Username already exists.");
        }
        if(userRepository.existsByEmail(input.getEmail())){
            throw new IllegalArgumentException("User with same Email address already exists.");
        }
        if(userRepository.existsByPhoneNo(input.getPhoneNo())){
            throw new IllegalArgumentException("User with same phone number already exists.");
        }
    }

    private UserEntity getConvertedUserByInput(RegisterUserInput input) {
       return conversionService.convert(input, UserEntity.UserEntityBuilder.class)
            .password(passwordEncoder.encode(input.getPassword()))
            .build();
    }
}
