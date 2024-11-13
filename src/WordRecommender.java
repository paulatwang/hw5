import java.io.*;
import java.util.*;

public class WordRecommender {
    ArrayList<String> dictWords;

    public WordRecommender(String dictionaryFile) {
        //Takes in the filename of the dictionary file and uses it to make recommendations
        while (true) {
            try {
                this.dictWords = new ArrayList<>();
                FileInputStream dictStream = new FileInputStream(dictionaryFile);
                Scanner dictScan = new Scanner(dictStream);

                while (dictScan.hasNext()){
                    this.dictWords.add(dictScan.next());
                }
                dictScan.close();
                break;
            } catch (FileNotFoundException e) {
                System.out.printf(Util.FILE_OPENING_ERROR);
                System.out.println(Util.DICTIONARY_PROMPT);
            }
        }
    }

    public double getSimilarity(String word1, String word2) {
        //Rank all replacements so that most "similar" word is recommended first, followed by second most similar etc.
        //Similarity based on "left-right similarity" - average of left similarity and right similarity
        word1 = word1.toLowerCase();
        word2 = word2.toLowerCase();

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
            if (Character.isLetter(letter)){ // filters out non-alphabetical characters
                letter = Character.toLowerCase(letter); // convert to lowercase
                wordSet.add(letter);
            }
        }

        // find valid replacement candidates from dictionary
        for (String dictWord : dictWords){
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
            double common = 0.0;
            try {
                common = (double) intersection.size() / union.size();
            } catch (NullPointerException e) {
                break;
            }


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
        } else {
            // get top N candidates in increasing order of similarity (least -> most)
            ArrayList<String> topCandidates = new ArrayList<>();
            while (topCandidates.size() < topN && !ranking.isEmpty()) { //included !ranking.isEmpty() (AP)
                Double topRank = ranking.lastKey();
                for (String candidate : ranking.get(topRank)) {
                    if (topCandidates.size() >= topN) {
                        break;
                    }
                    topCandidates.add(candidate); // add to the end
                }
                ranking.remove(topRank);
            }

            // return in increasing order of similarity
            return topCandidates;

        }
    }
}