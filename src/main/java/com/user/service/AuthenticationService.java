package com.user.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.user.entity.User;
import com.user.repository.UserRepository;
import com.user.request.LoginRequest;
import com.user.request.RegisterRequest;
import com.user.response.AuthenticationResponse;
import com.user.role.Role;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	
	
	public AuthenticationResponse login(LoginRequest registerRequest) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(registerRequest.getEmail(), registerRequest.getPassword())
				);
		
		var user=userRepository.findByEmail(registerRequest.getEmail()).orElseThrow();
		
		var jwtToken=jwtService.generateToken(user);
		return AuthenticationResponse.builder()
				.token(jwtToken)
				.build();
	}

	public AuthenticationResponse register(RegisterRequest registerRequest) {
		var user=User.builder()
				.fullName(registerRequest.getFullname())
				.address(registerRequest.getAddress())
				.email(registerRequest.getEmail())
				.password(passwordEncoder.encode(registerRequest.getPassword()))
				.role(Role.USER)
				.build();
		
		userRepository.save(user);
		var jwtToken=jwtService.generateToken(user);
		return AuthenticationResponse.builder()
				.token(jwtToken)
				.build();
	}

}
