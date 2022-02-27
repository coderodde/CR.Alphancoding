package net.coderodde;

import java.util.Arrays;
import java.util.Random;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DigitDecoderTest {
    
    private static final int TEST_ITERATIONS = 100;
    private static final int MAXIMUM_CHARS = 4;
    private static final int MAXIMUM_COUNT = 9;
    
    private final Random random = new Random(1L);
    
    @Test
    public void shit() {
        String s = "20#(7)12(10)";
        DigitDecoder.compute(s);
    }
    
//    public void testAll33() {
//        for (int iteration = 0; iteration < TEST_ITERATIONS; ++iteration) {
//            testSingle();
//        }
//    }
    
    private void testSingle() {
        String code = generateRandomCode();
        System.out.println(code);
        int[] decodedActual = DigitDecoder.compute(code);
        int[] decodedExpected = OPDigitDecoder.frequency(code);
        
        assertTrue(Arrays.equals(decodedActual, decodedExpected));
    }
    
    private String generateRandomCode() {
        int numberOfChars = random.nextInt(MAXIMUM_CHARS) + 1;
        StringBuilder stringBuilder = new StringBuilder();
        
        for (int i = 0; i < numberOfChars; i++) {
            stringBuilder.append(getNumber());
        }
        
        return stringBuilder.toString();
    }
    
    private String getNumber()  {
        boolean addCount = random.nextDouble() < 0.33;
        int number = 1 + random.nextInt(26);
        
        StringBuilder stringBuilder = new StringBuilder();
        String numberString = Integer.toString(number);
        stringBuilder.append(numberString);
        
        if (number >= 10) {
            stringBuilder.append("#");
        }
        
        if (addCount) {
            stringBuilder.append(getCountString());
        }
        
        return stringBuilder.toString();
    }
    
    private String getCountString() {
        int count = 1 + random.nextInt(MAXIMUM_COUNT);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");
        stringBuilder.append(Integer.toString(count));
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}
