package com.uas.rentcar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- HALAMAN LOGIN ---
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // --- HALAMAN REGISTER ---
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // --- PROSES SIMPAN USER BARU ---
    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute("user") User user) {
        // 1. Enkripsi Password
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        
        // 2. Default Role = USER
        user.setRole("USER");
        
        // 3. Simpan
        userRepository.save(user);
        
        return "redirect:/login?success";
    }

    // ==================================================================
    // FITUR KHUSUS: UBAH JADI ADMIN (BACKDOOR)
    // Gunakan ini karena edit manual di database Railway merusak password
    // ==================================================================
    @GetMapping("/makeAdmin")
    @ResponseBody // Agar return-nya teks biasa, bukan file HTML
    public String makeAdmin(@RequestParam("email") String email) {
        // Cari user berdasarkan email
        User user = userRepository.findByEmail(email);
        
        if (user != null) {
            // Ubah role jadi ADMIN
            user.setRole("ADMIN");
            // Simpan ulang (Password aman tidak berubah)
            userRepository.save(user);
            return "SUKSES! Akun " + email + " sekarang sudah menjadi ADMIN. Silakan Logout dan Login kembali.";
        } else {
            return "GAGAL: Email " + email + " tidak ditemukan di database.";
        }
    }
}