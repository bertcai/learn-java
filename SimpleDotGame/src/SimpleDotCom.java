public class SimpleDotCom {
    int[] locationCells;
    int numOfHtis = 0;
    public void setLocationCells(int[] locs){
        locationCells = locs;
    }

    public String checkYourself(String stringGuess){
        int guess = Integer.parseInt(stringGuess);
        String result = "miss";
        for(int cell: locationCells){
            if(cell == guess){
                result = "hit";
                numOfHtis++;
                break;
            }
        }
        if(numOfHtis == locationCells.length){
            result = "kill";
        }
        System.out.println(result);
        return result;
    }
}
