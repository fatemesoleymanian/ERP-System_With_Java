package com.example.minierp.domain.user;

public enum Role {
    ADMIN,
    SALES,
    INVENTORY_MANAGER;

    public String asAuthority() {
        return "ROLE_" + this.name();
    }
}
