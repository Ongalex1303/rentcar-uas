package com.uas.rentcar;

import java.security.Principal;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // PENTING: Untuk membaca session User Login
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class TransaksiController {

    @Autowired
    private PeminjamanRepository peminjamanRepository;

    @Autowired
    private MobilRepository mobilRepository;

    @Autowired
    private UserRepository userRepository;

    // --- USER: FORM SEWA ---
    @GetMapping("/sewa/{mobilId}")
    public String showSewaForm(@PathVariable(value = "mobilId") Long mobilId, Model model, Principal principal) {
        // Cek Login
        if (principal == null) return "redirect:/login";

        Mobil mobil = mobilRepository.findById(mobilId).orElse(null);
        // Cek Ketersediaan Mobil
        if (mobil == null || !mobil.getStatus().equals("Tersedia")) return "redirect:/";
        
        // Ambil data user dari database berdasarkan email login
        String emailUser = principal.getName(); 
        User currentUser = userRepository.findByEmail(emailUser);

        Peminjaman peminjaman = new Peminjaman();
        peminjaman.setMobil(mobil);
        
        // Auto-fill nama penyewa dari data akun
        if (currentUser != null) {
            peminjaman.setNamaPeminjam(currentUser.getName());
        }
        
        model.addAttribute("peminjaman", peminjaman);
        model.addAttribute("mobil", mobil);
        return "form_sewa";
    }

    // --- USER: PROSES SIMPAN SEWA ---
    @PostMapping("/sewa/save")
    public String savePeminjaman(@ModelAttribute("peminjaman") Peminjaman peminjaman, 
                                 RedirectAttributes ra, 
                                 Principal principal) {
        
        // Validasi Nama Peminjam (Security Check)
        if (principal != null) {
            String emailUser = principal.getName();
            User currentUser = userRepository.findByEmail(emailUser);
            peminjaman.setNamaPeminjam(currentUser.getName());
        }

        Mobil mobil = mobilRepository.findById(peminjaman.getMobil().getId()).orElse(null);
        
        // Hitung Durasi & Harga
        long days = ChronoUnit.DAYS.between(peminjaman.getTanggalPinjam(), peminjaman.getTanggalKembali());
        if (days < 1) days = 1;
        
        peminjaman.setTotalHarga(mobil.getHargaPerHari() * days);
        peminjaman.setMobil(mobil);
        peminjaman.setStatusTransaksi("Berjalan");

        // Simpan Transaksi & Update Status Mobil
        peminjamanRepository.save(peminjaman);
        mobil.setStatus("Disewa");
        mobilRepository.save(mobil);

        ra.addFlashAttribute("message", "Booking Berhasil! Silakan cek riwayat pesanan Anda.");
        return "redirect:/riwayat"; // Redirect ke halaman Riwayat
    }

    // --- USER: HALAMAN RIWAYAT SAYA (BARU) ---
    @GetMapping("/riwayat")
    public String riwayatSaya(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        
        String emailUser = principal.getName();
        User currentUser = userRepository.findByEmail(emailUser);
        
        if (currentUser != null) {
            // Cari data peminjaman milik user ini saja
            List<Peminjaman> riwayat = peminjamanRepository.findByNamaPeminjamOrderByIdDesc(currentUser.getName());
            model.addAttribute("listRiwayat", riwayat);
        }
        
        return "riwayat_user";
    }

    // --- ADMIN: LIST SEMUA TRANSAKSI ---
    @GetMapping("/admin/transaksi")
    public String listTransaksi(Model model) {
        model.addAttribute("listPeminjaman", peminjamanRepository.findAllByOrderByIdDesc());
        return "admin_transaksi";
    }

    // --- ADMIN: PROSES PENGEMBALIAN MOBIL ---
    @GetMapping("/admin/transaksi/kembali/{id}")
    public String kembalikanMobil(@PathVariable(value = "id") Long id, RedirectAttributes ra) {
        Peminjaman peminjaman = peminjamanRepository.findById(id).orElse(null);
        
        if (peminjaman != null && peminjaman.getStatusTransaksi().equals("Berjalan")) {
            peminjaman.setStatusTransaksi("Selesai");
            peminjamanRepository.save(peminjaman);

            Mobil mobil = peminjaman.getMobil();
            mobil.setStatus("Tersedia");
            mobilRepository.save(mobil);

            ra.addFlashAttribute("message", "Mobil berhasil dikembalikan. Status update: Tersedia.");
        }
        
        return "redirect:/admin/transaksi";
    }
}