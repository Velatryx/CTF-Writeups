package com.ironhold.controller;

import com.ironhold.model.Inmate;
import com.ironhold.repository.InmateRepository;
import com.ironhold.repository.MovementRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class InmateController {

    private final InmateRepository inmateRepository;
    private final MovementRepository movementRepository;
    private final JdbcTemplate jdbcTemplate;

    public InmateController(InmateRepository inmateRepository,
                             MovementRepository movementRepository,
                             @Qualifier("searchJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.inmateRepository = inmateRepository;
        this.movementRepository = movementRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/inmates")
    public String list(Model model) {
        model.addAttribute("inmates", inmateRepository.findAll());
        return "inmates";
    }

    @GetMapping("/inmates/search")
    public String search(@RequestParam(required = false) String q, Model model) {
        List<Map<String, Object>> results;
        if (q == null || q.isBlank()) {
            results = jdbcTemplate.queryForList("SELECT id, name, block FROM inmates");
        } else {
            String sql = "SELECT id, name, block FROM inmates WHERE name = '" + q + "'";
            results = jdbcTemplate.queryForList(sql);
        }
        model.addAttribute("results", results);
        model.addAttribute("query", q == null ? "" : q);
        return "inmate-search";
    }

    @GetMapping("/inmates/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Inmate inmate = inmateRepository.findById(id).orElse(null);
        model.addAttribute("inmate", inmate);
        return "inmate-detail";
    }

    @GetMapping("/inmates/{id}/movements")
    public String movements(@PathVariable Long id, Model model) {
        Inmate inmate = inmateRepository.findById(id).orElse(null);
        model.addAttribute("inmate", inmate);
        model.addAttribute("movements", movementRepository.findByInmateId(id));
        return "inmate-movements";
    }
}
