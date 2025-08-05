package com.example.minierp.interfaces.rest.auth;

import com.example.minierp.domain.user.Role;

public record RegisterRequest(String username, String password, Role role) { }
