package com.revplay.app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class ViewController {

    private static final Logger logger = LoggerFactory.getLogger(ViewController.class);

    private void setNoCacheHeaders(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    @GetMapping("/")
    public String welcome() {
        logger.info("welcome method called");
        return "welcome";
    }

    @GetMapping("/home")
    public String home(HttpServletResponse response) {
        logger.info("home method called");
        setNoCacheHeaders(response);
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        logger.info("login method called");
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        logger.info("register method called");
        return "register";
    }

    // All dashboard routes serve the same index template.
    // JavaScript reads the URL path and renders the correct content.
    @GetMapping({ "/songs", "/albums", "/artists", "/playlists", "/podcasts",
            "/genres", "/favorites", "/history", "/profile",
            "/upload", "/my-songs", "/my-albums", "/my-podcasts", "/stats" })
    public String dashboardPages(HttpServletResponse response) {
        logger.info("dashboardPages method called");
        setNoCacheHeaders(response);
        return "index";
    }
}
