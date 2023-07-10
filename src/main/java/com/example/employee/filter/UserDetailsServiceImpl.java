package com.example.employee.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.example.employee.exception.UserNotFoundException;
import com.example.employee.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) { 

		return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("user not registered"));
	}

}
