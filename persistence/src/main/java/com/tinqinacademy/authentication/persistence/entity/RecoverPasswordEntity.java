package com.tinqinacademy.authentication.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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

@Entity
@Table(name = "recover_password")
public class RecoverPasswordEntity extends BaseEntity {

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "recovery_code", nullable = false, unique = true)
    private String recoveryCode;
}
