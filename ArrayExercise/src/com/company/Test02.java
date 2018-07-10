package com.company;

/**
 * 测试数组的三种初始化方式
 *
 * @author cc
 */
public class Test02 {
    public static void main(String[] args) {
        // 静态初始化
        int[] a = {2, 3, 65};
        User[] b = {
                new User(1001, "banner"),
                new User(1002, "jack"),
                new User(1003, "joker")
        };
        // 默认初始化
        int[] c = new int[3];
        // 动态初始化
        int[] a1 = new int[2];
        a1[0] = 1;
        a1[1] = 2;

//        System.out.println(a);
//        System.out.println(b);
//        System.out.println(c);
    }
}
