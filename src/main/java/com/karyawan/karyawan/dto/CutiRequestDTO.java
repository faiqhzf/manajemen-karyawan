package com.karyawan.karyawan.dto;
import jakarta.validation.constraints.NotBlank;

public class CutiRequestDTO {
    @NotBlank(message = "Tanggal mulai harus diisi")
    private String tanggalMulai;

    @NotBlank(message = "Tanggal selesai harus diisi")
    private String tanggalSelesai;

    @NotBlank(message = "Alasan cuti tidak boleh kosong")
    private String alasan;


    public String getTanggalMulai() { return tanggalMulai; }
    public void setTanggalMulai(String tanggalMulai) { this.tanggalMulai = tanggalMulai; }
    public String getTanggalSelesai() { return tanggalSelesai; }
    public void setTanggalSelesai(String tanggalSelesai) { this.tanggalSelesai = tanggalSelesai; }
    public String getAlasan() { return alasan; }
    public void setAlasan(String alasan) { this.alasan = alasan; }

}