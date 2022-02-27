package net.coderodde;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import static net.coderodde.DigitDecoder.frequency;

public final class DigitDecoderV2 {
    
    private static final int ALPHABET_SIZE                = 26;
    private static final char SEPARATOR_CHARACTER         = '#';
    private static final char COUNT_VALUE_BEGIN_CHARACTER = '(';
    private static final char COUNT_VALUE_END_CHARACTER   = ')';
    private static final Set<Character> ALPHABET_SET      = new HashSet<>();
    
    static {
        for (char ch = '0'; ch <= '9'; ++ch) {
            ALPHABET_SET.add(ch);
        }
        
        ALPHABET_SET.add(SEPARATOR_CHARACTER);
        ALPHABET_SET.add(COUNT_VALUE_BEGIN_CHARACTER);
        ALPHABET_SET.add(COUNT_VALUE_END_CHARACTER);
    }
    
    private final CharacterFIFOBuffer buffer = new CharacterFIFOBuffer();
    private final int[] decodedInts          = new int[ALPHABET_SIZE];
    private final char[] codeCharacters;
    private int currentIndex; // 0 by default
    
    public static int[] decode(String code) {
        Objects.requireNonNull(code, "The input code is null.");
        DigitDecoderV2 decoder = new DigitDecoderV2(code.toCharArray());
        decoder.decode();
        return decoder.decodedInts;
    }
    
    private DigitDecoderV2(char[] inputCode) {
        codeCharacters = inputCode;
    }
    
    private void loadBuffer() {
        while (buffer.size() < CharacterFIFOBuffer.BUFFER_SIZE 
                && currentIndex < codeCharacters.length) {
            buffer.addCharacter(codeCharacters[currentIndex++]);
        }
        
        checkIsDigit(buffer.getCharacter(0));
    }
    
    private void decode() {
        // Preload the character buffer:
        loadBuffer();
        
        while (currentIndex < codeCharacters.length) {
            switch (buffer.size()) {
                case 4:
                    processBuffer();
                    break;
                    
                case 3:
                case 2:
                case 1:
                case 0:
                    buffer.addCharacter(codeCharacters[currentIndex]);
                    break;
            }
            
            currentIndex++;
        }
        
        // Discharge the tail:
        dischargeTail();
    }    
    
    private void dischargeTail() {
        switch (buffer.size()) {
            case 4:
                discharge4();
                break;
                
            case 3:
                discharge3();
                break;
                
            case 2:
                discharge2();
                break;
                
            case 1:
                discharge1();
                break;
                
            case 0:
                break;
                
            default:
                throw new IllegalStateException("Should nog get here.");
        }
    }
    
    private void discharge3() {
        if (buffer.getCharacter(2) == SEPARATOR_CHARACTER) {
            checkIsDigit(buffer.getCharacter(0));
            checkIsDigit(buffer.getCharacter(1));
            
            int index = getDoubleDigitIndex();
            decodedInts[index]++;
        } else {
            checkIsDigit(buffer.getCharacter(0));
            checkIsDigit(buffer.getCharacter(1));
            checkIsDigit(buffer.getCharacter(2));
            
            int index1 = getSingleDigitFirstIndex();
            int index2 = getSingleDigitSecondIndex();
            int index3 = getSingleDigitThirdIndex();
            
            decodedInts[index1]++;
            decodedInts[index2]++;
            decodedInts[index3]++;
        }
    }
    
    private void discharge2() {
        isLegalDigitCharacter(buffer.getCharacter(0));
        isLegalDigitCharacter(buffer.getCharacter(0));
        
        int index1 = getSingleDigitFirstIndex();
        int index2 = getSingleDigitSecondIndex();
        
        decodedInts[index1]++;
        decodedInts[index2]++;
    }
    
    private void discharge1() {
        if (isLegalDigitCharacter(buffer.getCharacter(0))) {
            int index = getSingleDigitFirstIndex();
            decodedInts[index]++;
        }
    }
    
    private void discharge4() {
        if (buffer.getCharacter(2) == SEPARATOR_CHARACTER) {
            checkIsDigit(buffer.getCharacter(0));
            checkIsDigit(buffer.getCharacter(1));
            checkNotSeparator(buffer.getCharacter(3));
            checkNotCountBegin(buffer.getCharacter(3));
            checkNotCountEnd(buffer.getCharacter(3));
            
            int index1 = getDoubleDigitIndex();
            int index2 = getSingleDigitFourthIndex();
            
            decodedInts[index1]++;
            decodedInts[index2]++;
        } else {
            // Here, the 3rd buffer char is not #
            checkIsDigit(buffer.getCharacter(0));
            
            if (buffer.getCharacter(1) == COUNT_VALUE_BEGIN_CHARACTER) {
                checkIsDigit(buffer.getCharacter(2));
                checkIsCountEnd(buffer.getCharacter(3));
                int count = readCount(currentIndex - 1);
                int index = getSingleDigitFirstIndex();
                decodedInts[index] += count;
            } else {
                checkIsDigit(buffer.getCharacter(0));
                checkIsDigit(buffer.getCharacter(1));
                checkIsDigit(buffer.getCharacter(2));
                checkIsDigit(buffer.getCharacter(3));
                
                int index1 = getSingleDigitFirstIndex();
                int index2 = getSingleDigitSecondIndex();
                int index3 = getSingleDigitThirdIndex();
                int index4 = getSingleDigitFourthIndex();
                
                decodedInts[index1]++;
                decodedInts[index2]++;
                decodedInts[index3]++;
                decodedInts[index4]++;
            }
        }
    }
    
    private void processBuffer() {
        if (buffer.getCharacter(2) == SEPARATOR_CHARACTER) {
            currentIndex -= 3;
            checkIsDigit(buffer.getCharacter(0));
            checkIsDigit(buffer.getCharacter(1));
            checkNotSeparator(buffer.getCharacter(3));
            
            int index = getDoubleDigitIndex();
            
            if (buffer.getCharacter(3) == COUNT_VALUE_BEGIN_CHARACTER) {
                currentIndex += 3;
                int count = readCount(currentIndex);
                decodedInts[index] += count;
                currentIndex++;
            } else {
                currentIndex++;
                decodedInts[index]++;
                buffer.clear();
                // Push the first two digit and the # away:
                buffer.addCharacter(codeCharacters[++currentIndex]);
                buffer.addCharacter(codeCharacters[++currentIndex]);
                buffer.addCharacter(codeCharacters[++currentIndex]);
            }
        } else if (buffer.getCharacter(2) == COUNT_VALUE_BEGIN_CHARACTER) {
            int index = getDoubleDigitIndex();
            int count = readCount(currentIndex - 2);
            decodedInts[index] += count;
            buffer.clear();
        } else if (buffer.getCharacter(1) == COUNT_VALUE_BEGIN_CHARACTER) {
            int index = getSingleDigitFirstIndex();
            int count = readCount(currentIndex - 2);
            decodedInts[index] += count;
            buffer.clear();
        } else {
            int index1 = getSingleDigitFirstIndex();
            int index2 = getSingleDigitSecondIndex();

            decodedInts[index1]++;
            decodedInts[index2]++;
        }
    }
    
    private int readCount(int index) {
        int saveCurrentIndex = index;
        int count = 0;
        int numberOfIterations = 0;
        
        while (index < codeCharacters.length
                && codeCharacters[index] != COUNT_VALUE_END_CHARACTER) {
            
            char digit = codeCharacters[index++];
            count *= 10;
            count += (int)(digit - '0');
            numberOfIterations++;
        }
        
        currentIndex = saveCurrentIndex + numberOfIterations - 1;
        
        assert index < codeCharacters.length : 
                "No trailing "
                + COUNT_VALUE_END_CHARACTER 
                + " at the end of the code.";
        
        assert codeCharacters[index] == COUNT_VALUE_END_CHARACTER :
                "Expected '" + COUNT_VALUE_END_CHARACTER
                + "'. Read '"
                + codeCharacters[index] 
                + "'.";
        
        return count;
    }
    
    private int getSingleDigitFirstIndex() {
        char character = buffer.getCharacter(0);
        return character - '0' - 1;
    }
    
    private int getSingleDigitSecondIndex() {
        char character = buffer.getCharacter(1);
        return character - '0' - 1;
    }
    
    private int getSingleDigitThirdIndex() {
        char character = buffer.getCharacter(2);
        return character - '0' - 1;
    }
    
    private int getSingleDigitFourthIndex() {
        char character = buffer.getCharacter(3);
        return character - '0' - 1;
    }
    
    private int getDoubleDigitIndex() {
        char characterHi = buffer.getCharacter(0);
        char characterLo = buffer.getCharacter(1);
        return (characterHi - '0') * 10 + characterLo - '0' - 1;
    }
    
    private static boolean isLegalDigitCharacter(char character) {
        // character must be in [1-9]:
        return character > '0' && character <= '9';
    }
    
    private void checkIsDigit(char character) {
        if (!Character.isDigit(character)) {
            throw new IllegalArgumentException(
                    "A digit in [1-9] expected at index " 
                            + currentIndex
                            + ".");
        }
    }
    
    private void checkNotSeparator(char character) {
        if (character == SEPARATOR_CHARACTER) {
            throw new IllegalArgumentException(
                    "Duplicate '" 
                            + SEPARATOR_CHARACTER
                            + "' is not allowed at index "
                            + currentIndex 
                            + ".");
        }
    }
    
    private void checkNotCountBegin(char character) {
        if (character == COUNT_VALUE_BEGIN_CHARACTER) {
            throw new IllegalArgumentException(
                    "Character '" 
                            + COUNT_VALUE_BEGIN_CHARACTER
                            + "' is not expected at index " 
                            + currentIndex 
                            + ".");
        }
    }
    
    private void checkNotCountEnd(char character) {
        if (character == COUNT_VALUE_END_CHARACTER) {
            throw new IllegalArgumentException(
                    "Character '" 
                            + COUNT_VALUE_END_CHARACTER
                            + "' is not expected at index " 
                            + currentIndex 
                            + ".");
        }
    }
    
    private void checkIsCountEnd(char character) {
        if (character != COUNT_VALUE_END_CHARACTER) {
            throw new IllegalArgumentException(
                    "'" 
                            + COUNT_VALUE_END_CHARACTER 
                            + "' is expected at index " 
                            + currentIndex 
                            + ".");
        }
    }
    
    public static void main(String[] args) {
        boolean test = true;
        
        if (test) {
            String code = "24#(5)1";
            int[] array = decode(code);
            System.out.println(Arrays.toString(array));
            System.out.println(frequency(code));
        } else {
            Scanner scanner = new Scanner(System.in);

            while (scanner.hasNextLine()) {
                String code = scanner.nextLine();
                System.out.println(frequency(code));
                System.out.println(Arrays.toString(decode(code)));
            }
        }
    }
}
