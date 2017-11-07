package edu.neu.madcourse.vikaschandrashekar.scroggle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by cvikas on 2/17/2017.
 */

public class Scrambler {
    static List<Integer> zeroNeighbors = new ArrayList<>(Arrays.asList(1, 3, 4));
    static List<Integer> oneNeighbors = new ArrayList<>(Arrays.asList(0, 2, 3, 4, 5));
    static List<Integer> twoNeighbors = new ArrayList<>(Arrays.asList(1, 4, 5));
    static List<Integer> threeNeighbors = new ArrayList<>(Arrays.asList(0, 1, 4, 6, 7));
    static List<Integer> fourNeighbors = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 5, 6, 7, 8));
    static List<Integer> fiveNeighbors = new ArrayList<>(Arrays.asList(1, 2, 4, 7, 8));
    static List<Integer> sixNeighbors = new ArrayList<>(Arrays.asList(3, 4, 7));
    static List<Integer> sevenNeighbors = new ArrayList<>(Arrays.asList(3, 4, 5, 6, 8));
    static List<Integer> eightNeighbors = new ArrayList<>(Arrays.asList(4, 5, 7));

    static List<String> patterns = new ArrayList<>();
    private List<Integer> usedPlaces = new ArrayList();


    public List<String> scramble(List<String> words) {
        patterns.add("014367852");
        patterns.add("034215876");
        patterns.add("048763125");
        patterns.add("130425876");
        patterns.add("124036785");
        patterns.add("152487630");
        patterns.add("210345876");
        patterns.add("251034678");
        patterns.add("213046785");
        patterns.add("376485210");
        patterns.add("304125876");
        patterns.add("340125876");
        patterns.add("436785210");
        patterns.add("463785210");
        patterns.add("476301258");
        patterns.add("521034678");
        patterns.add("512403678");
        patterns.add("584763012");
        patterns.add("637845210");
        patterns.add("673014852");
        patterns.add("678543012");
        patterns.add("748521036");
        patterns.add("763014852");
        patterns.add("785210364");
        patterns.add("876345210");
        patterns.add("841257630");
        patterns.add("852147630");
        Random randomGenerator = new Random();
        List<String> scrambledWords = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            String s = words.get(i);
            String output = "";
            String formedWord[] = new String[9];
            int patt = randomGenerator.nextInt(27);
            while (usedPlaces.contains(patt)) {
                patt = randomGenerator.nextInt(27);
            }
            String pattern = patterns.get(patt);
            usedPlaces.add(patt);
            for (int j = 0; j < 9; j++) {
                int x = Integer.parseInt(String.valueOf(pattern.charAt(j)));
                formedWord[x] = String.valueOf(s.charAt(j));
            }
            for (int t = 0; t < 9; t++) {
                output = output + formedWord[t];
            }
            scrambledWords.add(output);
        }
        /*
        Random randomGenerator = new Random();
        List<String> scrambledWords = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            String formedWord[] = new String[9];
            int f = randomGenerator.nextInt(9);
            String output = "";
            usedPlaces.add(f);
            String s = words.get(i);
            formedWord[f] = String.valueOf(s.charAt(0));
            for(int j=1; j<9; j++) {
                f = getNextPlace(f);
                if (usedPlaces.contains(f)) {
                    j--;
                } else {
                    formedWord[f]= String.valueOf(s.charAt(j));
                    usedPlaces.add(f);
                }
            }
            for (int t=0; t<9; t++){
                output = output+formedWord[t];
            }
            usedPlaces.clear();
            scrambledWords.add(output);
        }
        return scrambledWords;
    */
        return scrambledWords;
    }

    public int getNextPlace(int current) {
        int next = 0;
        Random r = new Random();
        switch (current) {
            case 0:
                next = r.nextInt(zeroNeighbors.size());
                next = zeroNeighbors.get(next);
                if (usedPlaces.contains(next))
                    next = 0;
                break;
            case 1:
                next = r.nextInt(oneNeighbors.size());
                next = oneNeighbors.get(next);
                if (usedPlaces.contains(next))
                    next = 1;
                break;
            case 2:
                next = r.nextInt(twoNeighbors.size());
                next = twoNeighbors.get(next);
                if (usedPlaces.contains(next))
                    next = 2;
                break;
            case 3:
                next = r.nextInt(threeNeighbors.size());
                next = threeNeighbors.get(next);
                if (usedPlaces.contains(next))
                    next = 3;
                break;
            case 4:
                next = r.nextInt(fourNeighbors.size());
                next = fourNeighbors.get(next);
                if (usedPlaces.contains(next))
                    next = 4;
                break;
            case 5:
                next = r.nextInt(fiveNeighbors.size());
                next = fiveNeighbors.get(next);
                if (usedPlaces.contains(next))
                    next = 5;
                break;
            case 6:
                next = r.nextInt(sixNeighbors.size());
                next = sixNeighbors.get(next);
                if (usedPlaces.contains(next))
                    next = 6;
                break;
            case 7:
                next = r.nextInt(sevenNeighbors.size());
                next = sevenNeighbors.get(next);
                if (usedPlaces.contains(next))
                    next = 7;
                break;
            case 8:
                next = r.nextInt(eightNeighbors.size());
                next = eightNeighbors.get(next);
                if (usedPlaces.contains(next))
                    next = 8;
                break;
        }
        return next;
    }
}
