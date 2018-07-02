public class StringConvert {
    public static void main(String args[]) {
        int i = 5;
        String str = String.valueOf(i);

        Integer integer = i;
        String str2 = integer.toString();

        String str3 = "999";
        int i2 = Integer.parseInt(str3);

        double pi = 3.14;
        String piStr = String.valueOf(pi);

        double pi2 = Double.parseDouble(piStr);

//        double pi3 = Double.parseDouble("3.1a4");

        System.out.println(str);
        System.out.println(str2);
        System.out.println(i2);
        System.out.println(piStr);
        System.out.println(pi2);
//        System.out.println(pi3);

        // 如果字符传为3.1a4，转换会报错
    }
}
