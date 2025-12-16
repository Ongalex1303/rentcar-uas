package com.uas.rentcar;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "peminjaman")
public class Peminjaman {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String namaPeminjam;
    private LocalDate tanggalPinjam;
    private LocalDate tanggalKembali;
    private Double totalHarga;
    
    // Status Transaksi: "Berjalan" atau "Selesai"
    private String statusTransaksi; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mobil_id", nullable = false)
    private Mobil mobil;
}