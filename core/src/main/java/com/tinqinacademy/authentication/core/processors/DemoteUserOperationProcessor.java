package com.tinqinacademy.authentication.core.processors;

import com.tinqinacademy.authentication.api.exceptionmodel.ErrorWrapper;
import com.tinqinacademy.authentication.api.operations.demoteuser.DemoteUser;
import com.tinqinacademy.authentication.api.operations.demoteuser.DemoteUserInput;
import com.tinqinacademy.authentication.api.operations.demoteuser.DemoteUserOutput;
import com.tinqinacademy.authentication.core.base.BaseOperationProcessor;
import com.tinqinacademy.authentication.core.exception.ErrorMapper;
import com.tinqinacademy.authentication.core.exception.exceptions.NotFoundException;
import com.tinqinacademy.authentication.persistence.entity.UserEntity;
import com.tinqinacademy.authentication.persistence.enums.Role;
import com.tinqinacademy.authentication.persistence.repository.UserRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@Service
public class DemoteUserOperationProcessor extends BaseOperationProcessor implements DemoteUser {

    private final UserRepository userRepository;

    public DemoteUserOperationProcessor(UserRepository userRepository, ConversionService conversionService, Validator validator,
                                         ErrorMapper errorMapper) {
        super(validator, conversionService, errorMapper);
        this.userRepository = userRepository;
    }

    @Override
    public Either<ErrorWrapper, DemoteUserOutput> process(DemoteUserInput input) {
        log.info("Start demoteUser input:{}.", input);
        return validateInput(input).flatMap(validated -> promoteUser(input));
    }

    private Either<ErrorWrapper, DemoteUserOutput> promoteUser(DemoteUserInput input) {
        return Try.of(() -> {
                UserEntity userForDemotion = getUserEntity(input);
                checkIfUserForDemotionIsMasterAdmin(userForDemotion);
                checkIfUserIsAlreadyUser(userForDemotion);
                demoteUser(userForDemotion);
                userRepository.save(userForDemotion);
                DemoteUserOutput output = DemoteUserOutput.builder()
                    .build();
                log.info("End demoteUser output:{}.", output);
                return output;

            })
            .toEither()
            .mapLeft(throwable -> Match(throwable).of(
                Case($(instanceOf(NotFoundException.class)), errorMapper.handleError(throwable, HttpStatus.NOT_FOUND)),
                Case($(instanceOf(IllegalArgumentException.class)), errorMapper.handleError(throwable, HttpStatus.BAD_REQUEST)),
                Case($(), errorMapper.handleError(throwable, HttpStatus.BAD_REQUEST))));
    }

    private static void checkIfUserForDemotionIsMasterAdmin(UserEntity userForDemotion) {
        if(userForDemotion.getUsername().equals("admin")){
            throw new IllegalArgumentException("You are not allowed to demote master admin!");
        }
    }

    private static void demoteUser(UserEntity userForDemotion) {
        userForDemotion.setRole(Role.USER);
    }

    private static void checkIfUserIsAlreadyUser(UserEntity userForPromotion) {
        if (userForPromotion.getRole()
            .equals(Role.USER)) {
            throw new IllegalArgumentException("You are not allowed to demote users that are already with user role!");
        }
    }

    private UserEntity getUserEntity(DemoteUserInput input) {
        return userRepository.findById(UUID.fromString(input.getUserId()))
            .orElseThrow(() -> new NotFoundException("No such user existing"));
    }

}
