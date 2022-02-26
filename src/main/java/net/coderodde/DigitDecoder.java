package net.coderodde;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;

public class DigitDecoder {

    private static final Set<Character> CHAR_ALPHABET = new HashSet<>();
    
    private static final int ALPHABET_SIZE                = 26;
    private static final char SEPARATOR_CHARACTER         = '#';
    private static final char COUNT_VALUE_BEGIN_CHARACTER = '(';
    private static final char COUNT_VALUE_END_CHARACTER   = ')';
    
    static {
        for (char ch = '0'; ch <= '9'; ++ch) {
            CHAR_ALPHABET.add(ch);
        }
        
        CHAR_ALPHABET.add(SEPARATOR_CHARACTER);
        CHAR_ALPHABET.add(COUNT_VALUE_BEGIN_CHARACTER);
        CHAR_ALPHABET.add(COUNT_VALUE_END_CHARACTER);
    }
   
    
    private final char[] inputCharacters;
    private final CharacterFIFOBuffer characterBuffer = 
            new CharacterFIFOBuffer();
    
    private final int[] counterArray = new int[ALPHABET_SIZE];
    private int currentIndex;
    private boolean readingCounter;
    private int previousInteger;
    
    private DigitDecoder(String code) {
        this.inputCharacters = code.toCharArray();
        checkInputCharacters();
    }
    
    private void checkInputCharacters() {
        for (char ch : inputCharacters) {
            if (!CHAR_ALPHABET.contains(ch)) {
                throw new IllegalArgumentException(
                        "Character '" + ch + "' is not accepted.");
            }
        }
    }
    
    private void processRepeat() {
        assert currentIndex > 0 :
                "The code string begins with " + COUNT_VALUE_BEGIN_CHARACTER + ".";
        
        assert inputCharacters[currentIndex] == COUNT_VALUE_BEGIN_CHARACTER : 
               "Current character is '" 
                + inputCharacters[currentIndex]
                + "'. '" 
                + COUNT_VALUE_BEGIN_CHARACTER 
                + "' expected.";
        
        
        int characterCount = readCount(currentIndex);
        
        if (characterBuffer.size() == CharacterFIFOBuffer.BUFFER_SIZE) {
            if (characterBuffer.getCharacter(2) == SEPARATOR_CHARACTER) {
                int outputArrayIndex = getCharacterBufferIndexDoubleChar();
                counterArray[outputArrayIndex] += characterCount;
            } else {
                int index1 = getCharacterBufferIndexSingleFirstChar();
                int index2 = getCharacterBufferIndexSingleSecondChar();
                int index3 = getCharacterBufferIndexSingleThirdChar();
                
                counterArray[index1]++;
                counterArray[index2]++;
                counterArray[index3] += characterCount; 
            }
        } else {
            switch (characterBuffer.size()) {
                case 1:
                    int outputArrayIndex = 
                            (int)(characterBuffer.getCharacter(0) - '0') - 1;

                    counterArray[outputArrayIndex] += characterCount;
                    break;

                case 2:
                    // Dump the left character:
                    outputArrayIndex = 
                            (int)(characterBuffer.getCharacter(0) - '0') - 1;
                    
                    counterArray[outputArrayIndex]++;
                    
                    // Dump the right character. The one right before the count
                    // value:
                    outputArrayIndex = 
                            (int)(characterBuffer.getCharacter(1) - '0') - 1;
                    
                    counterArray[outputArrayIndex] += characterCount;
                    break;

                default:
                    assert characterBuffer.size() == 1 ||
                           characterBuffer.size() == 2 : 
                            "Character buffer size is: " 
                            + characterBuffer.size() 
                            + ". Must be 1 or 2.";
            }
        }
        
        currentIndex++;
        characterBuffer.clear();
    }
    
    private static int getOutputArrayIndex(char digitCharacterHi,
                                           char digitCharacterLo) {
        assert digitCharacterHi != 1 && digitCharacterHi != 2 :
                "The high order digit is " 
                + digitCharacterHi 
                + ". Should be 1 or 2.";
        
        assert Character.isDigit(digitCharacterLo) :
                "The character " 
                + digitCharacterLo 
                + " is not a digit. Must be 0 - 9.";
        
        int index    = (int)(digitCharacterHi - '0') * 10;
        return index + (int)(digitCharacterLo - '0');
    }
    
    private void processOnEmptyBuffer() {
        char currentCharacter = inputCharacters[currentIndex];
        
        switch (currentCharacter) {
            case SEPARATOR_CHARACTER:
                throw new IllegalArgumentException(
                        "The code cannot start from '" 
                                + SEPARATOR_CHARACTER
                                + "'.");

            case COUNT_VALUE_BEGIN_CHARACTER:
                throw new IllegalArgumentException(
                        "The code cannot start from '" 
                                + COUNT_VALUE_BEGIN_CHARACTER 
                                + "'-");

            case COUNT_VALUE_END_CHARACTER:
                throw new IllegalArgumentException(
                        "The code cannot start from '" 
                                + COUNT_VALUE_END_CHARACTER 
                                + "'.");
        }

        characterBuffer.addCharacter(currentCharacter);
    }
    
    private void compute() {
        checkInputCharacters();
        
        mainLoop:
        while (currentIndex < inputCharacters.length) {
            char currentCharacter = inputCharacters[currentIndex];
            
            if (characterBuffer.size() == 0) {
                processOnEmptyBuffer();
            } else if (currentCharacter == COUNT_VALUE_BEGIN_CHARACTER) {
                processRepeat();
            } else if (currentCharacter == COUNT_VALUE_END_CHARACTER) {
                throw new IllegalStateException("fdsfds");
                // Omit!
            } else if (currentCharacter == SEPARATOR_CHARACTER) {
                switch (characterBuffer.size()) {
                    case 1:
                    case 2:
                        throw new IllegalStateException("no hell!");
                        
                    case 3: {
                        
                        if (currentIndex < inputCharacters.length - 1 && 
                                inputCharacters[currentIndex + 1]
                                == SEPARATOR_CHARACTER) {
                            throw new IllegalArgumentException(
                                    "Duplicate '" 
                                            + SEPARATOR_CHARACTER 
                                            + "' at index " 
                                            + (currentIndex + 1));
                        
                        }
                        
                        if (currentIndex < inputCharacters.length - 1) {
                            if (inputCharacters[currentIndex + 1] 
                                    == COUNT_VALUE_BEGIN_CHARACTER) {
                                processRepeat();
                            }
                        } else {
                            System.out.println("hello there");
                        }
                        
                        break;
                    }
                        
                    default:
                        assert false : 
                                "Bad character buffer size: " 
                                + characterBuffer.size()
                                + ".";
                }
            } else {
                if (characterBuffer.size() == 3) {
                    int index = getCharacterBufferIndexSingleFirstChar();
                    counterArray[index]++;
                }
                
                characterBuffer.addCharacter(currentCharacter);
            }
            
            ++currentIndex;
        }
        
        // Clean up:
        switch (characterBuffer.size()) {
            case 3:
                
                if (characterBuffer.getCharacter(2) == SEPARATOR_CHARACTER) {
                    int index = getCharacterBufferIndexDoubleChar();
                    counterArray[index]++;
                } else {
                    int index1 = getCharacterBufferIndexSingleFirstChar();
                    int index2 = getCharacterBufferIndexSingleSecondChar();
                    int index3 = getCharacterBufferIndexSingleThirdChar();
                    
                    counterArray[index1]++;
                    counterArray[index2]++;
                    counterArray[index3]++;
                }
                
                break;
                
            case 2:
                
                int index1 = getCharacterBufferIndexSingleFirstChar();
                int index2 = getCharacterBufferIndexSingleSecondChar();
                
                counterArray[index1]++;
                counterArray[index2]++;
                break;
                
            case 1:
                
                int index = getCharacterBufferIndexSingleFirstChar();
                counterArray[index]++;
                break;
        }
    }
    
    public static int[] decode(String code) {
        DigitDecoder alphaDecoder = 
                new DigitDecoder(
                        Objects.requireNonNull(
                                code, 
                                "The input code is null."));
        
        alphaDecoder.compute();
        return alphaDecoder.counterArray;
    }
    
    private static void checkIndex(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("index is negative: " + index);
        }
        
        if (index >= ALPHABET_SIZE) {
            throw new IllegalArgumentException(
                    "index is too large: " 
                            + index 
                            + ", Must be at most " 
                            + (ALPHABET_SIZE - 1) 
                            + ".");
        }
    }
    
    private int getCharacterBufferIndexDoubleChar() {
        int index = (characterBuffer.getCharacter(0) - '0') * 10;
        return index + (characterBuffer.getCharacter(1) - '0') - 1;
    }
    
    private int getCharacterBufferIndexSingleFirstChar() {
        return (int)(characterBuffer.getCharacter(0) - '0') - 1;
    }
    
    private int getCharacterBufferIndexSingleSecondChar() {
        return (int)(characterBuffer.getCharacter(1) - '0') - 1;
    }
    
    private int getCharacterBufferIndexSingleThirdChar() {
        return (int)(characterBuffer.getCharacter(2) - '0') - 1;
    }
    
    private int readCount(int separatorBeginIndex) {
        // Point to most significant counter digit:
        int index = separatorBeginIndex + 1;
        int count = 0;
        
        while (index < inputCharacters.length
                &&inputCharacters[index] != COUNT_VALUE_END_CHARACTER) {
            
            char digit = inputCharacters[index++];
            count *= 10;
            count += (int)(digit - '0');
            this.currentIndex++;
        }
        
        assert index < inputCharacters.length : 
                "No trailing " + COUNT_VALUE_END_CHARACTER + " at the end of the code.";
        
        assert inputCharacters[index] == COUNT_VALUE_END_CHARACTER :
                "Expected '" + COUNT_VALUE_END_CHARACTER + "'. Read '" + 
                inputCharacters[index] + "'.";
        
        return count;
    }
//    
//    public static void main(String[] args) {
//        boolean test = true;
//        
//        if (test) {
//            String code = "12345(100)8920#(34)";
//            int[] array = decode(code);
//            System.out.println(Arrays.toString(array));
//            System.out.println(frequency(code));
//        } else {
//            Scanner scanner = new Scanner(System.in);
//
//            while (scanner.hasNextLine()) {
//                String code = scanner.nextLine();
//                System.out.println(frequency(code));
//                System.out.println(Arrays.toString(decode(code)));
//            }
//        }
//    }
    
    public static List<Integer> frequency(String s) {
        List<Integer> freq = new ArrayList<>();
        int[] result = new int[26];
        int length = s.length();
        int i = 0;
        while (i < length) {
            int val = 0;
            if (i + 2 >= length || s.charAt(i + 2) != '#') {
                val = s.charAt(i) - '0';
                result[val - 1]++;
                i++;
            } else if (s.charAt(i + 2) == '#') {
                val = (s.charAt(i) - '0') * 10 + (s.charAt(i + 1) - '0');
                result[val - 1]++;
                i = i + 3;
            }
            if (i < length) {
                if (s.charAt(i) == '(') {
                    int fr = 0;
                    i++;
                    while (s.charAt(i) != ')') {
                        fr = fr * 10 + (s.charAt(i) - '0');
                        i++;
                    }
                    result[val - 1] += fr - 1;
                    i++;
                }
            }
        }

        for (int res : result) {
            freq.add(res);
        }
        return freq;
    }
}
        