package com.uas.rentcar;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Data
@Entity
@Table(name = "mobil")
public class Mobil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String merk;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String platNomor;

    @Column(nullable = false)
    private Double hargaPerHari;

    private String status;
    
    @Column(nullable = true, length = 255)
    private String imageUrl;

    @Transient
    public String getPhotosImagePath() {
        if (imageUrl == null || id == null) return "/images/default-car.png"; // Placeholder jika kosong
        // Support gambar URL (http) atau gambar Upload Lokal
        if(imageUrl.startsWith("http")) return imageUrl;
        return "/photos/" + id + "/" + imageUrl;
    }
}