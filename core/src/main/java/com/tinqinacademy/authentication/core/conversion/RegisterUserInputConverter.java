package com.tinqinacademy.authentication.core.conversion;

import com.tinqinacademy.authentication.api.operations.registeruser.RegisterUserInput;
import com.tinqinacademy.authentication.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class RegisterUserInputConverter extends BaseConverter<RegisterUserInput, UserEntity.UserEntityBuilder> {
    @Override
    public UserEntity.UserEntityBuilder convertObject(RegisterUserInput input){

        return UserEntity.builder()
            .birthDate(input.getBirthDate())
            .email(input.getEmail())
            .firstName(input.getFirstName())
            .lastName(input.getLastName())
            .password(input.getPassword())
            .phoneNo(input.getPhoneNo())
            .isAccountActivated(false)
            .username(input.getUsername());
    }
}
