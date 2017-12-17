// operate P&V 's Sign class
class Num {
    private int value;

    Num(int num) {
        this.value = num;
    }

    public void setNum(int num) {
        this.value = num;
    }

    public int getNum() {
        return value;
    }
}

public class Storage {
    private final int size = 1; //size
    private int num = 0; //store num

//    private int empty = size;
//    private int full = num;

    private Num empty = new Num(size);
    private Num full = new Num(num);

    private void operateP(Num p) {
        int temp = p.getNum();
        temp--;
        p.setNum(temp);
    }

    private void operateV(Num v) {
        int temp = v.getNum();
        temp++;
        v.setNum(temp);
    }

    public synchronized void produce() {
        while (empty.getNum() == 0) {
            try {
                wait();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        num++;
        operateP(empty); //P(empty)
        operateV(full); //V(full)
        System.out.println("produce, product left: " + num);
        notifyAll();
    }

    public synchronized void consume() {
        while (full.getNum() == 0) {
            try {
                wait();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        num--;
        operateP(full); //P(full)
        operateV(empty); //V(empty)
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
