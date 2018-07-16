package com.corejava.objectandclass;
/**
 * a sample Employee
 *
 * @author cc
 */

import java.time.LocalDate;
import java.util.Random;

public class Employee {
    private static int nextId = 1;

    private String name = "";
    private double salary;
    private LocalDate hireDay;
    private int id;

    static {
        Random generator = new Random();
        nextId = generator.nextInt(10000);
        // 返回一个0 ~ 10000-1的数
    }

    {
        id = nextId;
        nextId++;
    }


    public Employee() {
    }

    public Employee(double salary) {
        this("Employee #" + nextId, salary);
    }

    public Employee(String name, double salary, int year, int month, int day) {
        this.name = name;
        this.salary = salary;
        this.hireDay = LocalDate.of(year, month, day);
    }

    public Employee(String name, double salary) {
        this.name = name;
        this.salary = salary;
        this.id = 0;
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

    public void setId() {
        id = nextId;
        nextId++;
    }

    public static int getNextId() {
        return nextId;
    }

    public static void main(String[] args) {
        Employee e = new Employee("Harry", 50000);
        System.out.println(e.getHireDay() + " " + e.getSalary());
    }

    public int getId() {
        return id;
    }

    //....
}
