import java.math.BigDecimal;

/**
 * 测试浮点数
 *
 * @author cc
 */
public class TestPrimitiveType2 {
    public static void main(String[] args) {
        float a = 3.14f;
        double b = 6.28;
        double c = 528E-2;
        System.out.println(c);

        BigDecimal bd = BigDecimal.valueOf(1.0);
        bd = bd.subtract(BigDecimal.valueOf(0.1));
        bd = bd.subtract(BigDecimal.valueOf(0.1));
        bd = bd.subtract(BigDecimal.valueOf(0.1));
        bd = bd.subtract(BigDecimal.valueOf(0.1));
        bd = bd.subtract(BigDecimal.valueOf(0.1));
        System.out.println(bd);
        System.out.println(1.0 - 0.1 - 0.1 - 0.1 - 0.1 - 0.1);

        BigDecimal bd2 = BigDecimal.valueOf(0.1);
        BigDecimal bd3 = BigDecimal.valueOf(1.0 / 10.0);
        System.out.println(bd2.equals(bd3));
    }
}
