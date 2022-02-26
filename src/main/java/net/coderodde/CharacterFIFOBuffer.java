package net.coderodde;

import java.util.Iterator;

final class CharacterFIFOBuffer implements Iterable<Character> {

    static final int BUFFER_SIZE = 4;

    private int headIndex;
    private int size;
    private final char[] buffer = new char[BUFFER_SIZE];

    void addCharacter(char ch) {
        if (size == BUFFER_SIZE) {
            // Remove the head character and set 'ch' as the tail character:
            buffer[((headIndex + size) % BUFFER_SIZE)] = ch;
            headIndex = (headIndex + 1) % BUFFER_SIZE;
        } else {
            buffer[size++] = ch;
        }
    }

    char getCharacter(int index) {
        return buffer[(headIndex + index) % BUFFER_SIZE];
    }

    void clear() {
        size = 0;
    }

    int size() {
        return size;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append("[");

        for (int i = 0; i < size; ++i) {
            sb.append(getCharacter(i));
        }

        return sb.append("]").toString();
    }

    @Override
    public Iterator<Character> iterator() {
        return new Iterator<>() {
            
            private int index = 0;
            
            @Override
            public boolean hasNext() {
                return index < BUFFER_SIZE;
            }

            @Override
            public Character next() {
                return buffer[(headIndex + index++) % BUFFER_SIZE];
            }
        };
    }
}
