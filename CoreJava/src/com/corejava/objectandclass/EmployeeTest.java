package com.corejava.objectandclass;

import java.util.ArrayList;

/**
 * Test Employee
 *
 * @author cc
 */
public class EmployeeTest {
    public static void main(String[] args) {
//        Employee[] staff = new Employee[3];

        ArrayList<Employee> staff = new ArrayList<>();

        staff.add(new Employee("Bert", 75000, 1988, 12, 15));
        staff.add(new Employee("Banner", 75000, 1998, 12, 15));
        staff.add(new Employee("Bert", 88000, 1999, 12, 15));

        for (Employee e : staff) {
            e.raiseSalary(5);
        }

        for (Employee e : staff) {
            System.out.println("name: " + e.getName() + ", salary: " + e.getSalary() + ", hireDay: " + e.getHireDay());
        }
    }
}
