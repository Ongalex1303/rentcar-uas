package com.uas.rentcar;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MobilController {

    @Autowired
    private MobilRepository mobilRepository;

    // --- HALAMAN PUBLIK (CUSTOMER) ---
    @GetMapping("/")
    public String landingPage(Model model, @RequestParam(value = "keyword", required = false) String keyword) {
        List<Mobil> listMobil;
        if (keyword != null) {
            listMobil = mobilRepository.search(keyword); // Fitur Search
        } else {
            listMobil = mobilRepository.findAll();
        }
        model.addAttribute("listMobil", listMobil);
        model.addAttribute("keyword", keyword);
        return "landing_page";
    }

    // --- HALAMAN ADMIN ---
    @GetMapping("/admin")
    public String adminDashboard(Model model, @RequestParam(value = "keyword", required = false) String keyword) {
        List<Mobil> listMobil;
        if (keyword != null) {
            listMobil = mobilRepository.search(keyword);
        } else {
            listMobil = mobilRepository.findAll();
        }
        model.addAttribute("listMobil", listMobil);
        model.addAttribute("keyword", keyword);
        return "admin_dashboard";
    }

    @GetMapping("/admin/add")
    public String showAddForm(Model model) {
        model.addAttribute("mobil", new Mobil());
        model.addAttribute("pageTitle", "Tambah Armada Baru");
        return "form_mobil";
    }

    @GetMapping("/admin/edit/{id}")
    public String showEditForm(@PathVariable(value = "id") Long id, Model model, RedirectAttributes ra) {
        try {
            Mobil mobil = mobilRepository.findById(id).get();
            model.addAttribute("mobil", mobil);
            model.addAttribute("pageTitle", "Edit Data Mobil");
            return "form_mobil";
        } catch (Exception e) {
            ra.addFlashAttribute("message", "Mobil tidak ditemukan.");
            return "redirect:/admin";
        }
    }

    @PostMapping("/admin/save")
    public String saveMobil(@ModelAttribute("mobil") Mobil mobil,
                            @RequestParam("imageFile") MultipartFile multipartFile,
                            RedirectAttributes ra) throws IOException {
        
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

        if (!fileName.isEmpty()) {
            mobil.setImageUrl(fileName);
            Mobil savedMobil = mobilRepository.save(mobil);
            String uploadDir = "user-photos/" + savedMobil.getId();
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            if (mobil.getId() != null) {
                // Pertahankan gambar lama jika tidak upload baru
                Mobil existing = mobilRepository.findById(mobil.getId()).orElse(null);
                if (existing != null) mobil.setImageUrl(existing.getImageUrl());
            }
            mobilRepository.save(mobil);
        }
        
        ra.addFlashAttribute("message", "Data mobil berhasil disimpan!"); // Notifikasi Sukses
        return "redirect:/admin";
    }

    @GetMapping("/admin/delete/{id}")
    public String deleteMobil(@PathVariable(value = "id") Long id, RedirectAttributes ra) {
        mobilRepository.deleteById(id);
        ra.addFlashAttribute("message", "Mobil berhasil dihapus.");
        return "redirect:/admin";
    }
}