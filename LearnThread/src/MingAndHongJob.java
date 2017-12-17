class BankAccount {
    private int money = 30;

    public int getMoney() {
        return money;
    }

    public void withDraw(int amount) {
        money -= amount;
    }
}

public class MingAndHongJob implements Runnable {
    private BankAccount account = new BankAccount();

    public static void main(String[] args) {
        MingAndHongJob theJob = new MingAndHongJob();
        Thread one = new Thread(theJob);
        Thread two = new Thread(theJob);
        one.setName("XiaoMing");
        two.setName("XiaoHong");
        one.start();
        two.start();
    }

    public void run() {
        for (int i = 0; i < 3; i++) {
            makeWithDraw(10);
            if (account.getMoney() < 0) {
                System.out.println("Over Draw");
            }
        }
    }

    private synchronized void makeWithDraw(int amount) {
        if (account.getMoney() >= amount) {
            System.out.println(Thread.currentThread().getName() + " is drawing");
            try {
                System.out.println(Thread.currentThread().getName() + " is sleeping");
                Thread.currentThread().sleep(500);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " woke up");
            account.withDraw(amount);
            System.out.println(Thread.currentThread().getName() + " completes the drawing");
        } else {
            System.out.println("Sorry, it is not enough money for " + Thread.currentThread().getName());
        }
    }
}
