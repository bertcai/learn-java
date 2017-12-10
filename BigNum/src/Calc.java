public class Calc {

    public String add(BigNum a, BigNum b) {
        //获取数的数组
        char[] aArray = a.toArray();
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
        return realResult.toString();
    }

    public static void main(String[] args) {
        String a = "88900988";
        String b = "7878778888";
//        System.out.println(add(a, b));

    }

    public void sub(BigNum a, BigNum b) {

    }

    public void mul(BigNum a, BigNum b) {

    }

    public void div(BigNum a, BigNum b) {

    }
}
