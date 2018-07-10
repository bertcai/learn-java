package com.company;

/**
 * 测试数组的遍历
 *
 * @author cc
 */

public class Test03 {
    public static void main(String[] args) {
        int[] a = new int[4];

        for (int i = 0; i < a.length; i++) {
            a[i] = i;
        }

        for (int i = 0; i < a.length; i++) {
            System.out.println(a[i]);
        }

        System.out.println("############");

        for (int m : a) {
            System.out.println(m);
        }

        String[] ss = {"a", "b", "c", "d"};

        for (String s : ss) {
            System.out.println(s);
        }
    }
}
