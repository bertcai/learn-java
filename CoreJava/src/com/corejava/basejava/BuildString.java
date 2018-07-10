package com.corejava.basejava;

public class BuildString {
    public static void main(String[] args) {
        StringBuilder builder = new StringBuilder();

        char ch = 'a';
        String str = "Hello World";
        builder.append(ch);
        builder.append(str);
        System.out.println(builder);
    }
}
