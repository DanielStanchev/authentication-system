package com.tinqinacademy.authentication.rest.controllers;

import com.tinqinacademy.authentication.api.exceptionmodel.ErrorWrapper;
import com.tinqinacademy.authentication.api.operations.authenticateuser.AuthenticateUser;
import com.tinqinacademy.authentication.api.operations.authenticateuser.AuthenticateUserInput;
import com.tinqinacademy.authentication.api.operations.authenticateuser.AuthenticateUserOutput;
import com.tinqinacademy.authentication.api.operations.changepassword.ChangePassword;
import com.tinqinacademy.authentication.api.operations.changepassword.ChangePasswordInput;
import com.tinqinacademy.authentication.api.operations.changepassword.ChangePasswordOutput;
import com.tinqinacademy.authentication.api.operations.checkuserage.CheckUserAge;
import com.tinqinacademy.authentication.api.operations.checkuserage.CheckUserAgeInput;
import com.tinqinacademy.authentication.api.operations.checkuserage.CheckUserAgeOutput;
import com.tinqinacademy.authentication.api.operations.confirmregistration.ConfirmRegistration;
import com.tinqinacademy.authentication.api.operations.confirmregistration.ConfirmRegistrationInput;
import com.tinqinacademy.authentication.api.operations.confirmregistration.ConfirmRegistrationOutput;
import com.tinqinacademy.authentication.api.operations.demoteuser.DemoteUser;
import com.tinqinacademy.authentication.api.operations.demoteuser.DemoteUserInput;
import com.tinqinacademy.authentication.api.operations.demoteuser.DemoteUserOutput;
import com.tinqinacademy.authentication.api.operations.loginuser.LoginUser;
import com.tinqinacademy.authentication.api.operations.loginuser.LoginUserInput;
import com.tinqinacademy.authentication.api.operations.loginuser.LoginUserOutput;
import com.tinqinacademy.authentication.api.operations.logoutuser.LogoutUser;
import com.tinqinacademy.authentication.api.operations.logoutuser.LogoutUserInput;
import com.tinqinacademy.authentication.api.operations.logoutuser.LogoutUserOutput;
import com.tinqinacademy.authentication.api.operations.promoteuser.PromoteUser;
import com.tinqinacademy.authentication.api.operations.promoteuser.PromoteUserInput;
import com.tinqinacademy.authentication.api.operations.promoteuser.PromoteUserOutput;
import com.tinqinacademy.authentication.api.operations.recoverpassword.RecoverPassword;
import com.tinqinacademy.authentication.api.operations.recoverpassword.RecoverPasswordInput;
import com.tinqinacademy.authentication.api.operations.recoverpassword.RecoverPasswordOutput;
import com.tinqinacademy.authentication.api.operations.registeruser.RegisterUser;
import com.tinqinacademy.authentication.api.operations.registeruser.RegisterUserInput;
import com.tinqinacademy.authentication.api.operations.registeruser.RegisterUserOutput;
import com.tinqinacademy.authentication.api.operations.resetpassword.ResetPassword;
import com.tinqinacademy.authentication.api.operations.resetpassword.ResetPasswordInput;
import com.tinqinacademy.authentication.api.operations.resetpassword.ResetPasswordOutput;
import com.tinqinacademy.authentication.api.restapiroutes.RestApiRoutes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vavr.control.Either;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User API", description = "User related functionality.")
@RestController
public class UserController extends BaseController {

    private final RegisterUser registerUser;
    private final LoginUser loginUser;
    private final AuthenticateUser authenticateUser;
    private final CheckUserAge checkUserAge;
    private final ChangePassword changePassword;
    private final PromoteUser promoteUser;
    private final DemoteUser demoteUser;
    private final LogoutUser logoutUser;
    private final ConfirmRegistration confirmRegistration;
    private final RecoverPassword recoverPassword;
    private final ResetPassword resetPassword;

    public UserController(RegisterUser registerUser, LoginUser loginUser, AuthenticateUser authenticateUser, CheckUserAge checkUserAge,
                          ChangePassword changePassword, PromoteUser promoteUser, DemoteUser demoteUser, LogoutUser logoutUser,
                          ConfirmRegistration confirmRegistration, RecoverPassword recoverPassword, ResetPassword resetPassword) {
        this.registerUser = registerUser;
        this.loginUser = loginUser;
        this.authenticateUser = authenticateUser;
        this.checkUserAge = checkUserAge;
        this.changePassword = changePassword;
        this.promoteUser = promoteUser;
        this.demoteUser = demoteUser;
        this.logoutUser = logoutUser;
        this.confirmRegistration = confirmRegistration;
        this.recoverPassword = recoverPassword;
        this.resetPassword = resetPassword;
    }

    @Operation(summary = "Get user age.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ok"),
        @ApiResponse(responseCode = "404", description = "Not found"),
        @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @GetMapping(RestApiRoutes.AUTH_CHECK_USER_AGE)
    public ResponseEntity<?> checkUserAge(@PathVariable("userId") String userId){

        CheckUserAgeInput input = CheckUserAgeInput.builder()
            .userId(userId)
            .build();

        Either<ErrorWrapper, CheckUserAgeOutput> output = checkUserAge.process(input);
        return handleResult(output, HttpStatus.OK);
    }

    @Operation(summary = "Authenticate user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ok"),
        @ApiResponse(responseCode = "404", description = "Not found"),
        @ApiResponse(responseCode = "400", description = "Bad request")})
    @PostMapping(RestApiRoutes.AUTH_AUTHENTICATE)
    public ResponseEntity<?> authenticate(@RequestBody AuthenticateUserInput authenticateUserInput) {

        AuthenticateUserInput input = AuthenticateUserInput.builder()
            .token(authenticateUserInput.getToken())
            .build();

        Either<ErrorWrapper, AuthenticateUserOutput> output = authenticateUser.process(input);
        return handleResult(output, HttpStatus.OK);
    }

    @Operation(summary = "Login user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ok"),
        @ApiResponse(responseCode = "404", description = "Not found"),
        @ApiResponse(responseCode = "400", description = "Bad request")})
    @PostMapping(RestApiRoutes.AUTH_LOGIN)
    public ResponseEntity<?> login(@RequestBody LoginUserInput loginUserInput) {

        LoginUserInput input = LoginUserInput.builder()
            .username(loginUserInput.getUsername())
            .password(loginUserInput.getPassword())
            .build();

        Either<ErrorWrapper, LoginUserOutput> output = loginUser.process(input);

        HttpHeaders headers;

        if (output.isLeft()) {
            return new ResponseEntity<>(output.getLeft(), output.getLeft()
                .getErrorResponseInfoList()
                .getFirst()
                .getHttpStatus());
        }

        headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + output.get().getToken());
        return new ResponseEntity<>(output.get(), headers, HttpStatus.OK);
    }

    @Operation(summary = "Register a user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ok"),
        @ApiResponse(responseCode = "404", description = "Not found"),
        @ApiResponse(responseCode = "400", description = "Bad request")})
    @PostMapping(RestApiRoutes.AUTH_REGISTER)
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

    @Operation(summary = "User change password.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ok"),
        @ApiResponse(responseCode = "404", description = "Not found"),
        @ApiResponse(responseCode = "400", description = "Bad request")})
    @PostMapping(RestApiRoutes.AUTH_CHANGE_PASSWORD)
    public ResponseEntity<?> changePassword(@RequestHeader(HttpHeaders.AUTHORIZATION) String header,
                                            @RequestBody ChangePasswordInput changePasswordInput){

        String token = header.substring(7);

        ChangePasswordInput input = ChangePasswordInput.builder()
            .token(token)
            .oldPassword(changePasswordInput.getOldPassword())
            .newPassword(changePasswordInput.getNewPassword())
            .email(changePasswordInput.getEmail())
            .build();

        Either<ErrorWrapper, ChangePasswordOutput> output = changePassword.process(input);
        return handleResult(output,HttpStatus.OK);
    }

    @Operation(summary = "Admin promotes User.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ok"),
        @ApiResponse(responseCode = "404", description = "Not found"),
        @ApiResponse(responseCode = "400", description = "Bad request")})
    @PostMapping(RestApiRoutes.AUTH_PROMOTE)
    public ResponseEntity<?> promoteUser(@RequestBody PromoteUserInput promoteUserInput){

       PromoteUserInput input = PromoteUserInput.builder()
           .userId(promoteUserInput.getUserId())
           .build();

        Either<ErrorWrapper, PromoteUserOutput> output = promoteUser.process(input);
        return handleResult(output,HttpStatus.OK);
    }

    @Operation(summary = "Admin demotes Admin user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ok"),
        @ApiResponse(responseCode = "404", description = "Not found"),
        @ApiResponse(responseCode = "400", description = "Bad request")})
    @PostMapping(RestApiRoutes.AUTH_DEMOTE)
    public ResponseEntity<?> demoteUser(@RequestBody DemoteUserInput demoteUserInput){

        DemoteUserInput input = DemoteUserInput.builder()
            .userId(demoteUserInput.getUserId())
            .build();

        Either<ErrorWrapper, DemoteUserOutput> output = demoteUser.process(input);
        return handleResult(output,HttpStatus.OK);
    }

    @Operation(summary = "User logout.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ok"),
        @ApiResponse(responseCode = "404", description = "Not found"),
        @ApiResponse(responseCode = "400", description = "Bad request")})
    @PostMapping(RestApiRoutes.AUTH_LOGOUT)
    public ResponseEntity<?> logoutUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String header){

        String token = header.substring(7);

        LogoutUserInput input = LogoutUserInput.builder()
            .token(token)
            .build();

        Either<ErrorWrapper, LogoutUserOutput> output = logoutUser.process(input);
        return handleResult(output,HttpStatus.OK);
    }

    @Operation(summary = "User confirm registration.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ok"),
        @ApiResponse(responseCode = "404", description = "Not found"),
        @ApiResponse(responseCode = "400", description = "Bad request")})
    @PostMapping(RestApiRoutes.AUTH_CONFIRM_REGISTRATION)
    public ResponseEntity<?> confirmRegistration(@RequestBody ConfirmRegistrationInput confirmRegistrationInput){

        ConfirmRegistrationInput input = ConfirmRegistrationInput.builder()
            .confirmationCode(confirmRegistrationInput.getConfirmationCode())
            .build();

        Either<ErrorWrapper, ConfirmRegistrationOutput> output = confirmRegistration.process(input);
        return handleResult(output,HttpStatus.OK);
    }

    @Operation(summary = "Recover password.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "BAD REQUEST")})
    @PostMapping(RestApiRoutes.AUTH_RECOVER_PASSWORD)
    public ResponseEntity<?> recoverPassword(@RequestBody RecoverPasswordInput recoverPasswordInput){

        RecoverPasswordInput input = RecoverPasswordInput.builder()
            .email(recoverPasswordInput.getEmail())
            .build();

        Either<ErrorWrapper, RecoverPasswordOutput> output = recoverPassword.process(input);
        return handleResult(output,HttpStatus.OK);
    }

    @Operation(summary = "Reset password.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ok"),
        @ApiResponse(responseCode = "404", description = "Not found"),
        @ApiResponse(responseCode = "400", description = "Bad request")})
    @PostMapping(RestApiRoutes.AUTH_RESET_PASSWORD)
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordInput resetPasswordInput){

        ResetPasswordInput input = ResetPasswordInput.builder()
            .newPassword(resetPasswordInput.getNewPassword())
            .recoveryCode(resetPasswordInput.getRecoveryCode())
            .build();

        Either<ErrorWrapper, ResetPasswordOutput> output = resetPassword.process(input);
        return handleResult(output,HttpStatus.OK);
    }
}
