public class CompareString {
    public static void main(String args[]) {
        String[] strings = new String[100];
        for (int i = 0; i < 100; i++) {
            strings[i] = randomStr(2);
            System.out.printf("%s ", strings[i]);
        }

        for (int i = 0; i < 100; i++) {
            for (int j = i+1; j < 100; j++) {
                String str1 = strings[i];
                String str2 = strings[j];
                if (str1.equals(str2)) {
                    System.out.println(str1);
                }
            }
        }
    }

    private static String randomStr(int n) {
        String str = "";
        for (int i = 0; i < n; i++) {
            int number = (int) (48 + Math.random() * 10);
            int upperCase = (int) (65 + Math.random() * 26);
            int lowerCase = (int) (97 + Math.random() * 26);
            int flag = (int) (3 * Math.random());
            switch (flag) {
                case 0:
                    str += (char) number;
                    break;
                case 1:
                    str += (char) lowerCase;
                    break;
                case 2:
                    str += (char) upperCase;
                    break;
                default:
                    break;
            }
        }
        return str;
    }
}
