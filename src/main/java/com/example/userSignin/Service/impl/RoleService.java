package com.example.userSignin.Service.impl;

import com.springrolejwt.model.Role;

public interface RoleService {
    Role findByName(String name);
}

