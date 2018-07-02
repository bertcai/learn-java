public class MathFunc {

    private static boolean isPrime(int n) {
        if (n == 2) return true;

        double temp = Math.sqrt(n);

        for (int i = 2; i <= temp; i++) {
            if (n % i == 0) return false;
        }
        return true;
    }

    public static void main(String args[]) {
        float f1 = 5.4f;
        float f2 = 5.5f;

        System.out.println(Math.round(f1));
        System.out.println(Math.round(f2));

        System.out.println(Math.random());

        System.out.println((int) (Math.random() * 10));

        System.out.println(Math.sqrt(9));

        System.out.println(Math.pow(2, 4));

        System.out.println(Math.PI);

        System.out.println(Math.E);

        double e = Math.pow((1 + 1.0 / Integer.MAX_VALUE), Integer.MAX_VALUE);
        System.out.println(e);

        int sum = 0;
        for (int i = 2; i <= 10000*1000; i++) {
            if(isPrime(i)){
                sum++;
            }
        }

        System.out.println(sum);
    }
}


