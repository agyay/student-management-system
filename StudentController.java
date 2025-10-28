package com.example.demo.controller;

import com.example.demo.model.Student;
import com.example.demo.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping({"/", "/index"})
    public String viewHomePage(Model model) {
        List<Student> listStudents = studentService.getAllStudents();
        model.addAttribute("listStudents", listStudents);
        return "index";
    }

    @GetMapping("/showNewStudentForm")
    public String showNewStudentForm(Model model) {
        Student student = new Student();
        model.addAttribute("student", student);
        return "new_student";
    }

    @PostMapping("/saveStudent")
    public String saveStudent(@ModelAttribute("student") Student student) {
        studentService.saveStudent(student);
        return "redirect:/";
    }

    // --- DELETE endpoint ---
    @PostMapping("/delete")
    public String deleteStudent(@RequestParam("rollNo") String rollNo) {
        studentService.deleteByRollNo(rollNo);
        return "redirect:/";
    }
}
