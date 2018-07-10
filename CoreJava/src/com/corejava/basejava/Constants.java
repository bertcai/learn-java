package com.corejava.basejava;

public class Constants {
    public static void main(String[] args) {
        final double CM_PER_IMCH = 2.54;
        // const 是java保留字，但是并未使用，java中必须使用final来是定义常量
        double paperWidth = 8.5;
        double paperHeight = 11;
        System.out.println("Paper size: " + paperWidth * CM_PER_IMCH + " by " + paperHeight * CM_PER_IMCH);
    }
}
