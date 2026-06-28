package com.karyawan.karyawan.dto;
import java.math.BigDecimal;

public class KaryawanRequestDTO {
    private String nama;
    private String departemen;
    private BigDecimal gaji;
    private String username;
    private String password;

    // --- Getter dan Setter Mutlak Diperlukan oleh Spring (Jackson) ---
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getDepartemen() { return departemen; }
    public void setDepartemen(String departemen) { this.departemen = departemen; }

    public BigDecimal getGaji() { return gaji; }
    public void setGaji(BigDecimal gaji) { this.gaji = gaji; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}