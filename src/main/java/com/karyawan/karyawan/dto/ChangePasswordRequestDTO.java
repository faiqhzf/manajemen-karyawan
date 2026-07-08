package com.karyawan.karyawan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChangePasswordRequestDTO {
    @NotBlank(message = "Password lama wajib diisi")
    private String passwordLama;

    @NotBlank(message = "Password baru wajib diisi")
    @Size(min = 4, message = "Password baru minimal 4 karakter")
    private String passwordBaru;

    public String getPasswordLama() { return passwordLama; }
    public void setPasswordLama(String passwordLama) { this.passwordLama = passwordLama; }
    public String getPasswordBaru() { return passwordBaru; }
    public void setPasswordBaru(String passwordBaru) { this.passwordBaru = passwordBaru; }
}