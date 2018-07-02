import java.awt.*;
import javax.swing.*;

public class BallGame2 extends JFrame {

    Image ball = Toolkit.getDefaultToolkit().getImage("images/ball.png");
    Image desk = Toolkit.getDefaultToolkit().getImage("images/desk.jpg");

    //(x,y) of the ball
    double x = 100;
    double y = 100;
    double degree = 3.14 / 3;

    public void paint(Graphics g) {
        System.out.println("paint once");
        g.drawImage(desk, 0, 0, null);
        g.drawImage(ball, (int) x, (int) y, null);

        x = x + 10 * Math.cos(degree);
//        System.out.println(x);
        y = y + 10 * Math.sin(degree);
//        System.out.println(y);

        if (y > 500 - 40 - 30 || y < 40 + 40) {
            degree = -degree;
        }

        if (x < 30 || x > 856 - 40 - 30) {
            degree = 3.14 - degree;
        }
    }

    // Open Window
    void launchFrame() {
        setSize(856, 500);
        setLocation(50, 50);
        setVisible(true);

        //repaint window
        while (true) {
            repaint();
            try {
                Thread.sleep(20);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Hello world!  ");
        BallGame2 game = new BallGame2();
        game.launchFrame();
    }
}
