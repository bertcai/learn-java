public class Producer implements Runnable {
    private int num = 1; //produce num
    private int testNum = 20; //test times

    private Storage storage; //store

    public Producer(Storage storage) { //set store
        this.storage = storage;
    }

    public void produce() {
        storage.produce();
    }

    public void run() {
        while (testNum-- > 0) {
            this.produce();
        }
    }
}
