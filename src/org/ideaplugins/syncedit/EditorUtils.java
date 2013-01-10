package org.ideaplugins.syncedit;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.util.TextRange;
import java.util.ArrayList;
import java.util.List;

public class EditorUtils {


    public static TextRange[] getWordsAtOffset(Editor editor, int offset) {
        //System.out.println("EditorUtils.getWordsAtOffset(editor=" + editor + ", offset=" + offset + ")");
        Document document = editor.getDocument();
        CharSequence charsequence = document.getCharsSequence();
        if (offset == document.getTextLength()) {
            offset--;
        }
        //System.out.println("EditorUtils.getWordsAtOffset(editor=" + editor + ", offset=" + offset + ")=" + getWordSelections(editor, charsequence, offset));
        return getWordSelections(editor, charsequence, offset);
    }



    private static TextRange[] getWordSelections(Editor editor, CharSequence charsequence, int offset) {
        //System.out.println("EditorUtils.getWordSelections(editor=" + editor + ", charsequence=" + charsequence + ", offset=" + offset + ")");
        ArrayList<TextRange> arraylist = new ArrayList<TextRange>();
        addWordSelection(editor.getSettings().isCamelWords(), charsequence, offset, arraylist);
        return arraylist.toArray(new TextRange[arraylist.size()]);
    }



    public static void addWordSelection(boolean flag, CharSequence charsequence, int i, List<TextRange> list) {
        //System.out.println("EditorUtils.addWordSelection(flag=" + flag + ", charsequence=" + charsequence + ", i=" + i + ", list=" + list + ")");
        TextRange textrange = flag ? a(charsequence, i) : null;
        if (textrange != null) {
            list.add(textrange);
        }
        TextRange textrange1 = b(charsequence, i);
        if ((textrange1 != null) && (!textrange1.equals(textrange))) {
            list.add(textrange1);
        }
    }



    private static TextRange a(CharSequence charsequence, int i) {
        //System.out.println("EditorUtils.a(charsequence=" + charsequence + ")");
        if ((i > 0)
            &&
            (!Character.isJavaIdentifierPart(charsequence.charAt(i)))
            &&
            (Character.isJavaIdentifierPart(charsequence.charAt(i - 1)))) {
            i--;
        }

        if (Character.isJavaIdentifierPart(charsequence.charAt(i))) {
            int j = i;
            int k = i + 1;
            int l = charsequence.length();

            while ((j > 0) && (Character.isJavaIdentifierPart(charsequence.charAt(j - 1)))) {
                char c = charsequence.charAt(j - 1);
                char c2 = charsequence.charAt(j);
                char c4 = j + 1 >= l ? '\000' : charsequence.charAt(j + 1);
                if (((Character.isLowerCase(c)) && (Character.isUpperCase(c2)))
                    ||
                    ((c == '_') && (c2 != '_'))
                    ||
                    ((Character.isUpperCase(c)) && (Character.isUpperCase(c2)) && (Character.isLowerCase(c4)))) {
                    break;
                }
                j--;
            }

            while ((k < l) && (Character.isJavaIdentifierPart(charsequence.charAt(k)))) {
                char c1 = charsequence.charAt(k - 1);
                char c3 = charsequence.charAt(k);
                char c5 = k + 1 >= l ? '\000' : charsequence.charAt(k + 1);
                if (((Character.isLowerCase(c1)) && (Character.isUpperCase(c3)))
                    ||
                    ((c1 != '_') && (c3 == '_'))
                    ||
                    ((Character.isUpperCase(c1)) && (Character.isUpperCase(c3)) && (Character.isLowerCase(c5)))) {
                    break;
                }
                k++;
            }

            if (j + 1 < k) {
                return new TextRange(j, k);
            }
        }
        return null;
    }



    private static TextRange b(CharSequence charsequence, int i) {
        //System.out.println("b(charsequence=" + charsequence + ", i=" + i);
        if (charsequence.length() == 0) {
            return null;
        }
        if ((i > 0)
            &&
            (!Character.isJavaIdentifierPart(charsequence.charAt(i)))
            &&
            (Character.isJavaIdentifierPart(charsequence.charAt(i - 1)))) {
            i--;
        }
        if (Character.isJavaIdentifierPart(charsequence.charAt(i))) {
            int j = i;
            int k = i;
            while ((j > 0) && (Character.isJavaIdentifierPart(charsequence.charAt(j - 1)))) {
                j--;
            }
            while ((k < charsequence.length()) && (Character.isJavaIdentifierPart(charsequence.charAt(k)))) {
                k++;
            }
            return new TextRange(j, k);
        }
        return null;
    }



    public static void moveCaretForwardTo(Editor editor, int toOffset, boolean withSelection) {
        //System.out.println("moveCaretForwardTo(editor=" + editor + ", toOffset=" + toOffset + ", withSelection=" + withSelection);
        CaretModel caretModel = editor.getCaretModel();
        SelectionModel selectionModel = editor.getSelectionModel();
        int caretOffset = caretModel.getOffset();
        if (withSelection) {
            int newSelectionStart = caretOffset;
            int newSelectionEnd = toOffset;
            if (selectionModel.hasSelection()) {
                int selectionStart = selectionModel.getSelectionStart();
                int selectionEnd = selectionModel.getSelectionEnd();
                if (selectionStart < caretOffset) {
                    newSelectionStart = selectionStart;
                }
                else
                if (selectionEnd < toOffset) {
                    newSelectionStart = selectionEnd;
                }
                else
                if (selectionEnd == toOffset) {
                    newSelectionStart = toOffset;
                }
                else {
                    newSelectionStart = toOffset;
                    newSelectionEnd = selectionEnd;
                }
            }
            selectionModel.setSelection(newSelectionStart, newSelectionEnd);
        }
        else {
            selectionModel.removeSelection();
        }
        caretModel.moveToOffset(toOffset);
    }



    public static void moveCaretBackTo(Editor editor, int toOffset, boolean withSelection) {
        //System.out.println("moveCaretBackTo(editor=" + editor + ", toOffset=" + toOffset + ", withSelection=" + withSelection);
        CaretModel caretModel = editor.getCaretModel();
        SelectionModel selectionModel = editor.getSelectionModel();
        int caretOffset = caretModel.getOffset();
        if (withSelection) {
            int newSelectionStart = toOffset;
            int newSelectionEnd = caretOffset;
            if (selectionModel.hasSelection()) {
                int selectionStart = selectionModel.getSelectionStart();
                int selectionEnd = selectionModel.getSelectionEnd();
                if (selectionEnd > caretOffset) {
                    newSelectionEnd = selectionEnd;
                }
                else
                if (selectionStart > toOffset) {
                    newSelectionEnd = selectionStart;
                }
                else
                if (selectionStart == toOffset) {
                    newSelectionEnd = toOffset;
                }
                else {
                    newSelectionStart = selectionStart;
                    newSelectionEnd = toOffset;
                }
            }

            selectionModel.setSelection(newSelectionStart, newSelectionEnd);
        }
        else {
            selectionModel.removeSelection();
        }
        caretModel.moveToOffset(toOffset);
    }



    public static TextRange[] findMatchingWordRanges(Editor editor, int rangeStart, int rangeEnd, String selectedWord) {
        List<TextRange> result = new ArrayList<TextRange>();

        // method 1 - includes instances within other words
        /*
        List<TextRange> result = new ArrayList<TextRange>();
        String activeRangeText = editor.getDocument().getCharsSequence().subSequence(rangeStart, rangeEnd).toString();
        int wordLength = selectedWord.length();
        int cursor = 0;
        while ((cursor > -1) && (cursor < activeRangeText.length())) {
            cursor = activeRangeText.indexOf(selectedWord, cursor);
            if (cursor > -1) {
                result.add(new TextRange(cursor + rangeStart, rangeStart + cursor + wordLength));
                cursor += wordLength;
            }
        }
        */

        // method 2 - whole words only
        if (!selectedWord.contains(" ")) {
            try {
                String activeRangeText = editor.getDocument().getCharsSequence().subSequence(rangeStart, rangeEnd).toString();
                WholeWordIndexFinder wholeWordIndexFinder = new WholeWordIndexFinder(activeRangeText);
                List<IndexWrapper> results = wholeWordIndexFinder.findIndexesForKeyword(selectedWord);
                for (IndexWrapper nextIndex : results) {
                    result.add(new TextRange(nextIndex.getStart()+rangeStart, nextIndex.getEnd()+rangeStart));
                }
            }
            catch (Exception e) {
                System.out.println("SyncEdit could not handle selection: " + e.getMessage());
            }
        }

        return result.toArray(new TextRange[result.size()]);
    }



    public static Editor getEditor(AnActionEvent e) {
        return (Editor) e.getDataContext().getData("editor");
    }
}