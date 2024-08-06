package com.tinqinacademy.authentication.api.operations.registeruser;

import com.tinqinacademy.authentication.api.base.OperationInput;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder

public class RegisterUserInput implements OperationInput {

    @NotNull
    @Size(min = 2, max = 20, message = "Username must be between 2 and 20 symbols.")
    private String username;

    @NotNull
    @Size(min = 1, max = 20,message = "First name must be between 1 and 20 symbols.")
    private String firstName;

    @NotNull
    @Size(min = 1, max = 20, message = "Last name must be between 1 and 20 symbols.")
    private String lastName;

    @NotNull
    @Size(min = 2, max = 15,message = "Phone number must be between 2 and 15 symbols.")
    private String phoneNo;

    @NotNull
    @Size(min = 2,message = "Enter a valid password with min 3 symbols.")
    private String password;

    @Email(message = "Invalid email address.")
    private String email;

    @Past(message = "Birth date must be in the past.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
}
