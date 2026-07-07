package com.karyawan.karyawan.controller;

import com.karyawan.karyawan.dto.JwtResponse;
import com.karyawan.karyawan.dto.LoginRequest;
import com.karyawan.karyawan.dto.RegisterRequest;
import com.karyawan.karyawan.model.Pengguna;
import com.karyawan.karyawan.repository.PenggunaRepository;
import com.karyawan.karyawan.security.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final PenggunaRepository penggunaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager, 
                          PenggunaRepository penggunaRepository, 
                          PasswordEncoder passwordEncoder, 
                          JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.penggunaRepository = penggunaRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser( @Valid @RequestBody RegisterRequest request) {
        if (penggunaRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username sudah digunakan!"));
        }

        Pengguna pengguna = new Pengguna(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getRole() 
        );

        penggunaRepository.save(pengguna);
        return ResponseEntity.ok(Map.of("message", "Registrasi berhasil untuk user: " + request.getUsername()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        String jwt = jwtUtils.generateJwtToken(userDetails);
        
        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), role));
    }
}