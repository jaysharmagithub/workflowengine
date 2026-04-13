package com.techpulseIt.workflowengine.controller;


import com.techpulseIt.workflowengine.entity.User;
import com.techpulseIt.workflowengine.exception.UserAlreadyExistsException;
import com.techpulseIt.workflowengine.request.LoginReq;
import com.techpulseIt.workflowengine.response.JwtResponse;
import com.techpulseIt.workflowengine.security.jwt.JwtUtils;
import com.techpulseIt.workflowengine.security.user.AppUserDetails;
import com.techpulseIt.workflowengine.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final IUserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;


    @PostMapping("/register-user")
    public ResponseEntity<?> registerUser(@RequestBody User user){
        try {
            userService.registerUser(user);
            return ResponseEntity.ok("Registration successful!");
        }catch (UserAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginReq request){
        Authentication authentication = authenticationManager.
                authenticate(new UsernamePasswordAuthenticationToken
                        (request.email(), request.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtTokenForUser(authentication);
        AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority).toList();
        return ResponseEntity.ok(new JwtResponse(
                userDetails.getId(),
                userDetails.getEmail(),
                jwt,
                roles
        ));
    }

}