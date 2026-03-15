package com.carlosedolv.emergy_api.controllers;

import com.carlosedolv.emergy_api.dtos.auth.AuthenticationRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth")
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;

    public AuthenticationController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthenticationRequestDTO dto) {
        var userNamePassword = new UsernamePasswordAuthenticationToken(dto.email(), dto.password());
        var auth = this.authenticationManager.authenticate(userNamePassword);
        return ResponseEntity.ok().build();
    }
}
