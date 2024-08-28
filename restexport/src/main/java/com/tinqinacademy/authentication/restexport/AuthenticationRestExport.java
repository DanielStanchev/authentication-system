package com.tinqinacademy.authentication.restexport;

import com.tinqinacademy.authentication.api.operations.authenticateuser.AuthenticateUserInput;
import com.tinqinacademy.authentication.api.operations.authenticateuser.AuthenticateUserOutput;
import com.tinqinacademy.authentication.api.restapiroutes.RestApiRoutes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "authentication")
public interface AuthenticationRestExport {

    @PostMapping(RestApiRoutes.AUTH_AUTHENTICATE)
    AuthenticateUserOutput authenticate(@RequestBody AuthenticateUserInput authenticateUserInput);
}
