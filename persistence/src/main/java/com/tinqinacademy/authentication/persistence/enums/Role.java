package com.tinqinacademy.authentication.persistence.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum Role {

    ADMIN("admin"),
    USER("user"),
    UNKNOWN("");

    private final String code;

    Role(String code) {
        this.code = code;
    }

    @JsonCreator
    public static Role getByCode(String code) {
        if (code == null) {
            return null;
        }
        return Arrays.stream(Role.values())
            .filter(role -> role.toString().equals(code))
            .findFirst()
            .orElse(Role.UNKNOWN);
    }

    @JsonValue
    @Override
    public String toString() {
        return this.code;
    }
}
