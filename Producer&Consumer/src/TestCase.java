public class TestCase {
    public static void main (String[] args){
        Storage storage = new Storage();

        Producer p1 = new Producer(storage);
        Producer p2 = new Producer(storage);
        Producer p3 = new Producer(storage);
        Producer p4 = new Producer(storage);

        Consumer c1 = new Consumer(storage);
        Consumer c2 = new Consumer(storage);
        Consumer c3 = new Consumer(storage);

        Thread pr1 = new Thread(p1);
        Thread pr2 = new Thread(p2);
        Thread pr3 = new Thread(p3);
        Thread pr4 = new Thread(p4);
        Thread cr1 = new Thread(c1);
        Thread cr2 = new Thread(c2);
        Thread cr3 = new Thread(c3);

        cr1.start();
        cr2.start();
        cr3.start();
        pr1.start();
        pr2.start();
        pr3.start();
        pr4.start();
    }
}
