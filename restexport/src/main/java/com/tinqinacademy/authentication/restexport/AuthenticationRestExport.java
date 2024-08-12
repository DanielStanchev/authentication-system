package com.tinqinacademy.authentication.restexport;

import com.tinqinacademy.authentication.api.operations.authenticateuser.AuthenticateUserInput;
import com.tinqinacademy.authentication.api.operations.authenticateuser.AuthenticateUserOutput;
import com.tinqinacademy.authentication.api.operations.checkuserage.CheckUserAgeOutput;
import com.tinqinacademy.authentication.api.operations.loginuser.LoginUserInput;
import com.tinqinacademy.authentication.api.operations.loginuser.LoginUserOutput;
import com.tinqinacademy.authentication.api.operations.registeruser.RegisterUserInput;
import com.tinqinacademy.authentication.api.operations.registeruser.RegisterUserOutput;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "authentication")
public interface AuthenticationRestExport {

    @PostMapping("auth/authenticate")
    AuthenticateUserOutput authenticate(@RequestBody AuthenticateUserInput authenticateUserInput);

    @PostMapping("auth/login")
    LoginUserOutput login(@RequestBody LoginUserInput loginUserInput);

    @PostMapping("auth/register")
    RegisterUserOutput register(@RequestBody RegisterUserInput registerUserInput);

    @GetMapping("auth/check/{userId}")
    CheckUserAgeOutput checkUserAge(@PathVariable("userId") String userId);
}
