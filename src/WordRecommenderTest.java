import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WordRecommenderTest {

    WordRecommender similarityTest = new WordRecommender("engDictionary.txt");

    //getSimilarity tests

    //Test when the two words are the same
    @Test
    public void testSimilarityIdentical() {
        String input1 = "identical";
        String input2 = "identical";
        double expected = input1.length();
        double actual = similarityTest.getSimilarity(input1, input2);
        assertEquals(expected, actual, "Similarity should be length of the word.");
    }

    //Test when the two words are the same but with capitalization difference
    @Test
    public void testSimilarityCapitalization() {
        String input1 = "identical";
        String input2 = "Identical";
        double expected = input1.length();
        double actual = similarityTest.getSimilarity(input1, input2);
        assertEquals(expected, actual, "Similarity should not change based on capitalization.");
    }

    //Test when the two words are completely different
    @Test
    public void testSimilarityDifferent() {
        String input1 = "identical";
        String input2 = "different";
        double expected = 0;
        double actual = similarityTest.getSimilarity(input1, input2);
        assertEquals(expected, actual, "Similarity should be 0.");
    }

    //Test when two words are partially similar (left)
    @Test
    public void testSimilarityLeft() {
        String input1 = "apple";
        String input2 = "apply";
        double expected = 4.0;
        double actual = similarityTest.getSimilarity(input1, input2);
        assertEquals(expected, actual, "Similarity should be 4.0.");
    }

    //Test when two words are partially similar (right)
    @Test
    public void testSimilarityRight() {
        String input1 = "apple";
        String input2 = "maple";
        double expected = 3.0;
        double actual = similarityTest.getSimilarity(input1, input2);
        assertEquals(expected, actual, "Similarity should be 3.0.");
    }

    //Test when two words are of different length

    //Example 1 provided in HW outline: Oblige
    @Test
    public void testSimilarityOblige() {
        String input1 = "oblige";
        String input2 = "oblivion";
        double expected = 2.5;
        double actual = similarityTest.getSimilarity(input1, input2);
        assertEquals(expected, actual, "Similarity should be 2.5.");
    }

    //Example 2 provided in HW outline: Aghast
    @Test
    public void testSimilarityAghast() {
        String input1 = "aghast";
        String input2 = "gross";
        double expected = 1.5;
        double actual = similarityTest.getSimilarity(input1, input2);
        assertEquals(expected, actual, "Similarity should be 1.5.");
    }

    //getWordSuggestions tests

    //Set up and use a sample dictionary
    WordRecommender suggestionsTest = new WordRecommender("sampleDictionary.txt");
    int tolerance = 2;
    double commonPercent = 0.5;
    int topN = 4;

    //Test when there are no suggestions
    @Test
    public void testNoSuggestions(){
        String word = "app";
        ArrayList<String> expected = null;
        ArrayList<String> actual = suggestionsTest.getWordSuggestions(word, tolerance, commonPercent, topN);
        assertEquals(expected, actual, "Suggestion should be null");
    }

    //Test when there is only one suggestion (i.e. less than topN)
    @Test
    public void testOneSuggestion(){
        String word = "whee";
        ArrayList<String> expected = new ArrayList<String>(Arrays.asList("wheel"));
        ArrayList<String> actual = suggestionsTest.getWordSuggestions(word, tolerance, commonPercent, topN);
        assertEquals(expected, actual, "Suggestion should be white");
    }

    //Test tolerance
    @Test
    public void testLowTolerance(){
        String word = "bun";
        ArrayList<String> expected = new ArrayList<String>(Arrays.asList("ban"));
        ArrayList<String> actual = suggestionsTest.getWordSuggestions(word, 0, commonPercent, topN);
        assertEquals(expected, actual, "Suggestion should be ban only");
    }

    @Test
    public void testHighTolerance(){
        String word = "bun";
        HashSet<String> expected = new HashSet<String>(Arrays.asList("bunny", "ban"));
        ArrayList<String> actualOutput = suggestionsTest.getWordSuggestions(word, tolerance, commonPercent, topN);
        HashSet<String> actual = new HashSet<>();
        for (String output : actualOutput) {
            actual.add(output);
        }
        assertEquals(expected, actual, "Suggestion should be ban and bunny");
    }

    //Test commonPercent
    @Test
    public void testHighCommonPercent(){
        String word = "bun";
        ArrayList<String> expected = new ArrayList<String>(Arrays.asList("bunny"));
        ArrayList<String> actual = suggestionsTest.getWordSuggestions(word, tolerance, 0.7, topN);
        assertEquals(expected, actual, "Suggestion should be bunny only");
    }

    @Test
    public void testLowCommonPercent(){
        String word = "bun";
        HashSet<String> expected = new HashSet<>(Arrays.asList("ban", "bunny", "gum", "rut"));
        ArrayList<String> actualOutput = suggestionsTest.getWordSuggestions(word, tolerance, 0.2, topN);
        HashSet<String> actual = new HashSet<>();
        for (String output : actualOutput) {
            actual.add(output);
        }
        assertEquals(expected, actual, "Suggestion should be ban, bunny, gum, rut");
    }

    //Test topN suggestions
    @Test
    public void testTopN(){
        String word = "bun";
        ArrayList<String> expected = new ArrayList<String>(Arrays.asList("ban"));
        ArrayList<String> actual = suggestionsTest.getWordSuggestions(word, tolerance, 0.2, 1);
        assertEquals(expected, actual, "Suggestion should be ban only");
    }

    //Test when the word provided is already in the dictionary
    @Test
    public void testSameWord(){
        String word = "pop";
        ArrayList<String> expected = new ArrayList<String>(Arrays.asList("pop"));
        ArrayList<String> actual = suggestionsTest.getWordSuggestions(word, tolerance, commonPercent, topN);
        assertEquals(expected, actual, "Suggestion should be pop only");
    }

    //Test that the most similar word is the first option
    @Test
    public void testSimilarityOrder() {
        String word = "bun";
        int expected = 0;
        ArrayList<String> actualOutput = suggestionsTest.getWordSuggestions(word, tolerance, commonPercent, topN);
        double mostSimilar = 0;
        int wordSimilarityIndex = 0;
        for (int i = 0; i < actualOutput.size(); i++) {
            double wordSimilarity = suggestionsTest.getSimilarity(word, actualOutput.get(i));
            if (wordSimilarity > mostSimilar) {
                mostSimilar = wordSimilarity;
                wordSimilarityIndex = i;
            }
        }
        int actual = wordSimilarityIndex;
        assertEquals(expected, actual, "Index should be 0 for both");
    }

    //Test when word contains non-alphabetical characters
    @Test
    public void testNonAlphabeticalWord(){
        String word = "p3p";
        ArrayList<String> expected = new ArrayList<String>(Arrays.asList("pop"));
        ArrayList<String> actual = suggestionsTest.getWordSuggestions(word, tolerance, commonPercent, topN);
        assertEquals(expected, actual, "Suggestion should be pop only");
    }

    //Test when word contains capitalized characters
    @Test
    public void testCapitalWord(){
        String word = "POP";
        ArrayList<String> expected = new ArrayList<String>(Arrays.asList("pop"));
        ArrayList<String> actual = suggestionsTest.getWordSuggestions(word, tolerance, commonPercent, topN);
        assertEquals(expected, actual, "Suggestion should be pop only");
    }


}
