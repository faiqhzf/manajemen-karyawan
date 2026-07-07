package com.karyawan.karyawan.dto;

import com.karyawan.karyawan.model.Role;

import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @NotBlank(message = "Username tidak boleh kosong")
    private String username;

    @NotBlank(message = "Password tidak boleh kosong")
    @Size(min = 4, message = "Password minimal 4 karakter")
    private String password;

    @NotBlank(message = "Role tidak boleh kosong")
    private Role role;



    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}