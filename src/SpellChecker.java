import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class SpellChecker {

    public SpellChecker() {
      // TODO: You can modify the body of this constructor,
      // or you can leave it blank. You must keep the signature, however.
    }
  
    public static void start() {
      // TODO: You can modify the body of this method,
      // or you can leave it blank. You must keep the signature, however.
        String dictionaryName;
        String fileName;
        FileInputStream dictionaryInputStream;
        FileInputStream fileInputStream;

        /*
         * Step1: prompt the user for filename for dictionary
         * - if file doesn't exist, repeatedly prompt
         * - if file does exist, print name
         */

        Scanner input = new Scanner(System.in);

        while (true) {
            System.out.printf(Util.DICTIONARY_PROMPT);
            dictionaryName = input.next();
            try {
                dictionaryInputStream = new FileInputStream(dictionaryName);
                System.out.printf(Util.DICTIONARY_SUCCESS_NOTIFICATION, dictionaryName);
                break;
            } catch (FileNotFoundException e) {
                System.out.printf(Util.FILE_OPENING_ERROR);
            }
        }


            /*
             *  Step 2: prompt the user for filename to be spellchecked
             * -  if file doesn't exist, repeatedly prompt
             * - if file does exist, print name of output file
             */


        while (true) {
            System.out.printf(Util.FILENAME_PROMPT);
            fileName = input.next();
            try {
                fileInputStream = new FileInputStream(fileName);
                System.out.printf(Util.FILE_SUCCESS_NOTIFICATION, fileName, fileName + "_check.txt");
                break;
            } catch (FileNotFoundException e) {
                System.out.printf(Util.FILE_OPENING_ERROR);
            }
        }

        FileOutputStream output = null;
        try {
            output = new FileOutputStream(fileName + "_check.txt");
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
        PrintWriter writer = new PrintWriter(output);

            HashSet<String> dictionaryWords = new HashSet<>();
            Scanner dictionaryInputReader = new Scanner(dictionaryInputStream);
            while (dictionaryInputReader.hasNext()){
                String word = dictionaryInputReader.next();
                dictionaryWords.add(word);
            }
            dictionaryInputReader.close();

            Scanner fileInputReader = new Scanner(fileInputStream);
            Scanner inputLetter = new Scanner(System.in);
            WordRecommender dictionaryList = new WordRecommender(dictionaryName);
            int tolerance = 2;
            double commonPercent = 0.5;
            int topN = 4;

            while (fileInputReader.hasNext()) {
                String word = fileInputReader.next();
                if (!dictionaryWords.contains(word)) {
                    System.out.printf(Util.MISSPELL_NOTIFICATION, word);
                    System.out.printf(Util.FOLLOWING_SUGGESTIONS);
                    ArrayList<String> suggestions = dictionaryList.getWordSuggestions(word, tolerance, commonPercent, topN);

                    if (suggestions == null) {
                        System.out.printf(Util.NO_SUGGESTIONS);
                        System.out.printf(Util.TWO_OPTION_PROMPT);
                    } else {
                        for (int i = 0; i < topN; i++) {
                            System.out.printf(Util.SUGGESTION_ENTRY, i + 1, suggestions.get(i));

                        }
                        System.out.printf(Util.THREE_OPTION_PROMPT);
                    }

                    String letterChoice = input.next();

                    if (letterChoice.equals("r")) {
                        System.out.printf(Util.AUTOMATIC_REPLACEMENT_PROMPT);
                        int replacement = input.nextInt();
                        word = suggestions.get(replacement - 1);
                    }
                    if (letterChoice.equals("a")) {
                        word = word;
                    }
                    if (letterChoice.equals("t")) {
                        System.out.printf(Util.MANUAL_REPLACEMENT_PROMPT);
                        word = input.next();
                    }
                    if (!letterChoice.equals("r")|| !letterChoice.equals("a") || !letterChoice.equals("t")) {
                        System.out.printf(Util.INVALID_RESPONSE);
                    }
                }
                writer.printf("%s ", word);
            }
        fileInputReader.close();
        input.close();
        writer.close();

            /*
             * Step 3: Do spell checking
             * - iterate through each word in the spellchecked file
             * - if the word is in the dictionary file, add to output file
             * - if the word is not in dictionary file, prompt user to select 'r', 'a', or 't'
             *   - if 'r':
             *       - identify suggested words for replacement (call WordRecommender)
             *       - prompt user to select replacement, repeatedly prompt if input invalid
             *       - replace word in output file
             *       - if no suggestions available, then prompt user to reselect 'a' or 't'
             *   - if 'a':
             *       - leave misspelled word unchanged
             *       - add word to output file
             *   - if 't':
             *       - prompt user for new spelling
             *       - replace word in output file
             * */

        }

    // You can of course write other methods as well.
  }
