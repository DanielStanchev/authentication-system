package com.tinqinacademy.authentication.api.operations.authenticateuser;

import com.tinqinacademy.authentication.api.base.OperationInput;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AuthenticateUserInput implements OperationInput {
    private String token;
}
