package org.ideaplugins.syncedit;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;

public abstract class SelectSyncEditableWordAction
extends EditorAction {


    protected SelectSyncEditableWordAction(EditorActionHandler editorActionHandler) {
        super(editorActionHandler);
        //System.out.println("SelectSyncEditableWordAction constructor()");
    }



    public void update(AnActionEvent anActionEvent) {
        super.update(anActionEvent);
        //System.out.println("SelectSyncEditableWordAction update()");
        Editor editor = EditorUtils.getEditor(anActionEvent);
        anActionEvent.getPresentation().setEnabled((anActionEvent.getPresentation().isEnabled())
                                                   &&
                                                   (SyncEditModeController.isInSyncEditMode(editor)));
    }
}