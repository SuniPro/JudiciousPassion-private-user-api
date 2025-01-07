package com.suni.api.jpprivateuserapi.entity.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum RoleType {
    ADMIN, USER, GUEST;

    @JsonCreator
    public static RoleType fromString(String role) {
        for (RoleType type : RoleType.values()) {
            if (type.name().equalsIgnoreCase(role)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid RoleType: " + role);
    }

    @JsonValue
    public String toValue() {
        return name().toLowerCase(); // JSON으로 출력될 때 소문자로 변환
    }
}