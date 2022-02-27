package net.coderodde;

public final class OPDigitDecoder {

    public static int[] frequency(String s) {
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

        return result;
    }
}
