package org.ideaplugins.syncedit;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.undo.UndoManager;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.util.TextRange;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class SyncEditModeController {


    //private static final String ACTION_EDITOR_MOVE_LINE_START_WITH_SELECTION = "EditorLineStartWithSelection";
    //private static final String ACTION_EDITOR_MOVE_LINE_END_WITH_SELECTION = "EditorLineEndWithSelection";
    private static boolean _hasSyncEditSelection;
    private static RangeHighlighter _selectedWordBoxHighlight;
    private static RangeHighlighter _selectedWordColorHighlight;
    private static List<RangeHighlighter> _matchingWordColorHighlighters = new ArrayList<RangeHighlighter>();
    private static RangeHighlighter _activeRangeBoxHighlighter;
    private static Editor _activeEditor;
    private static boolean _modifyingDocument;
    private static Map<String, Word> _wordMap = new HashMap<String, Word>();
    private static List<Word> _words;
    private static List<Word> _repeatedWords;
    private static int _selectedRepeatedWordIndex = -1;


    private static DocumentAdapter _documentListener = new DocumentAdapter() {
        public void beforeDocumentChange(DocumentEvent e) {
            //System.out.println("DocumentAdapter beforeDocumentChange()");
            SyncEditModeController.beforeActiveEditorDocumentChange(e);
        }
        public void documentChanged(DocumentEvent e) {
            //System.out.println("DocumentAdapter documentChanged()");
            SyncEditModeController.activeEditorDocumentChange(e);
        }
    };


    private static EditorActionHandler _escapeHandler = new EditorActionHandler() {
        public void execute(Editor editor, DataContext dataContext) {
            //System.out.println("_escapeHandler execute()");
            if (SyncEditModeController._activeEditor != null) {
                if (SyncEditModeController._activeEditor == editor) {
                    SyncEditModeController.doEscape();
                }
                else {
                    ActionUtils.handleActionWithOriginalHandler("EditorEscape", editor, dataContext);
                    SyncEditModeController.leaveSyncEditMode();
                }
            }
        }
    };


    private static EditorActionHandler _enterHandler = new EditorActionHandler() {
        public void execute(Editor editor, DataContext dataContext) {
            //System.out.println("_enterHandler execute()");
            if (SyncEditModeController._activeEditor != null) {
                if (SyncEditModeController._activeEditor == editor) {
                    SyncEditModeController.doEscape();
                }
                else {
                    ActionUtils.handleActionWithOriginalHandler("EditorEnter", editor, dataContext);
                    SyncEditModeController.leaveSyncEditMode();
                }
            }
        }
    };


    private static EditorActionHandler _homeHandler = new EditorActionHandler() {
        public void execute(Editor editor, DataContext dataContext) {
            //System.out.println("_homeHandler execute()");
            if (SyncEditModeController._activeEditor == editor) {
                if ((editor.getCaretModel().getOffset() > SyncEditModeController._selectedWordBoxHighlight.getStartOffset())
                &&
                (editor.getCaretModel().getOffset() <= SyncEditModeController._selectedWordBoxHighlight.getEndOffset())) {
                    EditorUtils.moveCaretBackTo(editor,
                                                SyncEditModeController._selectedWordBoxHighlight.getStartOffset(),
                                                false);
                }
                else {
                    ActionUtils.handleActionWithOriginalHandler("EditorLineStart", editor, dataContext);
                }
            }
            else {
                ActionUtils.handleActionWithOriginalHandler("EditorLineStart", editor, dataContext);
                SyncEditModeController.leaveSyncEditMode();
            }
        }
    };


    private static EditorActionHandler _homeWithSelectionHandler = new EditorActionHandler() {
        public void execute(Editor editor, DataContext dataContext) {
            //System.out.println("_homeWithSelectionHandler execute()");
            if (SyncEditModeController._activeEditor == editor) {
                if ((editor.getCaretModel().getOffset() > SyncEditModeController._selectedWordBoxHighlight.getStartOffset())
                &&
                (editor.getCaretModel().getOffset() <= SyncEditModeController._selectedWordBoxHighlight.getEndOffset())) {
                    EditorUtils.moveCaretBackTo(editor,
                                                SyncEditModeController._selectedWordBoxHighlight.getStartOffset(),
                                                true);
                }
                else {
                    ActionUtils.handleActionWithOriginalHandler("EditorLineStartWithSelection", editor, dataContext);
                }
            }
            else {
                ActionUtils.handleActionWithOriginalHandler("EditorLineStartWithSelection", editor, dataContext);
                SyncEditModeController.leaveSyncEditMode();
            }
        }
    };


    private static EditorActionHandler _endHandler = new EditorActionHandler() {
        public void execute(Editor editor, DataContext dataContext) {
            //System.out.println("_endHandler execute()");
            if (SyncEditModeController._activeEditor == editor) {
                if ((editor.getCaretModel().getOffset() < SyncEditModeController._selectedWordBoxHighlight.getEndOffset())
                &&
                (editor.getCaretModel().getOffset() >= SyncEditModeController._selectedWordBoxHighlight.getStartOffset())) {
                    EditorUtils.moveCaretForwardTo(editor,
                                                   SyncEditModeController._selectedWordBoxHighlight.getEndOffset(),
                                                   false);
                }
                else {
                    ActionUtils.handleActionWithOriginalHandler("EditorLineEnd", editor, dataContext);
                }
            }
            else {
                ActionUtils.handleActionWithOriginalHandler("EditorLineEnd", editor, dataContext);
                SyncEditModeController.leaveSyncEditMode();
            }
        }
    };


    private static EditorActionHandler _endWithSelectionHandler = new EditorActionHandler() {
        public void execute(Editor editor, DataContext dataContext) {
            //System.out.println("_endWithSelectionHandler execute()");
            if (SyncEditModeController._activeEditor == editor) {
                if ((editor.getCaretModel().getOffset() < SyncEditModeController._selectedWordBoxHighlight.getEndOffset())
                &&
                (editor.getCaretModel().getOffset() >= SyncEditModeController._selectedWordBoxHighlight.getStartOffset())) {
                    EditorUtils.moveCaretForwardTo(editor,
                                                   SyncEditModeController._selectedWordBoxHighlight.getEndOffset(),
                                                   true);
                }
                else {
                    ActionUtils.handleActionWithOriginalHandler("EditorLineEndWithSelection", editor, dataContext);
                }
            }
            else {
                ActionUtils.handleActionWithOriginalHandler("EditorLineEndWithSelection", editor, dataContext);
                SyncEditModeController.leaveSyncEditMode();
            }
        }
    };



    static void enterSyncEditMode(Editor editor) {
        //System.out.println("enterSyncEditMode(editor=" + editor + ")");
        if (editor != null) {
            SelectionModel selectionModel = editor.getSelectionModel();
            if (selectionModel.hasSelection()) {
                int start = selectionModel.getSelectionStart();
                int end   = selectionModel.getSelectionEnd();
                if (start != end) {
                    if (start > end) {
                        int temp = end;
                        end = start;
                        start = temp;
                    }
                    enterSyncEditMode(editor, start, end);
                    selectionModel.removeSelection();
                }
            }
        }
    }



    public static void enterSyncEditMode(Editor editor, int rangeStart, int rangeEnd) {
        //System.out.println("enterSyncEditMode(editor=" + editor + ", rangeStart=" + rangeStart + ", rangeEnd=" + rangeEnd + ")");
        if ((_activeEditor != null) && (_activeEditor != editor)) {
            leaveSyncEditMode();
        }
        _activeEditor = editor;
        try {
            /*
            Color effectColor = EditorColors.SEARCH_RESULT_ATTRIBUTES.getDefaultAttributes().getBackgroundColor();
            if (effectColor == null) {
                effectColor = Color.magenta;
            }
            */
            TextAttributes rangeAttributes = editor.getColorsScheme().getAttributes(SyncEditModeColors.ACTIVE_SYNC_EDIT_RANGE_ATTRIBUTES);

            _activeRangeBoxHighlighter = _activeEditor.getMarkupModel().addRangeHighlighter(rangeStart,
                                                                                            rangeEnd,
                                                                                            5997,
                                                                                            rangeAttributes,
                                                                                            HighlighterTargetArea.EXACT_RANGE);

            _activeRangeBoxHighlighter.setGreedyToLeft(true);
            _activeRangeBoxHighlighter.setGreedyToRight(true);

            _activeEditor.getDocument().addDocumentListener(_documentListener);
            installEditorActionHandlers();
        }
        catch (RuntimeException e) {
            leaveSyncEditMode();
            throw e;
        }
    }



    private static void installEditorActionHandlers() {
        //System.out.println("SyncEditModeController.installEditorActionHandlers()");
        ActionUtils.installActionHandlerOverride("EditorEscape", _escapeHandler);
        ActionUtils.installActionHandlerOverride("EditorEnter", _enterHandler);
        ActionUtils.disableOtherActionsOnSameKeystrokes("NextSyncEditableWordAction");
        ActionUtils.disableOtherActionsOnSameKeystrokes("PreviousSyncEditableWordAction");
    }



    public static boolean isInSyncEditMode(Editor editor) {
        //System.out.println("SyncEditModeController.isInSyncEditMode()");
        return _activeEditor == editor;
    }



    public static boolean isInSyncEditMode() {
        return getActiveEditor() != null;
    }



    public static Editor getActiveEditor() {
        return _activeEditor;
    }



    public static void leaveSyncEditMode() {
        //System.out.println("SyncEditModeController.leaveSyncEditMode()");
        if (_activeEditor != null) {
            try {
                clearSyncEditSelection();
                clearWords();
            }
            finally {
                try {
                    if (_activeRangeBoxHighlighter != null) {
                        _activeEditor.getMarkupModel().removeHighlighter(_activeRangeBoxHighlighter);
                    }
                    _activeEditor.getDocument().removeDocumentListener(_documentListener);
                    _activeEditor = null;
                    _activeRangeBoxHighlighter = null;
                }
                finally {
                    restoreEditorActionHandlers();
                }
            }
        }
    }



    private static void restoreEditorActionHandlers() {
        //System.out.println("SyncEditModeController.restoreEditorActionHandlers()");
        ActionUtils.restoreOriginalActionHandler("EditorEscape");
        ActionUtils.restoreOriginalActionHandler("EditorEnter");
        ActionUtils.enableOtherActionsOnSameKeystrokes("NextSyncEditableWordAction");
        ActionUtils.enableOtherActionsOnSameKeystrokes("PreviousSyncEditableWordAction");
    }



    public static RangeHighlighter getActiveRangeBoxHighlighter() {
        //System.out.println("SyncEditModeController.getActiveRangeBoxHighlighter()");
        return _activeRangeBoxHighlighter;
    }



    private static void beforeActiveEditorDocumentChange(DocumentEvent e) {
        //System.out.println("SyncEditModeController.beforeActiveEditorDocumentChange()");
        UndoManager undoManager = UndoManager.getInstance(_activeEditor.getProject());
        if ((!_modifyingDocument) && (!undoManager.isUndoInProgress()) && (!undoManager.isRedoInProgress())) {
            int blockStart = e.getOffset();
            if ((blockStart < getActiveRangeBoxHighlighter().getStartOffset())
            ||
            (blockStart > getActiveRangeBoxHighlighter().getEndOffset())) {
                return;
            }
            RangeHighlighter selectedWordBoxHighlight = getSelectedWordBoxHighlight();
            if ((blockStart + e.getOldLength() > getActiveRangeBoxHighlighter().getEndOffset()) &&
            (selectedWordBoxHighlight != null)) {
                clearSyncEditSelection();
            }

            boolean needsHighlight = false;
            if (selectedWordBoxHighlight == null) {
                needsHighlight = true;
            }
            else {
                int boxHighlightEnd = selectedWordBoxHighlight.getEndOffset();
                int boxHighlightStart = selectedWordBoxHighlight.getStartOffset();
                if ((blockStart < boxHighlightStart) || (blockStart > boxHighlightEnd)
                ||
                (blockStart + e.getOldLength() > boxHighlightEnd)) {
                    needsHighlight = true;
                }
            }
            if (needsHighlight) {
                if (e.getOldLength() > 1) {
                    String overwrittenRange = e.getOldFragment().toString();
                    int overwrittenOffset = e.getOffset();
                    activateSyncEditSelection(_activeEditor,
                                              overwrittenRange,
                                              overwrittenOffset,
                                              overwrittenOffset + overwrittenRange.length());
                }
                else {
                    activateSyncEditSelectionForCaretLocation(_activeEditor);
                }
            }
        }
    }



    public static void activateSyncEditSelectionForCaretLocation(Editor editor) {
        //System.out.println("SyncEditModeController.activateSyncEditSelectionForCaretLocation()");
        Fragment fragment = getSyncEditFragmentAtCaret(editor);
        activateSyncEditSelection(editor, fragment.getText(), fragment.getStart(), fragment.getEnd());
    }



    private static Fragment getSyncEditFragmentAtCaret(Editor editor) {
        //System.out.println("SyncEditModeController.getSyncEditFragmentAtCaret()");
        SelectionModel selectionModel = editor.getSelectionModel();
        if (selectionModel.hasSelection()) {
            return getFragmentForSelection(editor);
        }
        try {
            CaretModel caretModel = editor.getCaretModel();
            int origCaretPos = caretModel.getOffset();
            Fragment fragment = null;
            if (caretModel.getLogicalPosition().column > 0) {
                caretModel.moveToOffset(origCaretPos - 1);
                selectionModel.selectWordAtCaret(true);
                caretModel.moveToOffset(origCaretPos);
                fragment = getFragmentForSelection(editor);
            }
            if ((fragment == null) || (fragment.getEnd() != origCaretPos)) {
                selectionModel.removeSelection();
                selectionModel.selectWordAtCaret(true);
                fragment = getFragmentForSelection(editor);
            }
            return fragment;
        }
        finally {
            selectionModel.removeSelection();
        }
    }



    private static Fragment getFragmentForSelection(Editor editor) {
        //System.out.println("SyncEditModeController.getFragmentForSelection()");
        Fragment fragment = new Fragment();
        SelectionModel selectionModel = editor.getSelectionModel();
        if (selectionModel.hasSelection()) {
            int start = selectionModel.getSelectionStart();
            int end = selectionModel.getSelectionEnd();
            if (start > end) {
                int temp = end;
                end = start;
                start = temp;
            }
            fragment.setStart(start);
            fragment.setEnd(end);
            if (start != end) {
                fragment.setText(editor.getDocument().getCharsSequence().subSequence(start, end).toString());
            }
        }
        return fragment;
    }



    private static boolean activateSyncEditSelection(Editor editor, String selectedWord, int syncEditStart, int syncEditEnd) {
        //System.out.println("SyncEditModeController.activateSyncEditSelection()");
        RangeHighlighter rangeHighlighter = getActiveRangeBoxHighlighter();
        int rangeStart = rangeHighlighter.getStartOffset();
        int rangeEnd = rangeHighlighter.getEndOffset();
        if ((syncEditStart >= rangeStart) && (syncEditEnd <= rangeEnd)) {
            clearSyncEditSelection();
            TextRange[] matchingWordRanges = EditorUtils.findMatchingWordRanges(editor,
                                                                                rangeStart,
                                                                                rangeEnd,
                                                                                selectedWord);
            if (matchingWordRanges.length > 1) {
                activateSyncEditSelection(editor, syncEditStart, syncEditEnd, matchingWordRanges);
                return true;
            }
        }
        return false;
    }



    private static void activateSyncEditSelection(Editor editor, int wordStart, int wordEnd, TextRange[] matchingWordRanges) {
        //System.out.println("SyncEditModeController.activateSyncEditSelection()");
        if (_hasSyncEditSelection) {
            clearSyncEditSelection();
        }
        try {
            highlightMatchingWordInstances(editor, matchingWordRanges, wordStart);
            _hasSyncEditSelection = true;
            installSelectionActionHandlers();
            _selectedWordBoxHighlight = editor.getMarkupModel().addRangeHighlighter(wordStart,
                                                                                    wordEnd,
                                                                                    5998,
                                                                                    new TextAttributes(null, null, Color.red, EffectType.BOXED, 0),
                                                                                    HighlighterTargetArea.EXACT_RANGE);

            _selectedWordBoxHighlight.setGreedyToLeft(true);
            _selectedWordBoxHighlight.setGreedyToRight(true);
        }
        catch (RuntimeException e) {
            clearSyncEditSelection();
            throw e;
        }
    }



    private static void installSelectionActionHandlers() {
        //System.out.println("SyncEditModeController.installSelectionActionHandlers()");
        ActionUtils.installActionHandlerOverride("EditorLineStart", _homeHandler);
        ActionUtils.installActionHandlerOverride("EditorLineStartWithSelection", _homeWithSelectionHandler);
        ActionUtils.installActionHandlerOverride("EditorLineEnd", _endHandler);
        ActionUtils.installActionHandlerOverride("EditorLineEndWithSelection", _endWithSelectionHandler);
    }



    static void highlightMatchingWordInstances(Editor editor, TextRange[] matchingWordRanges, int selectedWordPosition) {
        //System.out.println("SyncEditModeController.highlightMatchingWordInstances(editor=" + editor + ", matchingWordRanges=" + matchingWordRanges + ", selectedWordPosition=" + selectedWordPosition + ")");
        for (TextRange matchingWordRange : matchingWordRanges) {
            TextAttributes textAttributes = EditorColors.SEARCH_RESULT_ATTRIBUTES.getDefaultAttributes();
            int startOffset = matchingWordRange.getStartOffset();
            if (startOffset == selectedWordPosition) {
                textAttributes = EditorColors.WRITE_SEARCH_RESULT_ATTRIBUTES.getDefaultAttributes();
            }
            RangeHighlighter matchingWordHighlighter = editor.getMarkupModel().addRangeHighlighter(startOffset,
                                                                                                   matchingWordRange.getEndOffset(),
                                                                                                   5999,
                                                                                                   textAttributes,
                                                                                                   HighlighterTargetArea.EXACT_RANGE);
            matchingWordHighlighter.setGreedyToLeft(true);
            matchingWordHighlighter.setGreedyToRight(true);
            _matchingWordColorHighlighters.add(matchingWordHighlighter);
            if (startOffset == selectedWordPosition) {
                _selectedWordColorHighlight = matchingWordHighlighter;
            }
        }
    }



    private static void activeEditorDocumentChange(DocumentEvent e) {
        //System.out.println("SyncEditModeController.activeEditorDocumentChange()");
        UndoManager undoManager = UndoManager.getInstance(_activeEditor.getProject());
        if ((!_modifyingDocument)
            &&
            (!undoManager.isUndoInProgress())
            &&
            (!undoManager.isRedoInProgress())
            &&
            (getSelectedWordBoxHighlight() != null)) {
            int editOffset = e.getOffset();
            int boxStartOffset = getSelectedWordBoxHighlight().getStartOffset();
            int boxEndOffset = getSelectedWordBoxHighlight().getEndOffset();
            if ((editOffset >= boxStartOffset) && (editOffset <= boxEndOffset)) {
                handleSyncEdit(e);
            }
        }
    }



    private static void handleSyncEdit(DocumentEvent e) {
        //System.out.println("SyncEditModeController.handleSyncEdit(DocumentEvent=" + e + ")");
        _modifyingDocument = true;
        try {
            int caretOffset = _activeEditor.getCaretModel().getOffset();
            int caretOffsetCorrection = 0;
            int subOffset = e.getOffset() - getSelectedWordBoxHighlight().getStartOffset();
            int lengthRemoved = e.getOldLength();
            if (lengthRemoved > 0) {
                for (int i = _matchingWordColorHighlighters.size() - 1; i >= 0; i--) {
                    RangeHighlighter rangeHighlighter = _matchingWordColorHighlighters.get(i);
                    if (rangeHighlighter != _selectedWordColorHighlight) {
                        int otherOffset = rangeHighlighter.getStartOffset() + subOffset;
                        _activeEditor.getDocument().deleteString(otherOffset, otherOffset + lengthRemoved);
                        if (otherOffset < caretOffset) {
                            caretOffsetCorrection -= lengthRemoved;
                        }
                    }
                }
            }

            int lengthAdded = e.getNewLength();
            if (lengthAdded > 0) {
                for (int i = _matchingWordColorHighlighters.size() - 1; i >= 0; i--) {
                    RangeHighlighter rangeHighlighter = _matchingWordColorHighlighters.get(i);
                    if (rangeHighlighter != _selectedWordColorHighlight) {
                        int otherOffset = rangeHighlighter.getStartOffset() + subOffset;
                        _activeEditor.getDocument().insertString(otherOffset, e.getNewFragment());
                        if (otherOffset < caretOffset) {
                            caretOffsetCorrection += lengthAdded;
                        }
                    }
                }
            }

            if (caretOffsetCorrection != 0) {
                final int finalCaretOffsetCorrection = caretOffsetCorrection;
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        int oldOffset = SyncEditModeController._activeEditor.getCaretModel().getOffset();
                        int newOffset = oldOffset + finalCaretOffsetCorrection;
                        SyncEditModeController._activeEditor.getCaretModel().moveToOffset(newOffset);
                        SyncEditModeController._activeEditor.getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
                    }
                });
            }
        }
        finally {
            _modifyingDocument = false;
            clearWords();
        }
    }



    private static void doEscape() {
        //System.out.println("SyncEditModeController.doEscape()");
        if (_hasSyncEditSelection) {
            clearSyncEditSelection();
        }
        else {
            leaveSyncEditMode();
        }
    }



    public static void clearSyncEditSelection() {
        if (_hasSyncEditSelection) {
            _hasSyncEditSelection = false;
            restoreSelectionActionHandlers();
            _selectedWordBoxHighlight = null;
            for (Object _matchingWordColorHighlighter : _matchingWordColorHighlighters) {
                RangeHighlighter rangeHighlighter = (RangeHighlighter) _matchingWordColorHighlighter;
                _activeEditor.getMarkupModel().removeHighlighter(rangeHighlighter);
            }
            _matchingWordColorHighlighters.clear();
            _selectedWordColorHighlight = null;
        }
    }



    private static void restoreSelectionActionHandlers() {
        ActionUtils.restoreOriginalActionHandler("EditorLineStart");
        ActionUtils.restoreOriginalActionHandler("EditorLineEnd");
        ActionUtils.restoreOriginalActionHandler("EditorLineStartWithSelection");
        ActionUtils.restoreOriginalActionHandler("EditorLineEndWithSelection");
        if (_selectedWordBoxHighlight != null) {
            _activeEditor.getMarkupModel().removeHighlighter(_selectedWordBoxHighlight);
        }
    }



    public static RangeHighlighter getSelectedWordBoxHighlight() {
        return _selectedWordBoxHighlight;
    }



    public static void selectNextRepeatedWord() {
        if (_activeEditor != null) {
            List<Word> words = getRepeatedWords();
            Word wordToSelect = null;
            if (!words.isEmpty()) {
                if (_selectedRepeatedWordIndex == -1) {
                    wordToSelect = Word.findNextWord(words, _activeEditor.getCaretModel().getOffset());
                    if (wordToSelect == null) {
                        wordToSelect = Word.findNextWord(words, getActiveRangeStartOffset());
                    }
                    if (wordToSelect != null) {
                        _selectedRepeatedWordIndex = words.indexOf(wordToSelect);
                    }
                }
                else {
                    _selectedRepeatedWordIndex += 1;
                    if (_selectedRepeatedWordIndex == words.size()) {
                        _selectedRepeatedWordIndex = 0;
                    }
                    if (_selectedRepeatedWordIndex < words.size()) {
                        wordToSelect = words.get(_selectedRepeatedWordIndex);
                    }
                }
            }
            if (wordToSelect != null) {
                selectRepeatedWord(_activeEditor, wordToSelect);
            }
        }
    }



    public static void selectLastRepeatedWord() {
        if (_activeEditor != null) {
            List<Word> words = getRepeatedWords();
            Word wordToSelect = null;
            if (!words.isEmpty()) {
                if (_selectedRepeatedWordIndex == -1) {
                    wordToSelect = Word.findLastWord(words, _activeEditor.getCaretModel().getOffset());
                    if (wordToSelect == null) {
                        wordToSelect = Word.findLastWord(words, getActiveRangeEndOffset());
                    }
                    if (wordToSelect != null) {
                        _selectedRepeatedWordIndex = words.indexOf(wordToSelect);
                    }
                }
                else {
                    _selectedRepeatedWordIndex -= 1;
                    if (_selectedRepeatedWordIndex == -1) {
                        _selectedRepeatedWordIndex = words.size() - 1;
                    }
                    if (_selectedRepeatedWordIndex > -1) {
                        wordToSelect = words.get(_selectedRepeatedWordIndex);
                    }
                }
            }
            if (wordToSelect != null) {
                selectRepeatedWord(_activeEditor, wordToSelect);
            }
        }
    }



    public static void selectRepeatedWord(Editor editor, Word wordToSelect) {
        //System.out.println("SyncEditModeController.selectRepeatedWord(editor=" + editor + " , wordToSelect=" + wordToSelect + ")");
        TextRange firstInstanceRange = wordToSelect.getFirstInstanceRange();
        TextRange[] subsequentRanges = wordToSelect.getSubsequentInstanceRanges();
        TextRange[] allInstanceRanges = new TextRange[subsequentRanges.length + 1];
        allInstanceRanges[0] = firstInstanceRange;
        System.arraycopy(subsequentRanges, 0, allInstanceRanges, 1, subsequentRanges.length);
        activateSyncEditSelection(editor,
                                  firstInstanceRange.getStartOffset(),
                                  firstInstanceRange.getEndOffset(),
                                  allInstanceRanges);
        editor.getSelectionModel().setSelection(firstInstanceRange.getStartOffset(), firstInstanceRange.getEndOffset());
        editor.getCaretModel().moveToOffset(firstInstanceRange.getEndOffset());
    }



    private static List<Word> getRepeatedWords() {
        if (_repeatedWords == null) {
            _repeatedWords = new ArrayList<Word>();
            _words = new LinkedList<Word>();
            int offset = getActiveRangeStartOffset();
            while (offset < getActiveRangeEndOffset()) {
                TextRange[] wordRanges = EditorUtils.getWordsAtOffset(_activeEditor, offset);
                int limit = shouldFindCompoundWords() ? Math.min(wordRanges.length, 1) : wordRanges.length;
                for (int i = 0; i < limit; i++) {
                    TextRange wordRange = wordRanges[i];
                    if ((wordRange.getStartOffset() >= getActiveRangeStartOffset()) && (wordRange.getEndOffset() <= getActiveRangeEndOffset())) {
                        addWord(wordRange);
                    }
                }
                int wordLength = 1;
                offset += wordLength;
            }
        }

        return _repeatedWords;
    }



    private static void addWord(TextRange wordRange) {
        //System.out.println("SyncEditModeController.addWord(wordRange=" + wordRange +  ", isWordPart=" + isWordPart + ")");
        String wordText =
            _activeEditor.getDocument().getCharsSequence().subSequence(wordRange.getStartOffset(), wordRange.getEndOffset()).toString();
        if (shouldFindCompoundWords()) {
            for (int i = _words.size() - 1; i >= 0; i--) {
                Word lastWord = _words.get(i);
                int lastWordEnd = lastWord.getFirstInstanceRange().getEndOffset();
                if (lastWordEnd == wordRange.getStartOffset()) {
                    addWord(new TextRange(lastWord.getFirstInstanceRange().getStartOffset(), wordRange.getEndOffset()));
                }
                else if (lastWordEnd < wordRange.getStartOffset()) {
                    break;
                }
            }
            _words.add(new Word(wordText, wordRange));
        }

        Word word = _wordMap.get(wordText);
        if (word == null) {
            TextRange[] matchingWordRanges = EditorUtils.findMatchingWordRanges(_activeEditor,
                                                                                wordRange.getEndOffset(),
                                                                                getActiveRangeEndOffset(),
                                                                                wordText);

            word = new Word(wordText, wordRange, matchingWordRanges);
            _wordMap.put(wordText, word);
            if (word.isRepeated()) {
                _repeatedWords.add(word);
            }
        }
    }



    private static boolean shouldFindCompoundWords() {
        return false;
    }



    private static void clearWords() {
        _repeatedWords = null;
        _words = null;
        _selectedRepeatedWordIndex = -1;
        _wordMap.clear();
    }



    private static int getActiveRangeEndOffset() {
        return _activeRangeBoxHighlighter.getEndOffset();
    }



    private static int getActiveRangeStartOffset() {
        return _activeRangeBoxHighlighter.getStartOffset();
    }



    private static class Fragment {
        private String _text;
        private int _start;
        private int _end;

        public Fragment() {
        }

        public int getEnd() {
            return this._end;
        }
        public void setEnd(int end) {
            this._end = end;
        }

        public int getStart() {
            return this._start;
        }
        public void setStart(int start) {
            this._start = start;
        }

        public String getText() {
            return this._text;
        }
        public void setText(String text) {
            this._text = text;
        }
    }
}