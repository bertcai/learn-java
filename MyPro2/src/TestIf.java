/**
 * 测试选择机构
 *
 * @author cc
 */

public class TestIf {
    public static void main(String[] args) {
        double d = Math.random();
        int i = (int) (6 * d + 1);
        System.out.println(i);
        if (i <= 3) {
            System.out.println("小");
        } else {
            System.out.println("大");
        }

        double r = 4 * Math.random();
        double area = Math.PI * Math.pow(r, 2);
        double circle = 2 * Math.PI * r;
        System.out.println("r: " + r);
        System.out.println("area: " + area);
        System.out.println("circle: " + circle);
        if (area > circle) {
            System.out.println("面积大于周长");
        } else {
            System.out.println("周长大于面积");
        }
    }
}
