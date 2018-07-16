package com.corejava.objectandclass;

public class ParamTest {
    public static void main(String[] args) {
        // 一个方法无法修改一个基本数据类型的参数
        System.out.println("Test TripleValue:");
        double percent = 10;
        System.out.println("before: " + percent);
        tripleValue(percent);
        System.out.println("after: " + percent);

        // 一个方法可以改变对象的参数状态
        System.out.println("\nTest TripleSalary");
        Employee harry = new Employee("Harry", 50000);
        System.out.println("before: " + harry.getSalary());
        tripleSalary(harry);
        System.out.println("after: " + harry.getSalary());

        // 一个方法不能让对象参数引用一个新的对象
        System.out.println("\nTest swap");
        Employee a = new Employee("Ailey", 50000);
        Employee b = new Employee("Bob", 50000);
        System.out.println("a: " + a.getName());
        System.out.println("b: " + b.getName());
        swap(a, b);
        System.out.println("a: " + a.getName());
        System.out.println("b: " + b.getName());
    }

    public static void tripleValue(double x) {
        x = 3 * x;
        System.out.println("Method: " + x);
    }

    public static void tripleSalary(Employee x) {
        x.raiseSalary(200);
        System.out.println("Method salary: " + x.getSalary());
    }

    public static void swap(Employee a, Employee b) {
        Employee temp = a;
        a = b;
        b = temp;
        System.out.println("a: " + a.getName());
        System.out.println("b: " + b.getName());
    }
}