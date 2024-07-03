package com.flywire.exercise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;

import java.util.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;

@RestController
@RequestMapping("/employees")
public class EmployeeController {
    @Autowired
    private ResourceLoader resourceLoader;

    private List<Employee> employees;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    @PostConstruct
    public void init() {
        Resource resource = resourceLoader.getResource("classpath:json/data.json");
        try {
            ObjectMapper mapper = new ObjectMapper();
            employees = new ArrayList<>(Arrays.asList(mapper.readValue(resource.getInputStream(), Employee[].class)));
        } catch (Exception e) {
            employees = new ArrayList<>();
            e.printStackTrace();
        }
    }

    @GetMapping("/active")
    public List<Employee> getActiveEmployees() {
        return employees.stream()
                .filter(Employee::isActive)
                .sorted(Comparator.comparing(Employee::getLastName))
                .toList();
    }

    @GetMapping("/{id}")
    public Employee getEmployeeById(@PathVariable int id) {
        Employee employee = employees.stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElse(null);
        if (employee != null) {
            employee.setDirectHires(employees.stream()
                    .filter(e -> employee.getDirectReports().contains(e.getId()))
                    .toList());
        }
        return employee;
    }

    @GetMapping("/hired")
    public List<Employee> getEmployeesByHireDate(@RequestParam String start, @RequestParam String end) {
        Date startDate = Employee.parseDate(start);
        Date endDate = Employee.parseDate(end);

        return employees.stream()
                .filter(Employee::isActive)
                .filter(e -> {
                    Date hireDate = Employee.parseDate(e.getHireDate());
                    return hireDate != null && !hireDate.before(startDate) && !hireDate.after(endDate);
                })
                .sorted((e1, e2) -> {
                    Date hireDate1 = Employee.parseDate(e1.getHireDate());
                    Date hireDate2 = Employee.parseDate(e2.getHireDate());
                    return hireDate2.compareTo(hireDate1);
                })
                .toList();
    }

    @PostMapping("/add")
    public Employee addEmployee(@RequestBody Employee employee) {
        if (employees.stream().anyMatch(e -> e.getId() == employee.getId())) {
            throw new DuplicateEmployeeException("Employee ID already exists");
        }
        Employee manager = employees.stream()
                .filter(e -> e.getId() == employee.getManager())
                .findFirst()
                .orElse(null);
        if (manager != null) {
            manager.getDirectReports().add(employee.getId());
        }
        employees.add(employee);
        saveEmployees();
        return employee;
    }

    @PutMapping("/deactivate/{id}")
    public Employee deactivateEmployee(@PathVariable int id) {
        Employee employee = employees.stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElse(null);

        if (employee != null) {
            employee.setActive(false);
            saveEmployees();
        }
        return employee;
    }

    private void saveEmployees() {
        try {
            Resource resource = resourceLoader.getResource("classpath:json/data.json");
            Path path = Paths.get(resource.getURI());
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(path.toFile(), employees);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
