/**
 * 想法
 * 将大数在进行表示时使用字符串进行表示，计算时转化成数组，进行计算。
 */


public class BigNum {
    private String sValue;
    private char[] value;//存储的值低位在前高位在后
    private boolean sign = true; //大数的符号

    //获取值
    public char[] getValue() {
        return this.value;
    }

    //获取字符串值
    public String getsValue(){
        return this.sValue;
    }

    //设置符号
    public void setSign(boolean a) {
        this.sign = a;
    }

    //构造函数
    BigNum(String a) {
//        System.out.println(a.length());
        char si = a.charAt(0);
        if (si == '+') {
            this.sign = true;
            a = a.substring(1);
        }
        if (si == '-') {
            this.sign = false;
            a = a.substring(1);
        }
        //去除前面无意义的0
        if (a.length() > 1) {
            while (si == '0') {
                if (a.length() == 1)
                    break;
                a = a.substring(1);
                si = a.charAt(0);
            }
        }
        this.sValue = a;
        this.value = new StringBuffer(a).reverse().toString().toCharArray();
    }


    //将大数字符串转化成字符数组，低位在前高位在后，便于计算
    public char[] toArray() {
        return this.value;
    }

    //打印结果
    public String toString() {
        if (!this.sign)
            return ("-" + this.sValue);
        else
            return this.sValue;
    }

    //比较大小

    public int compare(BigNum b) {
        if (this.sign != b.sign) {
            return this.sign == true ? 1 : -1;
        }
        if (this.sValue.length() != b.sValue.length()) {
            if (this.sign) {
                return this.sValue.length() > b.sValue.length() ? 1 : -1;
            } else {
                return this.sValue.length() < b.sValue.length() ? 1 : -1;
            }
        }
        int tmp = this.sValue.compareTo(b.sValue);
        if (!this.sign) {
            tmp *= -1;
        }
//        if (tmp > 0) {
//            return -1;
//        }
//        if (tmp < 0) {
//            return 1;
//        }
        return tmp;
    }

    //比较绝对值大小
    public int absCompare(BigNum b) {
        char[] aArray = new StringBuffer(this.sValue).toString().toCharArray();
        char[] bArray = new StringBuffer(b.getsValue()).toString().toCharArray();
        int aLength = aArray.length;
        int bLength = bArray.length;
        if (aLength > bLength) {
            return 1;
        } else {
            if (aLength < bLength) {
                return -1;
            } else {
                int i = 0;
                while (i < aLength && aArray[i] == bArray[i])
                    i++;
                if (i == aLength)
                    return 0;
                if ((aArray[i] - '0') > (bArray[i] - '0'))
                    return 1;
                else
                    return -1;
            }
        }
    }

    public BigNum unsignedAdd(BigNum b) {
        //获取数的数组
        char[] aArray = this.value;
        char[] bArray = b.toArray();
        int aLength = aArray.length;
        int bLength = bArray.length;

        //整数相加最大位数为两数大的位数+1
        int maxLength = aLength >= bLength ? aLength : bLength;
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
            if (result[i] >= 10) {
                result[i + 1] += result[i] / 10;
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
        char[] aArray = this.value;
        char[] bArray = b.toArray();
        int aLength = aArray.length;
        int bLength = bArray.length;

        //找到最大的位数，两整数差的位数小于等于两个整数中的最大位数
        int maxLength = aLength > bLength ? aLength : bLength;
        int[] result = new int[maxLength];

        //判断正负号
        boolean si = true;
        if (aLength < bLength)
            si = false;
        else if (aLength == bLength) {
            int i = maxLength - 1;
            while (i > 0 && aArray[i] == bArray[i])
                i--;
            if (aArray[i] < bArray[i])
                si = false;
        }

        //计算结果
        for (int i = 0; i < maxLength; i++) {
            int aInt = i < aLength ? aArray[i] - '0' : 0;
            int bInt = i < bLength ? bArray[i] - '0' : 0;
            if (!si)
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
        if (!si)
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
        if (realResult.toString().equals(""))
            realResult.append('0');
        return new BigNum(realResult.toString());
    }

    public BigNum add(BigNum b) {
        //计算
        if (!this.sign && !b.sign) {
            BigNum temp = this.unsignedAdd(b);
            String s = temp.getsValue();
            s = "-" + s;
            return new BigNum(s);
        } else if (!this.sign && b.sign) {
            return b.unsignedSubtract(this);
        } else if (this.sign && !b.sign) {
            return this.unsignedSubtract(b);
        } else {
            return this.unsignedAdd(b);
        }
    }

    public BigNum subtract(BigNum b) {
//        //计算符号位
//        BigNum a = new BigNum(this.value);
//        char signA = '+';
//        char signB = '+';
//        char t_signA = a.getValue().charAt(0);
//        char t_signB = b.getValue().charAt(0);
//        if (t_signA == '+' || t_signA == '-') {
//            signA = t_signA;
//            a = new BigNum(a.getValue().substring(1));
//        }
//        if (t_signB == '+' || t_signB == '-') {
//            signB = t_signB;
//            b = new BigNum(b.getValue().substring(1));
//        }
        //计算
        if (!this.sign && !b.sign) {
            return b.unsignedSubtract(this);
        } else if (!this.sign && b.sign) {
            BigNum temp = this.unsignedAdd(b);
            String s = temp.getsValue();
            s = "-" + s;
            return new BigNum(s);
        } else if (this.sign && !b.sign) {
            return this.unsignedAdd(b);
        } else {
            return this.unsignedSubtract(b);
        }
    }

    public BigNum multiply(BigNum b) {
        //计算符号位
        char si;
        if (this.sign == b.sign) {
            si = '+';
        } else {
            si = '-';
        }

        //获取数组
        char[] aArray = this.value;
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
            if (result[i] >= 10) {
                result[i + 1] = result[i + 1] + result[i] / 10;
                result[i] = result[i] % 10;
            }
        }
        //转化结果
        StringBuffer realResult = new StringBuffer();
        if (si == '-')
            realResult.append(si);
        boolean isBegin = true;
        for (int i = maxLength - 1; i >= 0; i--) {
            if (result[i] == 0 && isBegin)
                continue;
            else
                isBegin = false;
            realResult.append(result[i]);
        }
        if (realResult.toString().equals("")) {
            realResult.append('0');
        }
        return new BigNum(realResult.toString());
    }


    public BigNum divide(BigNum b) {
        //除数不能为0
        if (b.getValue().equals("0")) {
            System.out.println("Divide by 0!");
            return new BigNum("error");
        }
        //计算符号位
        boolean si = !(this.sign ^ b.sign);


        //比较被除数与除数的大小
        if (this.absCompare(b) == -1)
            return new BigNum("0");

        String x = this.sValue, y = b.sValue, addZero = new String("1");
        int cnt = x.length() - y.length(); //被除数与除数相差位数
        for (int i = 0; i < cnt; i++) {
            y += "0";
            addZero += "0";
        }
//        BigNum mod = new BigNum("0");
        BigNum divA = new BigNum(x), divB = new BigNum(y);
        BigNum quotien = new BigNum("0");
        while (cnt >= 0) {
            BigNum addBI = new BigNum(addZero);
            while (divA.compare(divB) >= 0) {
                quotien = quotien.add(addBI);
                divA = divA.subtract(divB);
//                mod = divA;
            }
            divB = new BigNum(divB.sValue.substring(0, Math.max(1, divB.sValue.length() - 1)));
            addZero = addZero.substring(0, Math.max(1, cnt));
            cnt--;
        }
        quotien.setSign(si);
//        System.out.println(mod.toString());
        return quotien;
    }


    public String divideMod(BigNum b) {
        //除数不能为0
        BigNum [] result = new BigNum[2];
        if (b.getValue().equals("0")) {
            System.out.println("Divide by 0!");
            return "error";
        }
        //计算符号位
        boolean si = !(this.sign ^ b.sign);


        //比较被除数与除数的大小
        if (this.absCompare(b) == -1) {
//            return new BigNum("0");
            result[0] = new BigNum("0");
            result[1] = new BigNum(this.toString());
        }
        String x = this.sValue, y = b.sValue, addZero = new String("1");
        int cnt = x.length() - y.length(); //被除数与除数相差位数
        for (int i = 0; i < cnt; i++) {
            y += "0";
            addZero += "0";
        }
        BigNum divA = new BigNum(x), divB = new BigNum(y);
        BigNum quotien = new BigNum("0");
        while (cnt >= 0) {
            BigNum addBI = new BigNum(addZero);
            while (divA.compare(divB) >= 0) {
                quotien = quotien.add(addBI);
                divA = divA.subtract(divB);
            }
            divB = new BigNum(divB.sValue.substring(0, Math.max(1, divB.sValue.length() - 1)));
            addZero = addZero.substring(0, Math.max(1, cnt));
            cnt--;
        }
        quotien.setSign(si);
        if (!divA.sign)
            divA = divA.add(b);
        result[0] = quotien;
        result[1] = divA;
        return result[0].toString()+" "+result[1].toString();
    }



    public BigNum mod(BigNum b) {
        BigNum tmp = this.divide(b);
        tmp = tmp.multiply(b);
        BigNum mod = this.subtract(tmp);
        if (!mod.sign)
            return mod.add(b);
        else
            return mod;
    }
}
