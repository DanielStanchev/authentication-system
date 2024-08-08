package com.tinqinacademy.authentication.core.config;

import com.tinqinacademy.authentication.persistence.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class JwtTokenInfo {
    private UUID id;
    private Role role;
}
