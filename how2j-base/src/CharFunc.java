import java.util.Scanner;

public class CharFunc {
    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        char[] chars = input.toCharArray();

        String output = "";
        for (int i = 0; i < chars.length; i++) {
            if (Character.isDigit(chars[i])) {
                output += chars[i];
            }
            if (Character.isUpperCase(chars[i])) {
                output += chars[i];
            }
        }

        System.out.println(output);
    }
}
