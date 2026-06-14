package com.karyawan.karyawan.security;

import com.karyawan.karyawan.model.Pengguna;
import com.karyawan.karyawan.repository.PenggunaRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final PenggunaRepository penggunaRepository;

    public CustomUserDetailsService(PenggunaRepository penggunaRepository) {
        this.penggunaRepository = penggunaRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Pengguna pengguna = penggunaRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Pengguna tidak ditemukan: " + username));

        return User.builder()
                .username(pengguna.getUsername())
                .password(pengguna.getPassword())
                .roles(pengguna.getRole()) // Akan dikonversi menjadi "ROLE_HRD" atau "ROLE_KARYAWAN"
                .build();
    }
}