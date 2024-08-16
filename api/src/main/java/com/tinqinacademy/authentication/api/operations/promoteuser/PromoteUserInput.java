package com.tinqinacademy.authentication.api.operations.promoteuser;

import com.tinqinacademy.authentication.api.base.OperationInput;
import jakarta.validation.constraints.NotBlank;
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
public class PromoteUserInput implements OperationInput {

    @NotBlank(message = "User ID cannot be blank.")
    private String userId;
}
