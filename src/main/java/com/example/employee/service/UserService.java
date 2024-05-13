package com.example.employee.service;

import com.example.employee.dto.PasswordDTO;
import com.example.employee.dto.UserDTO;
import com.example.employee.entities.Users;

public interface UserService {

	String addUser(UserDTO userDto);

	Users getUserByEmail(String email);

	String changePassword(String email, PasswordDTO password);

	String deleteUser(long userId);

    void deleteUserByEmail(String email);
}
