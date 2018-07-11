package com.corejava.objectandclass;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Date;

/**
 * 测试Date类，作为类的初学类的测试
 *
 * @author cc
 */
public class TestDate {
    public static void main(String[] args) {
        Date now = new Date();
        System.out.println(now);

        Date birthday = new Date();
        Date deathday;

        LocalDate now2 = LocalDate.now();
        System.out.println(now2);

        LocalDate ThousandDayLater = now2.plusDays(1000);
        System.out.println(ThousandDayLater);

        // 只访问对象而不更改对象的方法称为访问器方法
        // 更改对象的方法称为更改器方法

        // 日历
        LocalDate date = LocalDate.now();
        int month = date.getMonthValue();
        int today = date.getDayOfMonth();
        date = date.minusDays(today - 1);
        DayOfWeek weekday = date.getDayOfWeek();
        int value = weekday.getValue();

        System.out.println("Mon Tue Wed Thu Fri Sat Sun");
        for (int i = 1; i < value; i++) {
            System.out.print("    ");
        }

        while (date.getMonthValue() == month) {
            System.out.printf("%3d", date.getDayOfMonth());
            if (date.getDayOfMonth() == today) {
                System.out.print("*");
            } else {
                System.out.print(" ");
            }
            date = date.plusDays(1);
            if (date.getDayOfWeek().getValue() == 1) {
                System.out.println();
            }
        }
    }
}
