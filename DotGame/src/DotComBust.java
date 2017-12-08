import java.math.BigInteger;
import java.util.*;

public class DotComBust {
    private GameHelper helper = new GameHelper();
    private ArrayList<DotCom> dotComList = new ArrayList<DotCom>();
    private int numOfGuesses = 0;

    public void setUpGame() {
        DotCom one = new DotCom();
        one.setName("www.moecai.com");
        DotCom two = new DotCom();
        two.setName("www.baidu.com");
        DotCom three = new DotCom();
        three.setName("www.google.com");
        dotComList.add(one);
        dotComList.add(two);
        dotComList.add(three);

        System.out.println("This is a silly game!");
        System.out.println("You should guess the true number to hit the three dot com ");
        System.out.println("moecai.com,google.com,baidu.com");
        System.out.println("Less times, Higher score");

        for (DotCom dotComSet : dotComList) {
            ArrayList<String> locations = helper.placeDotCom(3);
            dotComSet.setLocationCells(locations);
        }
    }

    public void startPlaying() {
        while (!dotComList.isEmpty()) {
            String userGuess = helper.getUserInput("Enter a guess");
            checkUserGuess(userGuess);
        }
        finishGame();
    }

    private void checkUserGuess(String userGuess) {
        numOfGuesses++;
        String result = "miss";
        for (DotCom dotComToTest : dotComList) {
            result = dotComToTest.checkYourself(userGuess);
            if (result.equals("kill")) {
                dotComList.remove(dotComToTest);
                break;
            }
        }
        System.out.println(result);
    }

    private void finishGame() {
        System.out.println("All Dot Coms are dead! Your stock is now worthless.");
        if (numOfGuesses <= 18) {
            System.out.println("Great,it only took you " + numOfGuesses + " guesses");
        } else {
            System.out.println("It took long time,you took " + numOfGuesses + " guesses");
        }
    }

    public static void main(String[] args) {
        DotComBust game = new DotComBust();
        game.setUpGame();
        game.startPlaying();
    }
}
