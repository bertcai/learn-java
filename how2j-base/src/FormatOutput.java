import java.util.Locale;
import java.util.Scanner;

public class FormatOutput {
    public static void main(String args[]) {
        String name = "Ciry";
        int age = 24;
        String sex = "girl";

        String sentence = "%s is a %d %s. %n";
        System.out.printf(sentence, name, age, sex);
        System.out.format(sentence, name, age, sex);

        int year = 2018;

        System.out.printf("%d%n", year);
        System.out.printf("%8d%n", year);
        System.out.printf("%-8d%n", year);
        System.out.printf("%08d%n", year);
        System.out.printf("%,8d%n", year * 1000);
        System.out.printf("%.2f%n", Math.PI);

        System.out.format(Locale.FRANCE, "%,.2f%n", Math.PI * 10000);
        System.out.format(Locale.US, "%,.2f%n", Math.PI * 10000);
        System.out.format(Locale.UK, "%,.2f%n", Math.PI * 10000);

        Scanner scanner = new Scanner(System.in);

        System.out.println("Please input Address:");
        String address = scanner.nextLine();

        String home = "My homeland is %s.";

        System.out.printf(home, address);
    }
}
