package com.karyawan.karyawan.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "karyawan")
public class Karyawan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private String nama;
    private String departemen;
    private BigDecimal gaji;
    private String noTelepon;
    private String fotoUrl;

    // Properti baru
    private String username;

    public Karyawan() {
    }

    public Karyawan(String nama, String departemen, double gaji, String username) {
        this.nama = nama;
        this.departemen = departemen;
        this.gaji = BigDecimal.valueOf(gaji);
        this.username = username;
    }

    // Getters
    public int getId() { return id; }
    public String getNama() { return nama; }
    public String getDepartemen() { return departemen; }
    public BigDecimal getGaji() { return gaji; }
    public String getUsername() { return username; }
    public String getNoTelepon() { return noTelepon; }
    public String getFotoUrl() { return fotoUrl; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNama(String nama) { this.nama = nama; }
    public void setDepartemen(String departemen) { this.departemen = departemen; }
    public void setGaji(BigDecimal gaji) { this.gaji = gaji; }
    public void setNoTelepon(String noTelepon) { this.noTelepon = noTelepon; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
    
    
    public void setUsername(String username) { this.username = username; }



    @Column(name = "kuota_cuti", nullable = false)
    private Integer kuotaCuti = 12;

    // Getter dan Setter
    public Integer getKuotaCuti() {
        return kuotaCuti;
    }

    public void setKuotaCuti(Integer kuotaCuti) {
        this.kuotaCuti = kuotaCuti;
    }
}