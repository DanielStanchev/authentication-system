package com.tinqinacademy.authentication.core.processors;

import com.tinqinacademy.authentication.api.exceptionmodel.ErrorWrapper;
import com.tinqinacademy.authentication.api.operations.promoteuser.PromoteUser;
import com.tinqinacademy.authentication.api.operations.promoteuser.PromoteUserInput;
import com.tinqinacademy.authentication.api.operations.promoteuser.PromoteUserOutput;
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
public class PromoteUserOperationProcessor extends BaseOperationProcessor implements PromoteUser {

    private final UserRepository userRepository;

    public PromoteUserOperationProcessor(UserRepository userRepository, ConversionService conversionService, Validator validator,
                                         ErrorMapper errorMapper) {
        super(validator, conversionService, errorMapper);
        this.userRepository = userRepository;
    }

    @Override
    public Either<ErrorWrapper, PromoteUserOutput> process(PromoteUserInput input) {
        log.info("Start promoteUser input:{}.", input);
        return validateInput(input).flatMap(validated -> promoteUser(input));
    }

    private Either<ErrorWrapper, PromoteUserOutput> promoteUser(PromoteUserInput input) {
        return Try.of(() -> {
                UserEntity userForPromotion = getUserEntity(input);
                checkIfUserIsAlreadyAdmin(userForPromotion);
                promoteTheUser(userForPromotion);
                userRepository.save(userForPromotion);
                PromoteUserOutput output = PromoteUserOutput.builder()
                    .build();
                log.info("End promoteUser output:{}.", output);
                return output;

            })
            .toEither()
            .mapLeft(throwable -> Match(throwable).of(
                Case($(instanceOf(NotFoundException.class)), errorMapper.handleError(throwable, HttpStatus.NOT_FOUND)),
                Case($(instanceOf(IllegalArgumentException.class)), errorMapper.handleError(throwable, HttpStatus.BAD_REQUEST)),
                Case($(), errorMapper.handleError(throwable, HttpStatus.BAD_REQUEST))));
    }

    private static void promoteTheUser(UserEntity userForPromotion) {
        userForPromotion.setRole(Role.ADMIN);
    }

    private static void checkIfUserIsAlreadyAdmin(UserEntity userForPromotion) {
        if (userForPromotion.getRole()
            .equals(Role.ADMIN)) {
            throw new IllegalArgumentException("You are not allowed to promote users that are already with admin role!");
        }
    }

    private UserEntity getUserEntity(PromoteUserInput input) {
        return userRepository.findById(UUID.fromString(input.getUserId()))
            .orElseThrow(() -> new NotFoundException("No such user existing"));
    }
}
