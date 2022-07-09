package com.login.services;

import com.login.shared.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserDto> findAllUsers();

    Optional<UserDto> findUserById(String id);

    Optional<UserDto> findUserByUsername(String username);

    UserDto registerUser(UserDto userDto);

    UserDto updateUser(String id, UserDto userDto);

    void deleteUserById(String id);

    void deleteAllUsers();
}
