package misc;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class WordSquareProcessor {
    
    // Calculates all word squares from the list of input words
    // Assumes all words are 4 characters in length
    public Set<Set<String>> execute(Set<String> words) {
        
        // Build indexes
        HashMap<Character, Set<String>> startingWithIndex = buildIndex(words, w -> w.charAt(0));
        HashMap<Character, Set<String>> endingWithIndex = buildIndex(words, w -> w.charAt(3));
        
        // Compute result
        Set<Set<String>> result = new HashSet<>();
        for (String candidateTopRow : words) {
            // left column - must start with first letter of top row
            Set<String> candidateLeftColumnWords = findWordsFromIndex(startingWithIndex, candidateTopRow.charAt(0), candidateTopRow);
            for (String candidateLeftColumn : candidateLeftColumnWords) {
                // right column - must start with last letter of top row
                Set<String> candidateRightColumnWords = findWordsFromIndex(startingWithIndex, candidateTopRow.charAt(3), candidateTopRow, candidateLeftColumn);
                for (String candidateRightColumn : candidateRightColumnWords) {
                    
                    // bottom row word - use both indexes for this
                    Set<String> wordsStartingWithLastLetterOfLeftColumn = findWordsFromIndex(startingWithIndex, candidateLeftColumn.charAt(3), candidateTopRow, candidateLeftColumn, candidateRightColumn);
                    Set<String> wordsEndingWithLastLetterOfRightColumn = findWordsFromIndex(endingWithIndex, candidateRightColumn.charAt(3), candidateTopRow, candidateLeftColumn, candidateRightColumn);
                    Set<String> candidateBottomRowWords = intersectionOfSets(wordsStartingWithLastLetterOfLeftColumn, wordsEndingWithLastLetterOfRightColumn);

                    for (String candidateBottomRow : candidateBottomRowWords) {
                        Set<String> wordSquare = new HashSet<>();
                        wordSquare.add(candidateTopRow);
                        wordSquare.add(candidateLeftColumn);
                        wordSquare.add(candidateRightColumn);
                        wordSquare.add(candidateBottomRow);
                        result.add(wordSquare);
                    }
                }
            }
        }
        
        
        return result;
    }

    private Set<String> intersectionOfSets(Set<String> s1, Set<String> s2) {
        Set<String> result = new HashSet<>(s1);
        result.retainAll(s2);
        return result;
    }

    // find words mapping to 'key' in the supplied index, but exclude all words in 'wordsToExcludeFromResults' in the returned Set
    private Set<String> findWordsFromIndex(HashMap<Character, Set<String>> index, Character key, String... wordsToExcludeFromResults) {
        Set<String> resultFromIndex = index.get(key);
        if (resultFromIndex == null || resultFromIndex.isEmpty()) {
            return Collections.emptySet();
        }

        Set<String> result = new HashSet<>(resultFromIndex);
        result.removeAll(Arrays.asList(wordsToExcludeFromResults));
        return result;
    }

    private HashMap<Character, Set<String>> buildIndex(Set<String> words, Function<String, Character> wordToCharacterMapper) {
        HashMap<Character, Set<String>> result = new HashMap<>();
        for (String word : words) {
            Character key = wordToCharacterMapper.apply(word);
            Set<String> wordsForKey = result.computeIfAbsent(key, ch -> new HashSet<>());
            wordsForKey.add(word);
        }
        return result;
    }
}
