package com.tinqinacademy.authentication.rest.controllers;

import com.tinqinacademy.authentication.api.exceptionmodel.ErrorWrapper;
import com.tinqinacademy.authentication.api.operations.authenticateuser.AuthenticateUser;
import com.tinqinacademy.authentication.api.operations.authenticateuser.AuthenticateUserInput;
import com.tinqinacademy.authentication.api.operations.authenticateuser.AuthenticateUserOutput;
import com.tinqinacademy.authentication.api.operations.loginuser.LoginUser;
import com.tinqinacademy.authentication.api.operations.loginuser.LoginUserInput;
import com.tinqinacademy.authentication.api.operations.loginuser.LoginUserOutput;
import com.tinqinacademy.authentication.api.operations.registeruser.RegisterUser;
import com.tinqinacademy.authentication.api.operations.registeruser.RegisterUserInput;
import com.tinqinacademy.authentication.api.operations.registeruser.RegisterUserOutput;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vavr.control.Either;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User API", description = "User related functionality.")
@RestController
public class UserController extends BaseController {

    private final RegisterUser registerUser;
    private final LoginUser loginUser;
    private final AuthenticateUser authenticateUser;

    public UserController(RegisterUser registerUser, LoginUser loginUser, AuthenticateUser authenticateUser) {
        this.registerUser = registerUser;
        this.loginUser = loginUser;
        this.authenticateUser = authenticateUser;
    }

    @Operation(summary = "Authenticate user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "BAD REQUEST")})
    @PostMapping("auth/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticateUserInput authenticateUserInput) {

        AuthenticateUserInput input = AuthenticateUserInput.builder()
            .token(authenticateUserInput.getToken())
            .build();

        Either<ErrorWrapper, AuthenticateUserOutput> output = authenticateUser.process(input);
        return handleResult(output, HttpStatus.OK);
    }

    @Operation(summary = "Login user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "BAD REQUEST")})
    @PostMapping("auth/login")
    public ResponseEntity<?> login(@RequestBody LoginUserInput loginUserInput) {

        LoginUserInput input = LoginUserInput.builder()
            .username(loginUserInput.getUsername())
            .password(loginUserInput.getPassword())
            .build();

        Either<ErrorWrapper, LoginUserOutput> output = loginUser.process(input);
        return handleResult(output, HttpStatus.OK);
    }

    @Operation(summary = "Register a user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "CREATED"),
        @ApiResponse(responseCode = "400", description = "BAD REQUEST")})
    @PostMapping("auth/register")
    public ResponseEntity<?> register(@RequestBody RegisterUserInput registerUserInput) {

        RegisterUserInput input = RegisterUserInput.builder()
            .username(registerUserInput.getUsername())
            .firstName(registerUserInput.getFirstName())
            .lastName(registerUserInput.getLastName())
            .phoneNo(registerUserInput.getPhoneNo())
            .password(registerUserInput.getPassword())
            .email(registerUserInput.getEmail())
            .birthDate(registerUserInput.getBirthDate())
            .build();

        Either<ErrorWrapper, RegisterUserOutput> output = registerUser.process(input);
        return handleResult(output, HttpStatus.CREATED);
    }
}
