package com.example.demo.service;

import com.example.demo.model.Student;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final Path csvPath = Paths.get("students.csv");

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        if (!Files.exists(csvPath)) {
            return students;
        }
        try (BufferedReader br = Files.newBufferedReader(csvPath)) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first && line.toLowerCase().contains("roll")) {
                    first = false;
                    continue;
                }
                first = false;
                String[] parts = line.split(",");
                Student s = new Student();
                if (parts.length > 0) s.setName(parts[0].trim());
                if (parts.length > 1) s.setRollNo(parts[1].trim());
                if (parts.length > 2) s.setDepartment(parts[2].trim());
                if (parts.length > 3) s.setEmail(parts[3].trim());
                if (parts.length > 4) s.setPhone(parts[4].trim());
                students.add(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return students;
    }

    public void saveStudent(Student student) {
        boolean exists = Files.exists(csvPath);
        try (BufferedWriter bw = Files.newBufferedWriter(csvPath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            if (!exists) {
                bw.write("name,rollNo,department,email,phone");
                bw.newLine();
            }
            String line = String.join(",", safe(student.getName()), safe(student.getRollNo()),
                    safe(student.getDepartment()), safe(student.getEmail()), safe(student.getPhone()));
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveAll(List<Student> students) {
        try (Writer writer = Files.newBufferedWriter(csvPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            StatefulBeanToCsv<Student> beanToCsv = new StatefulBeanToCsvBuilder<Student>(writer).build();
            beanToCsv.write(students);
        } catch (Exception e) {
            try (BufferedWriter bw = Files.newBufferedWriter(csvPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                bw.write("name,rollNo,department,email,phone");
                bw.newLine();
                for (Student s : students) {
                    bw.write(String.join(",", safe(s.getName()), safe(s.getRollNo()),
                            safe(s.getDepartment()), safe(s.getEmail()), safe(s.getPhone())));
                    bw.newLine();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void deleteByRollNo(String rollNo) {
        List<Student> students = getAllStudents();
        List<Student> remaining = students.stream()
                .filter(s -> s.getRollNo() == null || !s.getRollNo().equalsIgnoreCase(rollNo))
                .collect(Collectors.toList());
        saveAll(remaining);
    }

    private String safe(String s) {
        return s == null ? "" : s.replaceAll(",", " ");
    }
}
