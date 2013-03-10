package org.bejug.tictactoe;

/**
 * An immutable version of CharMatcher for medium-sized sets of characters that uses a hash table
 * with linear probing to check for matches.
 *
 * @author Christopher Swenson
 */
final class MediumCharMatcher extends CharMatcher {
    static final int MAX_SIZE = 1023;
    private final char[] table;
    private final boolean containsZero;
    private final long filter;

    private MediumCharMatcher(char[] table, long filter, boolean containsZero,
                              String description) {
        super(description);
        this.table = table;
        this.filter = filter;
        this.containsZero = containsZero;
    }

    private boolean checkFilter(int c) {
        return 1 == (1 & (filter >> c));
    }

    // This is all essentially copied from ImmutableSet, but we have to duplicate because
    // of dependencies.

    // Represents how tightly we can pack things, as a maximum.
    private static final double DESIRED_LOAD_FACTOR = 0.5;

    /**
     * Returns an array size suitable for the backing array of a hash table that
     * uses open addressing with linear probing in its implementation.  The
     * returned size is the smallest power of two that can hold setSize elements
     * with the desired load factor.
     */
    static int chooseTableSize(int setSize) {
        if (setSize == 1) {
            return 2;
        }
        // Correct the size for open addressing to match desired load factor.
        // Round up to the next highest power of 2.
        int tableSize = Integer.highestOneBit(setSize - 1) << 1;
        while (tableSize * DESIRED_LOAD_FACTOR < setSize) {
            tableSize <<= 1;
        }
        return tableSize;
    }

    // This method is thread-safe, since if any two threads execute it simultaneously, all
    // that will happen is that they compute the same data structure twice, but nothing will ever
    // be incorrect.
    @Override
    public CharMatcher precomputed() {
        return this;
    }

    static CharMatcher from(char[] chars, String description) {
        // Compute the filter.
        long filter = 0;
        int size = chars.length;
        boolean containsZero = (chars[0] == 0);
        // Compute the filter.
        for (char c : chars) {
            filter |= 1L << c;
        }
        // Compute the hash table.
        char[] table = new char[chooseTableSize(size)];
        int mask = table.length - 1;
        for (char c : chars) {
            int index = c & mask;
            while (true) {
                // Check for empty.
                if (table[index] == 0) {
                    table[index] = c;
                    break;
                }
                // Linear probing.
                index = (index + 1) & mask;
            }
        }
        return new MediumCharMatcher(table, filter, containsZero, description);
    }

    @Override
    public boolean matches(char c) {
        if (c == 0) {
            return containsZero;
        }
        if (!checkFilter(c)) {
            return false;
        }
        int mask = table.length - 1;
        int startingIndex = c & mask;
        int index = startingIndex;
        do {
            // Check for empty.
            if (table[index] == 0) {
                return false;
                // Check for match.
            } else if (table[index] == c) {
                return true;
            } else {
                // Linear probing.
                index = (index + 1) & mask;
            }
            // Check to see if we wrapped around the whole table.
        } while (index != startingIndex);
        return false;
    }
}
