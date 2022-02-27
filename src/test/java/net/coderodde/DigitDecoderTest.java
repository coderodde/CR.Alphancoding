package net.coderodde;

import java.util.Arrays;
import java.util.Random;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DigitDecoderTest {
    
    private static final int TEST_ITERATIONS = 100;
    private static final int MAXIMUM_CHARS = 20;
    private static final int MAXIMUM_COUNT = 99999;
    
    private final Random random = new Random(1L);
    
    @Test
    public void testAll() {
        for (int iteration = 0; iteration < TEST_ITERATIONS; ++iteration) {
            testSingle();
        }
    }
    
    private void testSingle() {
        String code = generateRandomCode();
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
        StringBuilder stringBuilder = new StringBuilder();
        char digit = (char)((int) '1' + random.nextInt(9));
        stringBuilder.append(digit);
        
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
