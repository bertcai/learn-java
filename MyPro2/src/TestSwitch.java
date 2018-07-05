/**
 * 测试Switch
 *
 * @author cc
 */

public class TestSwitch {
    public static void main(String[] args) {
        int month = (int) (1 + 12 * Math.random());
        System.out.println(month);

        switch (month) {
            case 1:
                System.out.println("一月");
                break;
            case 2:
                System.out.println("二月");
                break;
            default:
                System.out.println("其他");
                break;
        }
    }
}
