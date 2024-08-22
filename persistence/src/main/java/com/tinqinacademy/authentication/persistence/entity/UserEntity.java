package com.tinqinacademy.authentication.persistence.entity;

import com.tinqinacademy.authentication.persistence.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder(toBuilder = true)

@Entity
@Table(name = "users")
public class UserEntity extends BaseEntity {

    @Column(name = "username", nullable = false,unique = true)
    private String username;

    @Column(name = "password",nullable = false)
    private String password;

    @Column(name = "first_name",nullable = false)
    private String firstName;

    @Column(name = "last_name",nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false,unique = true)
    private String email;

    @Column(name = "phone_no",nullable = false,unique = true)
    private String phoneNo;

    @Column(name = "birth_date",nullable = false)
    private LocalDate birthDate;

    @Column(name = "is_account_activated", nullable = false)
    private Boolean isAccountActivated;

    @Enumerated(EnumType.STRING)
    @Column(name = "role",nullable = false)
    private Role role;
}
