public class Storage {
    private final int size = 1; //size
    private int num = 0; //store num

    private int empty = size;
    private int full = num;

    public synchronized void produce() {
        while (empty == 0) {
            try {
                wait();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        num++;
        empty--; //P(empty)
        full++; //V(full)
        System.out.println("produce, product left: " + num);
        notifyAll();
    }

    public synchronized void consume() {
        while (full == 0) {
            try {
                wait();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        num--;
        full--; //P(full)
        empty++; //V(empty)
        System.out.println("consume, product left: " + num);
        notifyAll();
    }

//    public static void main(String[] args) {
//        Storage storage = new Storage();
//
//        Producer produce = new Producer(storage);
//        Consumer consume = new Consumer(storage);
//
//        Thread producer = new Thread(produce);
//        Thread consumer = new Thread(consume);
//
//        consumer.start();
//        producer.start();
//    }
}
