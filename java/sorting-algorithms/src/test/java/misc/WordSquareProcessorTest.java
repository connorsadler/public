package misc;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WordSquareProcessorTest {

    @Test
    void testSimpleCase() {
        
        //   ATOM
        //   C  A
        //   I  R
        //   DARK
        
        Set<String> words = new HashSet<>(Arrays.asList("ATOM", "ACID", "MARK", "DARK"));

        Set<Set<String>> result = new WordSquareProcessor().execute(words);

        assertEquals(1, result.size());
        assertTrue(result.contains(new HashSet<>(Arrays.asList("ATOM", "ACID", "MARK", "DARK"))));
    }

    @Test
    void testExpandedCase() {

        //    ATOM [M]
        //    C  A [E]
        //    I  R [E]
        //    DARK [K]
        //   [DECK]
        
        // Also adds 'DECK' which can be a bottom row word
        // Also adds 'MEEK' which can be a right column word

        Set<String> words = new HashSet<>(Arrays.asList("ATOM", "ACID", "MARK", "DARK", "DECK", "MEEK"));

        Set<Set<String>> result = new WordSquareProcessor().execute(words);

        assertEquals(4, result.size());

        assertTrue(result.contains(new HashSet<>(Arrays.asList("ATOM", "ACID", "MARK", "DARK"))));
        assertTrue(result.contains(new HashSet<>(Arrays.asList("ATOM", "ACID", "MARK", "DECK"))));
        assertTrue(result.contains(new HashSet<>(Arrays.asList("ATOM", "ACID", "MEEK", "DARK"))));
        assertTrue(result.contains(new HashSet<>(Arrays.asList("ATOM", "ACID", "MEEK", "DECK"))));
        
        
    }

}
