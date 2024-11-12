import java.io.*;
import java.util.*;

public class WordRecommender {
    private FileInputStream inputStream;

    public WordRecommender(String dictionaryFile) {
        //Takes in the filename of the dictionary file and uses it to make recommendations
        // (should be able to accept any dictionary name, not just the default provided)
//        Scanner s = new Scanner(System.in);
        while (true) {
            try {
                this.inputStream = new FileInputStream(dictionaryFile);
                break; // exit loop if file is successfully found and opened
            } catch (FileNotFoundException e) {
                System.err.println("There was an error in opening that file.");
                System.out.println("Please enter the name of a file to use as a dictionary.");
//                dictionaryFile = s.nextLine(); // Prompt user for a new filename
            }
        }

        System.out.println("Using the dictionary at '" + dictionaryFile + "'.");
//        s.close();
    }

    public double getSimilarity(String word1, String word2) {
        //Rank all replacements so that most "similar" word is recommended first, followed by second most similar etc.
        //Similarity based on "left-right similarity" - average of left similarity and right similarity
        double left = 0.0;
        double right = 0.0;
        String shorter;
        String longer;

        // get shorter and longer word
        if (word1.length() <= word2.length()) {
            shorter = word1;
            longer = word2;
        }
        else {
            shorter = word2;
            longer = word1;
        }

        // get similarity
        String reversedShort = new StringBuilder(shorter).reverse().toString();
        String reversedLong = new StringBuilder(longer).reverse().toString();
        for (int i = 0; i < shorter.length(); i++){
            if (shorter.charAt(i) == longer.charAt(i)) {
                left ++;
            }
            if (reversedShort.charAt(i) == reversedLong.charAt(i)){
                right ++;
            }
        }

        return (left + right) / 2.0;
    }

    public ArrayList<String> getWordSuggestions(String word, int tolerance, double commonPercent, int topN) {
        /*
         * Given an incorrect word, should return a list of legal word suggestions
         *   - Inputs: tolerance = 2, commonPercent = 0.5, topN = 4
         *   - Valid candidates: lengthDiff <= tolerance && common >= commonPercent
         *   - Return: topN replacement candidates in increasing order of "left-right" similarity to the misspelled word
         * */

        ArrayList<String> candidates = new ArrayList<>();

        // add letters in word to wordSet
        HashSet<Character> wordSet = new HashSet<>();
        for (char letter : word.toCharArray()) {
            wordSet.add(letter);
        }


        // find valid replacement candidates from dictionary
            Scanner s1 = new Scanner(this.inputStream);
            while (s1.hasNextLine()) {
            String dictWord = s1.nextLine();

            // calculate length
            int lengthDiff = Math.abs(dictWord.length() - word.length());

            // add letters in dictWord to dictSet
            HashSet<Character> dictSet = new HashSet<>();
            for (char letter : dictWord.toCharArray()) {
                dictSet.add(letter);
            }

            // compare characters in common
            HashSet<Character> intersection = new HashSet<>(wordSet);
            intersection.retainAll(dictSet);
            HashSet<Character> union = new HashSet<>(wordSet);
            union.addAll(dictSet);
            double common = (double) intersection.size() / union.size();

            // add valid candidates to array
            if (lengthDiff <= tolerance && common >= commonPercent) {
                candidates.add(dictWord);
            }

        }

        // rank replacements based on similarity
        TreeMap<Double, ArrayList<String>> ranking = new TreeMap<>();
        for (String candidate : candidates) {
            double similarityScore = getSimilarity(word, candidate);
            ranking.putIfAbsent(similarityScore, new ArrayList<>()); // if no rank exists, create one
            ranking.get(similarityScore).add(candidate); // add candidate to rank
        }

        // if there are no replacements (AP)
        if (ranking.isEmpty()) {
            return null;
        }

        // get top N candidates in increasing order of similarity (least -> most)
        ArrayList<String> topCandidates = new ArrayList<>();
        while (topCandidates.size() < topN && !ranking.isEmpty()) { //Added !ranking.isEmpty() (AP)
                Double topRank = ranking.lastKey();
                for (String candidate : ranking.get(topRank)) {
                    topCandidates.add(0, candidate);
                }
                ranking.remove(topRank);
            }

        // return in increasing order of similarity
        int size = topCandidates.size();
        return new ArrayList<>(topCandidates.subList(size - 4, size));
    }
}