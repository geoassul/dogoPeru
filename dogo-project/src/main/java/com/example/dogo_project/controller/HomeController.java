package com.example.dogo_project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Páginas del backend: login.html y el panel de administración en static/admin.
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "redirect:/login.html";
    }

    /** Panel de administración (static/admin/index.html). */
    @GetMapping("/admin")
    public String adminSinBarra() {
        return "redirect:/admin/";
    }

    @GetMapping("/admin/")
    public String admin() {
        return "forward:/admin/index.html";
    }
}
