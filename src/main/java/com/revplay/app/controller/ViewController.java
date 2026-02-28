package com.revplay.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class ViewController {

    private void setNoCacheHeaders(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    @GetMapping("/")
    public String welcome() {
        return "welcome";
    }

    @GetMapping("/home")
    public String home(HttpServletResponse response) {
        setNoCacheHeaders(response);
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    // All other dashboard routes are removed for the progress demo.
    // They are redirected back to home.
    @GetMapping({ "/songs", "/albums", "/artists", "/playlists", "/podcasts",
            "/genres", "/favorites", "/history", "/profile",
            "/upload", "/my-songs", "/my-albums", "/my-podcasts", "/stats" })
    public String dashboardPages(HttpServletResponse response) {
        return "redirect:/home";
    }
}
