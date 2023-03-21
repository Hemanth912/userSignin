package com.example.userSignin.Service.impl;

import com.springrolejwt.model.User;
import com.springrolejwt.model.UserDto;

import java.util.List;

public interface UserService {
    User save(UserDto user);
    List<User> findAll();
    User findOne(String username);
}
