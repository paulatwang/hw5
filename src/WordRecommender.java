public class WordRecommender {

    public WordRecommender(String dictionaryFile) {
        //Takes in the filename of the dictionary file and uses it to make recommendations (should be able to accept any dictionary name, not just the default provided)
        // TODO: change this!
    }
  
    public double getSimilarity(String word1, String word2) {
        //Rank all replacements so that most "similar" word is recommended first, followed by second most similar etc.
        //Similarity based on "left-right similarity" - average of left similarity and right similarity
        // TODO: change this!
        return 0.0;
    }
  
    public ArrayList<String> getWordSuggestions(String word, int tolerance, double commonPercent, int topN) {
        // Given an incorrect word, should return a list of legal word suggestions  (can assume the incorrect word will not be in the dictionary)
        // Should have a tolerance of 2, common percentage minimum of 50% and a "top N" of 4
        // Valid candidate for replacing word if difference in length is at most tolerance characters, and at least commonPercent of characters in common
        // Return topN replacement candidates in increasing order of "left-right" similarity to the misspelled word
        // TODO: change this!
        return null;
    }
  
    
  }