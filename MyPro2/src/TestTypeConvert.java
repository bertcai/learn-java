/**
 * 测试类型转化
 *
 * @author cc
 */
public class TestTypeConvert {
    public static void main(String[] args) {
        int a = 324;
        long b = a;
        double d = b;
//        a = b;
//        long e=3.23F;
        float f = 23124142L;

//        byte b2 = 1230;

        double x = 3.14;
        int nx = (int) x;
        // 注意，强制类型转化对小数的处理是舍去，而不是四舍五入
        char c = 'a';
        int d2 = c + 1;
        System.out.println((char) d2);
    }
}
