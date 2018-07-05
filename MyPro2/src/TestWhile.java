/**
 * 测试循环
 *
 * @author cc
 */
public class TestWhile {
    public static void main(String[] args) {
//        int i = 1;
        int sum = 0;
//
//        while (i <= 100) {
//            sum += i;
//            i++;
//        }

        System.out.println(sum);

        int sum2 = 0;
        for (int j = 0; j <= 100; j++) {
            sum2 += j;
        }
        System.out.println(sum2);

        for (int a = 1, b = a + 10; a < 5; a++, b = a * 2) {
            System.out.println("" + a + " " + b);
        }

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                System.out.print(i + "\t");
            }
            System.out.println();
        }
        int res = 0;
        for (int i = 1; i <= 9; i++) {
            for (int j = 1; j <= i; j++) {
                res = i * j;
                System.out.print(j + "*" + i + "=" + res + "\t");
            }
            System.out.println();
        }

        int i = 1;
        int sum01 = 0;
        int sum02 = 0;
        while (i <= 100) {
            if (i % 2 == 0) {
                sum01 += i;
            } else {
                sum02 += i;
            }
            i++;
        }
        System.out.println("偶数和：" + sum01);
        System.out.println("奇数和：" + sum02);

        int k = 0;
        for (int j = 1; j <= 1000; j++) {
            if (j % 5 == 0) {
                System.out.print(j + "\t");
                k++;
            }
            if (k == 5) {
                k = 0;
                System.out.println();
            }
        }
    }
}
