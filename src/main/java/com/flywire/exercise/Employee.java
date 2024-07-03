package com.flywire.exercise;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Employee {
    private int id;
    private String name;
    private String position;
    private boolean active;
    private List<Integer> directReports = new ArrayList<>();
    private List<Employee> directHires = new ArrayList<>();
    private int manager;
    private String hireDate;

    public int getId() {
        return id;
    }

    public int getManager() {
        return manager;
    }

    public void setManager(int manager) {
        this.manager = manager;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Integer> getDirectReports() {
        return directReports;
    }

    public void setDirectReports(List<Integer> directReports) {
        this.directReports = directReports;
    }

    public List<Employee> getDirectHires() {
        return directHires;
    }

    public void setDirectHires(List<Employee> directHires) {
        this.directHires = directHires;
    }

    public String getHireDate() {
        return hireDate;
    }

    public void setHireDate(String hireDate) {
        this.hireDate = hireDate;
    }

    public static Date parseDate(String dateStr) {
        try {
            return new SimpleDateFormat("MM/dd/yyyy").parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    public String getLastName() {
        return name.split(" ")[1];
    }
}
