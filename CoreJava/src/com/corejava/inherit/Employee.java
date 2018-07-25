package com.corejava.inherit;

import java.time.LocalDate;
import java.util.Objects;

public class Employee extends Person implements Comparable<Employee> {
    private double salary;
    private LocalDate hireDay;

    public Employee(String name, double salary, int year, int month, int day) {
        super(name);
        this.salary = salary;
        this.hireDay = LocalDate.of(year, month, day);
    }


    public LocalDate getHireDay() {
        return hireDay;
    }

    public double getSalary() {
        return salary;
    }

    @Override
    public String getDescription() {
        return String.format("an employee with a salary of $%.2f", salary);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        Employee orther = (Employee) obj;

        return Objects.equals(this.getName(), ((Employee) obj).getName())
                && salary == ((Employee) obj).salary
                && Objects.equals(hireDay, ((Employee) obj).hireDay);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getName(), salary, hireDay);
    }

    @Override
    public String toString() {
        return getClass().getName() + "[name=" + this.getName() + ", " +
                "salary=" + salary + ", hireDay=" + hireDay + "]";
    }

    @Override
    public int compareTo(Employee o) {
        return Double.compare(salary, o.getSalary());
        // 这里使用静态compare方法能够避免舍入误差对结果造成影响
    }

    public void raiseSalary(double byPercent) {
        double raise = salary * byPercent / 100;
        salary += raise;
    }
}
