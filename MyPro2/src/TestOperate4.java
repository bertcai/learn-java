/**
 * 测试位运算
 *
 * @author cc
 */
public class TestOperate4 {
    public static void main(String[] args) {
        int a = 3;
        int b = 4;
        System.out.println(a & b);
        System.out.println(a | b);
        System.out.println(a ^ b);
        System.out.println(~a);

        System.out.println(a >> 2);
        System.out.println(a << 2);
    }
}
