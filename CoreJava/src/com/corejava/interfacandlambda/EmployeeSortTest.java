package com.corejava.interfacandlambda;

import com.corejava.inherit.Employee;

import java.util.Arrays;

public class EmployeeSortTest {
    public static void main(String[] args) {
        Employee[] staff = new Employee[3];

        staff[0] = (new Employee("Harry", 35000, 1989, 6, 24));
        staff[1] = (new Employee("Nick", 39762, 1989, 6, 24));
        staff[2] = (new Employee("Bert", 21000, 1989, 6, 24));

        Arrays.sort(staff);

        for (Employee e : staff) {
            System.out.println(e.getName());
        }
    }
}
