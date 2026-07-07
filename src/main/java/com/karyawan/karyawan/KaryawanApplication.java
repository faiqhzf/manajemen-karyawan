package com.karyawan.karyawan;

import com.karyawan.karyawan.model.Pengguna;
import com.karyawan.karyawan.model.Role;
import com.karyawan.karyawan.repository.PenggunaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class KaryawanApplication {

    public static void main(String[] args) {
        SpringApplication.run(KaryawanApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(PenggunaRepository penggunaRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (penggunaRepository.findByUsername("admin_hr").isEmpty()) {
                Pengguna admin = new Pengguna();
                admin.setUsername("admin_hr");

                admin.setPassword(passwordEncoder.encode("admin123")); 
                admin.setRole(Role.HRD);
                
                penggunaRepository.save(admin);
                System.out.println("AKUN ADMIN BERHASIL DIBUAT OTOMATIS!");
            }
        };
    }
}