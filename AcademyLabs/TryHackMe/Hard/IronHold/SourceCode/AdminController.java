package com.ironhold.controller;

import com.ironhold.repository.AdminNoticeRepository;
import com.ironhold.repository.InmateRepository;
import com.ironhold.repository.StaffRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.InputStream;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final StaffRepository staffRepository;
    private final AdminNoticeRepository adminNoticeRepository;
    private final InmateRepository inmateRepository;

    public AdminController(StaffRepository staffRepository,
                            AdminNoticeRepository adminNoticeRepository,
                            InmateRepository inmateRepository) {
        this.staffRepository = staffRepository;
        this.adminNoticeRepository = adminNoticeRepository;
        this.inmateRepository = inmateRepository;
    }

    @GetMapping("/staff")
    public String staff(Model model) {
        model.addAttribute("staff", staffRepository.findAll());
        return "admin-staff";
    }

    @GetMapping("/staff/{id}")
    public String staffDetail(@PathVariable Long id, Model model) {
        model.addAttribute("member", staffRepository.findById(id).orElse(null));
        return "admin-staff-detail";
    }

    @GetMapping("/control")
    public String control(Model model) {
        model.addAttribute("records", adminNoticeRepository.findAll());
        model.addAttribute("blockCount", inmateRepository.findAll().stream()
                .map(i -> i.getBlock())
                .distinct()
                .count());
        return "admin-control";
    }

    @GetMapping("/roster")
    public String roster(Model model) {
        model.addAttribute("staff", staffRepository.findAll());
        return "admin-roster";
    }

    @GetMapping("/settings")
    public String settings() {
        return "admin-settings";
    }

    @GetMapping("/settings/diagnostics")
    public String diagnostics(Model model) {
        String output;
        try {
            Process process = new ProcessBuilder("df", "-h", "/opt/ironhold")
                    .redirectErrorStream(true)
                    .start();
            try (InputStream is = process.getInputStream()) {
                output = new String(is.readAllBytes());
            }
            process.waitFor();
        } catch (Exception e) {
            output = "Diagnostics unavailable: " + e.getMessage();
        }
        model.addAttribute("output", output);
        return "admin-diagnostics";
    }
}
