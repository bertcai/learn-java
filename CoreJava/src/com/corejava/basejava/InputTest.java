package com.corejava.basejava;

import java.io.Console;
import java.util.Date;
import java.util.Scanner;

/**
 * 测试输入
 *
 * @author cc
 * @version 1.0
 */
public class InputTest {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        System.out.println("What is your name ?");
        String name = in.nextLine();
        System.out.println("How old are you ?");
        int age = in.nextInt();

        System.out.println("name: " + name + "\n" + "age: " + age);

//        Console cons = System.console();
//        String username = cons.readLine("Username: ");
//        char[] passwd = cons.readPassword("Password: ");

        System.out.printf("%tc", new Date());
    }
}
