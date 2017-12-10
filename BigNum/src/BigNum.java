/**
 * 想法
 * 将大数在进行表示时使用字符串进行表示，计算时转化成数组，进行计算。
 */

import java.math.*;

public class BigNum {
    private String value;

    //获取值
    public String getValue() {
        return value;
    }

    //构造函数
    BigNum(String a) {
        value = a;
    }


    //将大数字符串转化成字符数组，低位在前高位在后，便于计算
    public char[] toArray() {
        return new StringBuffer(value).reverse().toString().toCharArray();
    }

    public BigNum unsignedAdd(BigNum b) {
        //获取数的数组
        char[] aArray = new StringBuffer(value).reverse().toString().toCharArray();
        char[] bArray = b.toArray();
        int aLength = aArray.length;
        int bLength = bArray.length;

        //整数相加最大位数为两数大的位数+1
        int maxLength = aLength > bLength ? aLength : bLength;
        int[] result = new int[maxLength + 1];

        //按位相加
        for (int i = 0; i < maxLength; i++) {
            //判断当前位是否超过该数的最大位，若是，则用0继续计算
            int aInt = i < aLength ? (aArray[i] - '0') : 0;
            int bInt = i < bLength ? (bArray[i] - '0') : 0;
            result[i] = aInt + bInt;
        }

        //处理进位
        for (int i = 0; i < maxLength; i++) {
            if (result[i] > 10) {
                result[i + 1] = result[i + 1] + 1;
                result[i] = result[i] % 10;
            }
        }

        StringBuffer realResult = new StringBuffer();
        //判断结果最高位是否为0，若为0，则不打印出来
        for (int i = maxLength; i >= 0; i--) {
            if (result[i] == 0 && i == maxLength)
                continue;
            //结果高位在前低位在后
            realResult.append(result[i]);
        }
        return new BigNum(realResult.toString());
    }

    public BigNum unsignedSubtract(BigNum b) {
        //获取数的数组
        char[] aArray = new StringBuffer(value).reverse().toString().toCharArray();
        char[] bArray = b.toArray();
        int aLength = aArray.length;
        int bLength = bArray.length;

        //找到最大的位数，两整数差的位数小于等于两个整数中的最大位数
        int maxLength = aLength > bLength ? aLength : bLength;
        int[] result = new int[maxLength];

        //判断正负号
        char sign = '+';
        if (aLength < bLength)
            sign = '-';
        else if (aLength == bLength) {
            int i = maxLength - 1;
            while (i > 0 && aArray[i] == bArray[i])
                i--;
            if (aArray[i] < bArray[i])
                sign = '-';
        }

        //计算结果
        for (int i = 0; i < maxLength; i++) {
            int aInt = i < aLength ? aArray[i] - '0' : 0;
            int bInt = i < bLength ? bArray[i] - '0' : 0;
            if (sign == '-')
                result[i] = bInt - aInt;
            else
                result[i] = aInt - bInt;
        }

        //处理借位
        for (int i = 0; i < maxLength; i++) {
            if (result[i] < 0) {
                result[i + 1] = result[i + 1] - 1;
                result[i] = result[i] + 10;
            }
        }

        //转化结果
        StringBuffer realResult = new StringBuffer();
        if (sign == '-')
            realResult.append('-');
        boolean isBegin = true;
        for (int i = maxLength - 1; i >= 0; i--) {
            if (result[i] == 0 && isBegin)
                continue;
            else
                isBegin = false;
            realResult.append(result[i]);
        }

        //为0时
        if (realResult.equals(""))
            realResult.append('0');
        return new BigNum(realResult.toString());
    }

    public BigNum add(BigNum b) {
        //计算符号位
        BigNum a = new BigNum(value);
        char signA = '+';
        char signB = '+';
        char t_signA = a.getValue().charAt(0);
        char t_signB = b.getValue().charAt(0);
        if (t_signA == '+' || t_signA == '-') {
            signA = t_signA;
            a = new BigNum(a.getValue().substring(1));
        }
        if (t_signB == '+' || t_signB == '-') {
            signB = t_signB;
            b = new BigNum(b.getValue().substring(1));
        }
        //计算
        if (signA == '-' && signB == '-') {
            BigNum temp = a.unsignedAdd(b);
            String s = temp.getValue();
            s = "-" + s;
            return new BigNum(s);
        } else if (signA == '-' && signB == '+') {
            return b.unsignedSubtract(a);
        } else if (signA == '+' && signB == '-') {
            return a.unsignedSubtract(b);
        } else {
            return a.unsignedAdd(b);
        }
    }

    public BigNum subtract(BigNum b) {
        //计算符号位
        BigNum a = new BigNum(value);
        char signA = '+';
        char signB = '+';
        char t_signA = a.getValue().charAt(0);
        char t_signB = b.getValue().charAt(0);
        if (t_signA == '+' || t_signA == '-') {
            signA = t_signA;
            a = new BigNum(a.getValue().substring(1));
        }
        if (t_signB == '+' || t_signB == '-') {
            signB = t_signB;
            b = new BigNum(b.getValue().substring(1));
        }
        //计算
        if (signA == '-' && signB == '-') {
            return b.unsignedSubtract(a);
        } else if (signA == '-' && signB == '+') {
            BigNum temp = a.unsignedAdd(b);
            String s = temp.getValue();
            s = "-" + s;
            return new BigNum(s);
        } else if (signA == '+' && signB == '-') {
            return a.unsignedAdd(b);
        } else {
            return a.unsignedSubtract(b);
        }
    }

    public BigNum multiply(BigNum b) {
        //计算符号位
        char signA = value.charAt(0);
        char signB = b.getValue().charAt(0);
        char sign = '+';
        if (signA == '+' || signA == '-') {
            sign = signA;
            value = value.substring(1);
        }
        if (signB == '+' || signB == '-') {
            if (sign == signB)
                sign = '+';
            else
                sign = '-';
            b = new BigNum(b.getValue().substring(1));
        }

        //获取数组
        char[] aArray = new StringBuffer(value).reverse().toString().toCharArray();
        char[] bArray = b.toArray();
        int aLength = aArray.length;
        int bLength = bArray.length;

        //两数相乘，最大位数为两大整数之和
        int maxLength = aLength + bLength;
        int[] result = new int[maxLength];

        //计算结果，第i位于第j位相乘的结果是第i+j位的结果
        for (int i = 0; i < aLength; i++) {
            for (int j = 0; j < bLength; j++) {
                result[i + j] = result[i + j] + (aArray[i] - '0') * (bArray[j] - '0');
            }
        }

        //处理进位
        for (int i = 0; i < maxLength - 1; i++) {
            if (result[i] > 10) {
                result[i + 1] = result[i + 1] + result[i] / 10;
                result[i] = result[i] % 10;
            }
        }
        //转化结果
        StringBuffer realResult = new StringBuffer();
        if (sign == '-')
            realResult.append(sign);
        boolean isBegin = true;
        for (int i = maxLength - 1; i >= 0; i--) {
            if (result[i] == 0 && isBegin)
                continue;
            else
                isBegin = false;
            realResult.append(result[i]);
        }
        if (realResult.toString() == "")
            realResult.append('0');
        return new BigNum(realResult.toString());
    }





    //初次简单测试加法
    public static void main(String[] args) {
        BigNum a = new BigNum("88900988");
        BigNum b = new BigNum("7878778888");
        BigInteger c = new BigInteger("88900988");
        BigInteger d = new BigInteger("7878778888");
        System.out.println(a.add(b).getValue());
        System.out.println(a.subtract(b).getValue());
        System.out.println(a.multiply(b).getValue());
        System.out.println(c.add(d));
        System.out.println(c.subtract(d));
        System.out.println(c.multiply(d));

    }
}
