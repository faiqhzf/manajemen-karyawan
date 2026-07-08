package com.karyawan.karyawan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UpdateProfilRequestDTO {
    @NotBlank(message = "Nama tidak boleh kosong")
    private String nama;

    @Pattern(regexp = "^$|^[0-9+\\-\\s]{8,15}$", message = "Format nomor telepon tidak valid")
    private String noTelepon;

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public String getNoTelepon() { return noTelepon; }
    public void setNoTelepon(String noTelepon) { this.noTelepon = noTelepon; }
}