public class InOrOutBox {
    public static void main(String[] args) {
//        int i = 5;
//        Integer it = new Integer(i);
//        int i2 = it.intValue();
//        System.out.println(it instanceof Number);

        byte b = 1;
        short s = 2;
        int i = 3;
        float f = 3.14f;
        double d = 3.14;
        Byte b2 = b;
        Short s2 = s;
        Float f2 = f;
        Double d2 = d;
        Integer i2 = i;
        int i3 = b2;
        // 题2答案： int不可以转byte，byte可以转int，int可以对Byte拆箱，但是不能对byte装箱

        System.out.println(Byte.MAX_VALUE);
        System.out.println(Byte.MIN_VALUE);
    }
}
