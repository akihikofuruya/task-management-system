package com.example.taskmanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PageController {

    @GetMapping({"/", "/login"})
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String register() {
        return "auth/register";
    }

    @GetMapping("/tasks")
    public String tasks() {
        return "tasks/list";
    }

    @GetMapping("/tasks/new")
    public String newTask() {
        return "tasks/form";
    }

    @GetMapping("/tasks/{id}")
    public String taskDetail(@PathVariable("id") Long taskId, Model model) {
        model.addAttribute("taskId", taskId);
        return "tasks/detail";
    }

    @GetMapping("/tasks/{id}/edit")
    public String editTask(@PathVariable("id") Long taskId, Model model) {
        model.addAttribute("taskId", taskId);
        return "tasks/form";
    }
}
