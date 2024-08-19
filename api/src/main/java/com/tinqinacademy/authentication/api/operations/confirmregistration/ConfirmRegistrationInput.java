package com.tinqinacademy.authentication.api.operations.confirmregistration;

import com.tinqinacademy.authentication.api.base.OperationInput;
import jakarta.validation.constraints.NotEmpty;
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
public class ConfirmRegistrationInput implements OperationInput {

    @NotEmpty(message = "Confirmation code cannot be empty.")
    private String confirmationCode;
}
