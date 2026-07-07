package com.karyawan.karyawan.security;

import com.karyawan.karyawan.model.Pengguna;
import com.karyawan.karyawan.repository.PenggunaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private PenggunaRepository penggunaRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Pengguna pengguna = penggunaRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User tidak ditemukan: " + username));

       
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + pengguna.getRole().name());

        return new User(pengguna.getUsername(), pengguna.getPassword(), Collections.singletonList(authority));
    }
}