package com.se2.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

  @GetMapping("/")
  public String home(Model model) {
    model.addAttribute("message", "Chào mừng đến với BroSport!");
    return "index";
  }

  @GetMapping("/api/ping")
  @ResponseBody
  public String ping() {
    return "Pong! Server is running...";
  }
}