package com.uas.rentcar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Menampilkan Halaman Login
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Mengarah ke file login.html
    }

    // Menampilkan Halaman Register
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register"; // Mengarah ke file register.html
    }

    // Proses Simpan User Baru
    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute("user") User user) {
        // 1. Enkripsi Password User (Agar aman di database)
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        
        // 2. Set Role Default sebagai USER (Pelanggan)
        user.setRole("USER");
        
        // 3. Simpan ke Database
        userRepository.save(user);
        
        // 4. Redirect ke Login dengan pesan sukses
        return "redirect:/login?success";
    }
}