class RunnableDemo implements Runnable {
//    private Thread t;
//    private String threadName;
//
//    RunnableDemo(String name) {
//        threadName = name;
//        System.out.println("Creating " + threadName);
//    }
//
//    public void run() {
//        System.out.println("Running " + threadName);
//        try {
//            for (int i = 4; i > 0; i--) {
//                System.out.println("Thread: " + threadName + ", " + i);
//                Thread.sleep(50);
//            }
//        } catch (InterruptedException e) {
//            System.out.print("Thread " + threadName + "interrupted.");
//        }
//        System.out.println("Thread " + threadName + " exiting.");
//    }
//
//    public void start() {
//        System.out.println("Thread " + threadName + " starting!");
//        if (t == null) {
//            t = new Thread(this, threadName);
//            t.start();
//        }
//    }
    public void run(){
        go();
    }
    public void go(){
        doMore();
    }
    public void doMore(){
        System.out.println("top o' the stack");
    }
}

class ThreadDemo extends Thread {
    private Thread t;
    private String threadName;

    ThreadDemo(String name) {
        threadName = name;
        System.out.println("Creating " + threadName);
    }

    public void run() {
        System.out.println("Running " + threadName);
        try {
            for (int i = 4; i > 0; i--) {
                System.out.println("Thread: " + threadName + ", " + i);
                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            System.out.print("Thread " + threadName + "interrupted.");
        }
        System.out.println("Thread " + threadName + " exiting.");
    }

    public void start() {
        System.out.println("Thread " + threadName + " starting!");
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }
}

public class TestThread {
    public static void main(String[] args) {
//        RunnableDemo r1 = new RunnableDemo("Thread-1");
//        r1.start();
//
//        RunnableDemo r2 = new RunnableDemo("Thread-2");
//        r2.start();


//        ThreadDemo t1 = new ThreadDemo("Thread-1");
//        ThreadDemo t2 = new ThreadDemo("Thread-2");
//        t1.start();
//        t2.start();
     RunnableDemo threadJob = new RunnableDemo();
     Thread myThread = new Thread(threadJob);

     myThread.start();

     System.out.println("back in main");

    }
}
