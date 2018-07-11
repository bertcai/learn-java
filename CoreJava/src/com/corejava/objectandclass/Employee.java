package com.corejava.objectandclass;
/**
 * a sample Employee
 *
 * @author cc
 */

import java.time.LocalDate;

public class Employee {
    private String name;
    private double salary;
    private LocalDate hireDay;

    public Employee(String name, double salary, int year, int mmonth, int day) {
        this.name = name;
        this.salary = salary;
        this.hireDay = LocalDate.of(year, mmonth, day);
    }

    public String getName() {
        return name;
    }

    public double getSalary() {
        return salary;
    }

    public LocalDate getHireDay() {
        return hireDay;
    }

    public void raiseSalary(double byPercent) {
        double raise = salary * byPercent / 100;
        salary += raise;
    }

    //....
}
