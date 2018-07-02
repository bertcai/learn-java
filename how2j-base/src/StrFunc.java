public class StrFunc {
    public static void main(String args[]) {
        String[] array = new String[8];
        int[] flag = new int[8];
        for (int i = 0; i < 8; i++) {
            array[i] = randomStr(5);
            flag[i] = Character.toUpperCase(array[i].charAt(0));
        }

        for (int i = 0; i < 8; i++) {
            for (int j = i; j < 8; j++) {
                if (flag[i] > flag[j]) {
                    int temp = flag[i];
                    String tempStr = array[i];
                    flag[i] = flag[j];
                    flag[j] = temp;
                    array[i] = array[j];
                    array[j] = tempStr;
                }
            }
        }

        for (String str : array) {
            System.out.println(str);
        }

        String password = randomStr(3);
        String password2 = "";

        for (int i = 48; i <= 122; i++) {
            if (i == password.charAt(0)) {
                password2 += password.charAt(0);
                for (int j = 48; j <= 122; j++) {
                    if (j == password.charAt(1)) {
                        password2 += password.charAt(1);
                        for (int k = 48; k <= 122; k++) {
                            if (k == password.charAt(2)) {
                                password2 += password.charAt(2);
                            }
                        }
                    }
                }
            }
        }
        System.out.println(password);
        System.out.println(password2);

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
