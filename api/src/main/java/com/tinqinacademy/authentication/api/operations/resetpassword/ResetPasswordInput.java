package com.tinqinacademy.authentication.api.operations.resetpassword;

import com.tinqinacademy.authentication.api.base.OperationInput;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class ResetPasswordInput implements OperationInput {

    @NotEmpty(message = "Recovery code should not be empty.")
    private String recoveryCode;

    @NotNull(message = "New password should not be null.")
    @Size(min = 2,message = "Enter a valid password with min 3 symbols.")
    private String newPassword;
}
