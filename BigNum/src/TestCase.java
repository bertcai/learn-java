import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.math.BigInteger;

public class TestCase {

    public static void main(String[] args) {
        String a = "", b = "";
        for (int i = 0; i < 40; i++) {
            int t = (int) (Math.random() * 10);
            a += t;
        }
        for (int i = 0; i < 40; i++) {
            int t = (int) (Math.random() * 10);
            b += t;
        }

//        a = "-"+a;
//        b = "-"+b;
        BigNum x = new BigNum(a);
        BigNum y = new BigNum(b);
        BigInteger m = new BigInteger(a);
        BigInteger n = new BigInteger(b);

        System.out.println(a);
        System.out.println(b);

        System.out.println("This is ture answer: ");
        System.out.println(m.add(n));
        System.out.println(m.subtract(n));
        System.out.println(m.multiply(n));
        System.out.println(m.divide(n));
        System.out.println(m.mod(n));

        System.out.println("This is test answer: ");
        System.out.println(x.add(y).toString());
        System.out.println(x.subtract(y).toString());
        System.out.println(x.multiply(y).toString());
        System.out.println(x.divide(y).toString());
        System.out.println(x.mod(y).toString());

        System.out.println("This is result: ");
        System.out.println(x.add(y).toString().equals(m.add(n).toString()));
        System.out.println(x.subtract(y).toString().equals(m.subtract(n).toString()));
        System.out.println(x.multiply(y).toString().equals(m.multiply(n).toString()));
        System.out.println(x.divide(y).toString().equals(m.divide(n).toString()));
        System.out.println(x.mod(y).toString().equals(m.mod(n).toString()));


//        BigNum a = new BigNum("2238315117249912249398702771272189240702");
//        BigNum b = new BigNum("0256260089943676888682445390856578962923");
//        BigInteger c = new BigInteger("2238315117249912249398702771272189240702");
//        BigInteger d = new BigInteger("0256260089943676888682445390856578962923");
////        BigNum a = new BigNum("88");
////        BigNum b = new BigNum("787");
////        BigInteger c = new BigInteger("88");
////        BigInteger d = new BigInteger("787");
//
//
//
//        System.out.println(a.add(b).toString());
//        System.out.println(a.subtract(b).toString());
//        System.out.println(a.multiply(b).toString());
//        System.out.println(a.divide(b).toString());
//        System.out.println(a.mod(b).toString());
//        System.out.println(c.add(d));
//        System.out.println(c.subtract(d));
//        System.out.println(c.multiply(d));
//        System.out.println(c.divide(d));
//        System.out.println(c.mod(d));

    }
}
