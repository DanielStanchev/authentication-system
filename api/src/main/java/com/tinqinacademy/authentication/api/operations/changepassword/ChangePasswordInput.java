package com.tinqinacademy.authentication.api.operations.changepassword;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinqinacademy.authentication.api.base.OperationInput;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
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
public class ChangePasswordInput implements OperationInput {

    @JsonIgnore
    private String token;

    @NotEmpty(message = "Old password should not be empty.")
    @Size(min = 4, max = 32)
    private String oldPassword;

    @NotEmpty(message = "New password should not be empty.")
    @Size(min = 4, max = 32)
    private String newPassword;

    @NotEmpty(message = "Email should not be empty.")
    @Email(message = "Enter a valid email containing @.")
    private String email;
}
