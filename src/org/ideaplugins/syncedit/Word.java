package org.ideaplugins.syncedit;

import com.intellij.openapi.util.TextRange;

import java.util.Arrays;
import java.util.List;

public class Word {


    private final String _wordText;
    private final TextRange _firstInstanceRange;
    private final TextRange[] _subsequentInstanceRanges;



    public Word(String wordText, TextRange wordRange) {
        this(wordText, wordRange, null);
        System.out.println("Word(wordText=" + wordText + ", wordRange=" + wordRange + ")");
    }



    public Word(String wordText, TextRange firstInstanceRange, TextRange[] subsequentInstanceRanges) {
        System.out.println("Word(wordText=" + wordText + ", firstInstanceRange=" + firstInstanceRange + ", subsequentInstanceRanges=" + Arrays.toString(subsequentInstanceRanges));
        this._wordText = wordText;
        this._firstInstanceRange = firstInstanceRange;
        this._subsequentInstanceRanges = subsequentInstanceRanges;
    }



    public static Word findLastWord(List<Word> words, int fromOffset) {
        System.out.println("findLastWord(words=" + words + ", fromOffset=" + fromOffset + ")");
        Word wordToSelect = null;
        for (int i = words.size() - 1; i >= 0; i--) {
            Word word = words.get(i);
            if (word.getFirstInstanceRange().getStartOffset() <= fromOffset) {
                wordToSelect = word;
            }
        }
        return wordToSelect;
    }



    public static Word findNextWord(List<Word> words, int fromOffset) {
        System.out.println("findNextWord(words=" + words + ", fromOffset=" + fromOffset + ")");
        Word wordToSelect = null;
        for (int i = 0; (i < words.size()) && (wordToSelect == null); i++) {
            Word word = words.get(i);
            if (word.getFirstInstanceRange().getStartOffset() >= fromOffset) {
                wordToSelect = word;
            }
        }
        return wordToSelect;
    }



    public TextRange getFirstInstanceRange() {
        return this._firstInstanceRange;
    }



    public TextRange[] getSubsequentInstanceRanges() {
        return this._subsequentInstanceRanges;
    }



    public boolean isRepeated() {
        return this._subsequentInstanceRanges.length > 0;
    }



    public String toString() {
        return this._wordText;
    }
}