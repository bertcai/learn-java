//import java.util.Scanner;
//
//public class Main {
//    public static void main(String args[]) {
//        Scanner sc = new Scanner(System.in);
//        while (sc.hasNext()) {
//            int[] receive = new int[21];
//            int[] receiveSum = new int[18];
//            for (int i = 0; i < 21; i++) {
//                receive[i] = sc.nextInt();
//            }
//            int max = -1;
//            int temp = 0;
//            for (int i = 0; i < 18; i++) {
//                receiveSum[i] = receive[i]+receive[i+1]+receive[i+2]+receive[i+3];
//            }
//            for(int i=0;i<18;i++){
//                if(max < receiveSum[i]){
//                    max = receiveSum[i];
//                    temp=i;
//                }
//            }
//            System.out.println(temp);
//        }
//    }
//}
//
//import java.util.Scanner;
//
//public class Main {
//    public static void main(String args[]) {
//        String input = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
//        String output = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm";
//        Scanner sc = new Scanner(System.in);
//        while (sc.hasNext()) {
//            String inputStr = sc.nextLine();
//            String outputStr = "";
//            for (int i = 0; i < inputStr.length(); i++) {
//                char c = inputStr.charAt(i);
//                if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
//                    int temp = input.indexOf(c);
//                    outputStr += output.charAt(temp);
//                } else {
//                    outputStr += c;
//                }
//            }
//            System.out.println(outputStr);
//        }
//    }
//}


import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            String input = sc.nextLine();
            String[] inputs = input.split(",");
            int[] inputArray = new int[inputs.length];
            for (int i = 0; i < inputs.length; i++) {
                String temp = inputs[i].trim();
                inputArray[i] = Integer.parseInt(temp);
            }
            System.out.println(Arrays.toString(inputArray));
            long sum = 0;
            long maxSum = Integer.MIN_VALUE;
            boolean flag = false;
            for (int i = 0; i < inputArray.length; i++) {
                if (inputArray[i] >= 0) {
                    flag = true;
                }
            }
            for (int i = 0; i < inputArray.length; i++) {
                if (!flag) {
                    sum = inputArray[i];
                }
                if (flag) {
                    sum += inputArray[i];
                    if (sum < 0) {
                        sum = 0;
                    }
                }
                if (sum > maxSum) {
                    maxSum = sum;
                }
            }
            System.out.println(maxSum);
        }
    }
}