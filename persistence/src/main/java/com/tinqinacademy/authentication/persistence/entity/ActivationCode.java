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
@Table(name = "activation_code")
public class ActivationCode extends BaseEntity{

    @Column(name = "user_email", nullable = false, unique = true)
    private String userEmail;

    @Column(name = "activation_code", nullable = false)
    private String activationCode;
}
