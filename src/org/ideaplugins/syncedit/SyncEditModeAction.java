package org.ideaplugins.syncedit;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

public class SyncEditModeAction
extends AnAction
implements IntentionAction {

    private static final Logger LOG = Logger.getInstance(SyncEditModeAction.class.toString());

    public static void executeForEditor(Editor editor) {
        //LOG.info("SyncEditModeAction.executeForEditor()" + editor);
        if (SyncEditModeController.isInSyncEditMode(editor)) {
            if ((editor.getCaretModel().getOffset() < SyncEditModeController.getActiveRangeBoxHighlighter().getStartOffset())
                ||
                (editor.getCaretModel().getOffset() > SyncEditModeController.getActiveRangeBoxHighlighter().getEndOffset())) {
                if (editor.getSelectionModel().hasSelection()) {
                    SyncEditModeController.leaveSyncEditMode();
                    SyncEditModeController.enterSyncEditMode(editor);
                }
                else {
                    SyncEditModeController.leaveSyncEditMode();
                }
            }
            else {
                SyncEditModeController.activateSyncEditSelectionForCaretLocation(editor);
            }
        }
        else {
            SyncEditModeController.enterSyncEditMode(editor);
        }
    }



    /**
     * Called by menu items, toolbars, etc to see if they should be enabled for syncedit action
     * @param e
     */
    public void update(AnActionEvent e) {
        super.update(e);
        boolean syncEditAvailable = Configuration.getInstance().isPluginEnabled();
        Editor editor = EditorUtils.getEditor(e);
        if (!SyncEditModeController.isInSyncEditMode(editor)) {

            /* The toolbar icon will generate update() calls from every open window which will immediately close a syncedit in one editor when the toolbar from
            another editor window makes an update call to see if that button should be enabled/disabled. Checking to see if the active editor's project matches
            the ActionEvent's project will avoid this problem. */
            if (SyncEditModeController.getActiveEditor() != null &&
                e.getProject() == SyncEditModeController.getActiveEditor().getProject()) {
                SyncEditModeController.leaveSyncEditMode();
            }

            if ((editor == null) || (!editor.getSelectionModel().hasSelection())) {
                syncEditAvailable = false;
            }
        }
        e.getPresentation().setEnabled(syncEditAvailable);
    }



    public void actionPerformed(AnActionEvent e) {
        if (Configuration.getInstance().isPluginEnabled()) {
            Editor editor = EditorUtils.getEditor(e);
            executeForEditor(editor);
        }
    }



    @NotNull
    public String getText() {
        return "Enter SyncEdit Mode for selection";
    }



    @NotNull
    public String getFamilyName() {
        return "SyncEdit";
    }



    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return Configuration.getInstance().isPluginEnabled() &&
               editor.getSelectionModel().hasSelection() &&
               !SyncEditModeController.isInSyncEditMode();
    }



    public void invoke(@NotNull Project project, Editor editor, PsiFile file)
    throws IncorrectOperationException {
        executeForEditor(editor);
    }



    public boolean startInWriteAction() {
        return false;
    }
}