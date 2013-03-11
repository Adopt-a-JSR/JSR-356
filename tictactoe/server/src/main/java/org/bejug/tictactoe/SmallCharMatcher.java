package org.bejug.tictactoe;

/**
 * An immutable small version of CharMatcher that uses an efficient hash table implementation, with
 * non-power-of-2 sizing to try to use no reprobing, if possible.
 *
 * @author Christopher Swenson
 */
final class SmallCharMatcher extends CharMatcher {
    static final int MAX_SIZE = 63;
    static final int MAX_TABLE_SIZE = 128;
    private final boolean reprobe;
    private final char[] table;
    private final boolean containsZero;
    final long filter;

    private SmallCharMatcher(char[] table, long filter, boolean containsZero,
                             boolean reprobe, String description) {
        super(description);
        this.table = table;
        this.filter = filter;
        this.containsZero = containsZero;
        this.reprobe = reprobe;
    }

    private boolean checkFilter(int c) {
        return 1 == (1 & (filter >> c));
    }

    @Override
    public CharMatcher precomputed() {
        return this;
    }

    static char[] buildTable(int modulus, char[] allChars, boolean reprobe) {
        char[] table = new char[modulus];
        for (int i = 0; i < allChars.length; i++) {
            char c = allChars[i];
            int index = c % modulus;
            if (index < 0) {
                index += modulus;
            }
            if ((table[index] != 0) && !reprobe) {
                return null;
            } else if (reprobe) {
                while (table[index] != 0) {
                    index = (index + 1) % modulus;
                }
            }
            table[index] = c;
        }
        return table;
    }

    static CharMatcher from(char[] chars, String description) {
        long filter = 0;
        int size = chars.length;
        boolean containsZero = false;
        boolean reprobe = false;
        containsZero = chars[0] == 0;

        // Compute the filter.
        for (char c : chars) {
            filter |= 1L << c;
        }
        char[] table = null;
        for (int i = size; i < MAX_TABLE_SIZE; i++) {
            table = buildTable(i, chars, false);
            if (table != null) {
                break;
            }
        }
        // Compute the hash table.
        if (table == null) {
            table = buildTable(MAX_TABLE_SIZE, chars, true);
            reprobe = true;
        }
        return new SmallCharMatcher(table, filter, containsZero, reprobe, description);
    }

    @Override
    public boolean matches(char c) {
        if (c == 0) {
            return containsZero;
        }
        if (!checkFilter(c)) {
            return false;
        }
        int index = c % table.length;
        if (index < 0) {
            index += table.length;
        }
        while (true) {
            // Check for empty.
            if (table[index] == 0) {
                return false;
            } else if (table[index] == c) {
                return true;
            } else if (reprobe) {
                // Linear probing will terminate eventually.
                index = (index + 1) % table.length;
            } else {
                return false;
            }
        }
    }
}
