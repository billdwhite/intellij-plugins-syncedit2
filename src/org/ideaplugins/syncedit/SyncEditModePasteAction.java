package org.ideaplugins.syncedit;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;

public class SyncEditModePasteAction
extends AnAction {


    public void update(AnActionEvent e) {
        AnAction pasteAction = ActionManager.getInstance().getAction("$Paste");
        pasteAction.update(new AnActionEvent(e.getInputEvent(),
                                             e.getDataContext(),
                                             e.getPlace(),
                                             e.getPresentation(),
                                             e.getActionManager(),
                                             e.getModifiers()));
        //System.out.println("SyncEditModePasteAction.update()");
    }



    public void actionPerformed(AnActionEvent e) {
        SyncEditModeController.leaveSyncEditMode();
        AnAction pasteAction = ActionManager.getInstance().getAction("$Paste");
        Document document = EditorUtils.getEditor(e).getDocument();
        final int[] pasteOffset = {0};
        final int[] pasteLength = {0};
        DocumentAdapter documentAdapter = new DocumentAdapter() {
            public void documentChanged(DocumentEvent e) {
                pasteOffset[0] = e.getOffset();
                pasteLength[0] = e.getNewLength();
            }
        };
        document.addDocumentListener(documentAdapter);

        //System.out.println("SyncEditModePasteAction.actionPerformed()");

        try {
            pasteAction.actionPerformed(e);
            SyncEditModeController.enterSyncEditMode(EditorUtils.getEditor(e),
                                                     pasteOffset[0],
                                                     pasteOffset[0] + pasteLength[0]);
        }
        finally {
            document.removeDocumentListener(documentAdapter);
        }
    }
}