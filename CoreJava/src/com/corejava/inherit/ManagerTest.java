package com.corejava.inherit;

public class ManagerTest {
    public static void main(String[] args) {
        Manager boss = new Manager("Carl Cracker", 80000, 1989, 12, 2);
        boss.setBonus(20000);

        Employee[] staff = new Employee[3];

        staff[0] = boss;
        staff[1] = new Employee("Tom Coder", 50000, 1993, 04, 7);
        staff[2] = new Employee("Bert Tester", 60000, 1998, 5, 2);

        for (Employee e : staff) {
            System.out.println("name: " + e.getName() + " ,salary: " + e.getSalary());
        }
    }
}
