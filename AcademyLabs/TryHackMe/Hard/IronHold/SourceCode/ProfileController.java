package com.ironhold.controller;

import com.ironhold.model.Staff;
import com.ironhold.repository.StaffRepository;
import com.ironhold.security.SessionUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;

@Controller
public class ProfileController {

    private final StaffRepository staffRepository;

    public ProfileController(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        Staff staff = staffRepository.findByUsername(SessionUtil.currentUsername(session));
        model.addAttribute("staff", staff);
        return "profile";
    }

    @PostMapping("/profile/update")
    public String update(@ModelAttribute Staff staff, HttpSession session) {
        Staff current = staffRepository.findByUsername(SessionUtil.currentUsername(session));

        current.setFullName(staff.getFullName());
        current.setEmail(staff.getEmail());
        if (staff.getBadgeNumber() != null && !staff.getBadgeNumber().isBlank()) {
            current.setBadgeNumber(staff.getBadgeNumber());
        }
        if (staff.getRole() != null && !staff.getRole().isBlank()) {
            current.setRole(staff.getRole());
        }

        staffRepository.save(current);
        return "redirect:/profile";
    }
}
