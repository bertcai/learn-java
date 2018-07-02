public class ControlString {
    public static void main(String args[]) {
        String str = "let there be light";
        String subStr[] = str.split(" ");
        String strUp = "";
        for (String s : subStr) {
            char a = Character.toUpperCase(s.charAt(0));
            s = s.substring(1);
            strUp += a + s + " ";
        }
        strUp = strUp.trim();
        System.out.println(strUp);

        int count = 0;
        String str2 = "peter piper picked a peck of pickled peppers";
        String subStr2[] = str2.split(" ");
        for (String s : subStr2) {
            if (s.charAt(0) == 'p') {
                count++;
            }
        }
        System.out.println(count);

        String str3 = "lengendary";
        int str3Length = str3.length();
        String str3Up = "";
        for (int i = 0; i < str3.length(); i++) {
            char temp = str3.charAt(i);
            if (i % 2 == 0) {
                temp = Character.toUpperCase(temp);
            }
            str3Up += temp;
        }
        System.out.println(str3Up);
        System.out.println(str3.substring(0, str3Length - 1)
                + Character.toUpperCase(str3.charAt(str3Length - 1)));

        String str4 = "Nature has given us that two ears, two eyes, and but one tongue, to the end that we should hear " +
                "and see more than we speak";
        int index = str4.lastIndexOf("two");
        int str4Length = str4.length();
        char temp4 = str4.charAt(index);
        temp4 = Character.toUpperCase(temp4);
        String str4Up = str4.substring(0, index) + temp4 + str4.substring(index + 1);
        System.out.println(str4Up);
    }
}
