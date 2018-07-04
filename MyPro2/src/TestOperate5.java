/**
 * 测试条件运算符
 *
 * @author cc
 */
public class TestOperate5 {
    public static void main(String[] args) {
        int score = 80;
        int x = -100;
        String type = score < 60 ? "不及格" : "及格";
        System.out.println(type);

        System.out.println(x > 0 ? 1 : (x == 0 ? 1 : -1));
    }
}
