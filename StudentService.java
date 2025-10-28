
package com.example.demo.service;

import com.example.demo.model.Student;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class StudentService {

    private static final String CSV_FILE = "students.csv";

    @PostConstruct
    public void init() {
        File file = new File(CSV_FILE);
        if (!file.exists()) {
            try (CSVWriter writer = new CSVWriter(new FileWriter(CSV_FILE))) {
                writer.writeNext(new String[]{"ID", "Name", "Roll No", "Department", "Email", "Phone"});
            } catch (IOException e) {
                // Handle exception
            }
        }
    }

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(CSV_FILE))) {
            String[] nextLine;
            reader.readNext(); // Skip header
            while ((nextLine = reader.readNext()) != null) {
                students.add(new Student(Long.parseLong(nextLine[0]), nextLine[1], nextLine[2], nextLine[3], nextLine[4], nextLine[5]));
            }
        } catch (IOException | CsvValidationException e) {
            // Handle exception
        }
        return students;
    }

    public void addStudent(Student student) {
        List<Student> students = getAllStudents();
        long newId = students.isEmpty() ? 1 : students.get(students.size() - 1).getId() + 1;
        student.setId(newId);
        try (CSVWriter writer = new CSVWriter(new FileWriter(CSV_FILE, true))) {
            writer.writeNext(new String[]{String.valueOf(student.getId()), student.getName(), student.getRollNo(), student.getDepartment(), student.getEmail(), student.getPhone()});
        } catch (IOException e) {
            // Handle exception
        }
    
    }
    public void saveAll(List<Student> students) {
    try (Writer writer = new FileWriter("students.csv")) {
        StatefulBeanToCsv<Student> beanToCsv = new StatefulBeanToCsvBuilder<Student>(writer).build();
        beanToCsv.write(students);
    } catch (Exception e) {
        e.printStackTrace();
        }
    }

}
