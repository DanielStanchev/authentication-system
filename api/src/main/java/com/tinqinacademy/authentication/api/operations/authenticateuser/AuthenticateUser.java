package com.tinqinacademy.authentication.api.operations.authenticateuser;

import com.tinqinacademy.authentication.api.base.OperationProcessor;
import com.tinqinacademy.authentication.api.operations.loginuser.LoginUserInput;
import com.tinqinacademy.authentication.api.operations.loginuser.LoginUserOutput;

public interface AuthenticateUser extends OperationProcessor<AuthenticateUserOutput, AuthenticateUserInput> {}
