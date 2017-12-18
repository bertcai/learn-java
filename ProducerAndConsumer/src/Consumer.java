import java.util.ArrayList;

public class Consumer implements Runnable {
    private int num = 1;
    private int testNum = 20;

    private Storage storage;

    public Consumer(Storage storage) { //set store
        this.storage = storage;
    }

    public void consume() {
        storage.consume();
    }

    public void run() {
        while (testNum-- > 0) {
            this.consume();
        }

        ArrayList
    }
}
