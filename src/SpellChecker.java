import java.io.*;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Scanner;

public class SpellChecker {
    private static String dictionaryName;
    private static String fileName;
    private static String outputFileName;

    public SpellChecker() {
    }
  
    public static void start() {
        // Step1: READ DICTIONARY FILE
        Scanner input = new Scanner(System.in);

        // open and read dictionary into hashset
        HashSet<String> dictionaryWords = new HashSet<>();
        while (true) {
            System.out.printf(Util.DICTIONARY_PROMPT);
            dictionaryName = input.next(); // store dictionary name
            try {
                FileInputStream dictionaryInputStream = new FileInputStream(dictionaryName);
                Scanner dictionaryInputReader = new Scanner(dictionaryInputStream);
                System.out.printf(Util.DICTIONARY_SUCCESS_NOTIFICATION, dictionaryName);

                // read dictionary words into hashset
                while (dictionaryInputReader.hasNext()){
                    dictionaryWords.add(dictionaryInputReader.next());
                }
                dictionaryInputReader.close();
                break;
            } catch (FileNotFoundException e) {
                System.out.printf(Util.FILE_OPENING_ERROR);
            }
        }

        // Step 2: OPEN FILE FOR SPELLCHECKING
        // open file for spellchecking and create output file
        while (true) {
            System.out.printf(Util.FILENAME_PROMPT);
            fileName = input.next();
            try {
                FileInputStream fileInputStream = new FileInputStream(fileName);
                outputFileName = fileName.substring(0, fileName.length() - 4) + "_chk.txt";
                FileOutputStream output = new FileOutputStream(outputFileName);
                PrintWriter writer = new PrintWriter(output);
                Scanner fileInputReader = new Scanner(fileInputStream);

                System.out.printf(Util.FILE_SUCCESS_NOTIFICATION, fileName, outputFileName);

                // STEP 3: DO SPELL CHECKING
                // initialize word recommender and parameters for its methods
                WordRecommender dictionaryList = new WordRecommender(dictionaryName);
                int tolerance = 2;
                double commonPercent = 0.5;
                int topN = 4;

                // read words from file
                while (fileInputReader.hasNext()) {
                    String word = fileInputReader.next();
                    if (!dictionaryWords.contains(word)) {
                        System.out.printf(Util.MISSPELL_NOTIFICATION, word);
                        ArrayList<String> suggestions = dictionaryList.getWordSuggestions(word, tolerance, commonPercent, topN);

                        // if no suggestions
                        if (suggestions.isEmpty()) {
                            System.out.printf(Util.NO_SUGGESTIONS);
                            System.out.printf(Util.TWO_OPTION_PROMPT);

                            // prompt for 'a' or 't'
                            while (true){
                                String letterChoice = input.next();
                                if (letterChoice.equals("a")) {
                                    break;
                                }
                                if (letterChoice.equals("t")){
                                    System.out.printf(Util.MANUAL_REPLACEMENT_PROMPT);
                                    word = input.next(); // replace with next user input
                                    break;
                                }
                                System.out.printf(Util.INVALID_RESPONSE);
                            }


                        } else { // if suggestions exist
                            System.out.printf(Util.FOLLOWING_SUGGESTIONS);
                            for (int i = 0; i < topN; i++) {
                                System.out.printf(Util.SUGGESTION_ENTRY, i + 1, suggestions.get(i)); // print suggestions
                            }
                            System.out.printf(Util.THREE_OPTION_PROMPT);

                            // prompt for 'r', 'a', 't'
                            while (true){
                                String letterChoice = input.next();
                                if (letterChoice.equals("r")) {
                                    System.out.printf(Util.AUTOMATIC_REPLACEMENT_PROMPT);
                                    while (true) {
                                        try {
                                            int intChoice = input.nextInt();
                                            if (intChoice > 0 && intChoice < suggestions.size()) {
                                                word = suggestions.get(intChoice - 1); // replace with selection
                                            } else {
                                                System.out.printf(Util.INVALID_RESPONSE);
                                            }
                                            break;
                                        } catch (InputMismatchException e) {
                                            System.out.printf(Util.INVALID_RESPONSE);
                                            input.next();
                                        }
                                    }
                                    break;
                                }
                                if (letterChoice.equals("a")) {
                                    break; // add as is
                                }
                                if (letterChoice.equals("t")) {
                                    System.out.printf(Util.MANUAL_REPLACEMENT_PROMPT);
                                    word = input.next(); // replace with next user input
                                    break;
                                }
                                System.out.printf(Util.INVALID_RESPONSE);
                            }
                        }


                    }
                    // write to file
                    writer.printf("%s ", word);
                }
                // file complete!
                writer.close();
                fileInputReader.close();
                System.out.printf("Spellchecking complete. Output written to %s%n", outputFileName);
                break;
            } catch (FileNotFoundException e) {
                System.out.printf(Util.FILE_OPENING_ERROR);
            }
        }
        input.close();
        }
  }
