package org.ideaplugins.syncedit;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.keymap.KeymapManager;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionUtils {


    private static Map<String, EditorActionHandler> _originalActionHandlers = new HashMap();
    private static Map<String, AnAction> _originalActions = new HashMap();
    private static Map<KeyStroke, List<String>> _disabledActionsForKeystrokes = new HashMap();



    private static final EditorActionHandler DISABLED_ACTION_HANDLER = new EditorActionHandler() {
        public boolean isEnabled(Editor editor, DataContext dataContext) {
            //System.out.println("EditorActionhandler.isEnabled(): " + false);
            return false;
        }
        public void execute(Editor editor, DataContext dataContext) {
        }
    };



    public static boolean installActionHandlerOverride(String actionID, EditorActionHandler editorActionHandler) {
        //System.out.println("ActionUtils.installActionHandlerOverride(actionID=" + actionID + ", editorActionHandler=" + editorActionHandler);
        ActionManager actionManager = ActionManager.getInstance();
        AnAction anAction = actionManager.getAction(actionID);
        if ((anAction instanceof EditorAction)) {
            EditorAction editorAction = (EditorAction) anAction;
            EditorActionHandler oldActionHandler = editorAction.getHandler();
            _originalActionHandlers.put(actionID, oldActionHandler);
            editorAction.setupHandler(editorActionHandler);
            return true;
        }
        return false;
    }


    public static void installActionOverride(String actionID, AnAction action) {
        //System.out.println("ActionUtils.installActionOverride(actionID=" + actionID + ", action=" + action);
        ActionManager actionManager = ActionManager.getInstance();
        AnAction oldAction = actionManager.getAction(actionID);
        _originalActions.put(actionID, oldAction);
        actionManager.unregisterAction(actionID);
        actionManager.registerAction(actionID, action);
    }



    public static EditorActionHandler getOriginalActionHandler(String actionID) {
        //System.out.println("ActionUtils.getOriginalActionHandler(actionID=" + actionID);
        return (EditorActionHandler) _originalActionHandlers.get(actionID);
    }



    public static AnAction getOriginalAction(String actionID) {
        //System.out.println("ActionUtils.getOriginalAction(actionID=" + actionID);
        return (AnAction) _originalActions.get(actionID);
    }



    public static void handleActionWithOriginalHandler(String actionID, Editor editor, DataContext dataContext) {
        //System.out.println("ActionUtils.handleActionWithOriginalHandler(actionID=" + actionID + ", editor=" + editor + ", dataContext=" + dataContext);
        EditorActionHandler actionHandler = getOriginalActionHandler(actionID);
        if (actionHandler != null) {
            actionHandler.execute(editor, dataContext);
        }
    }



    public static boolean restoreOriginalActionHandler(String actionID) {
        //System.out.println("ActionUtils.restoreOriginalActionHandler(actionID=" + actionID);
        EditorActionManager editorActionManager = EditorActionManager.getInstance();
        EditorActionHandler actionHandler = (EditorActionHandler) _originalActionHandlers.remove(actionID);
        if (actionHandler != null) {
            editorActionManager.setActionHandler(actionID, actionHandler);
            return true;
        }
        return false;
    }



    public static void restoreOriginalAction(String actionID) {
        //System.out.println("ActionUtils.restoreOriginalAction(actionID=" + actionID);
        ActionManager actionManager = ActionManager.getInstance();
        AnAction originalAction = (AnAction) _originalActions.remove(actionID);
        actionManager.unregisterAction(actionID);
        if (originalAction != null) {
            actionManager.registerAction(actionID, originalAction);
        }
    }



    public static void disableOtherActionsOnSameKeystrokes(String actionID) {
        //System.out.println("ActionUtils.disableOtherActionsOnSameKeystrokes(actionID=" + actionID);
        KeymapManager keymapManager = KeymapManager.getInstance();
        Shortcut[] shortcuts = keymapManager.getActiveKeymap().getShortcuts(actionID);
        for (Shortcut shortcut : shortcuts) {
            if ((shortcut instanceof KeyboardShortcut)) {
                KeyboardShortcut keyboardShortcut = (KeyboardShortcut) shortcut;
                disableActionsForKeystroke(keyboardShortcut.getFirstKeyStroke(), actionID);
            }
        }
    }



    public static void enableOtherActionsOnSameKeystrokes(String actionID) {
        //System.out.println("ActionUtils.enableOtherActionsOnSameKeystrokes(actionID=" + actionID);
        KeymapManager keymapManager = KeymapManager.getInstance();
        Shortcut[] shortcuts = keymapManager.getActiveKeymap().getShortcuts(actionID);
        for (Shortcut shortcut : shortcuts) {
            if ((shortcut instanceof KeyboardShortcut)) {
                KeyboardShortcut keyboardShortcut = (KeyboardShortcut) shortcut;
                enableActionsForKeystroke(keyboardShortcut.getFirstKeyStroke());
            }
        }
    }



    public static void disableActionsForKeystroke(KeyStroke keyStroke, String excludeActionID) {
        //System.out.println("ActionUtils.disableActionsForKeystroke(keyStroke=" + keyStroke + ", excludeActionID=" + excludeActionID);
        KeymapManager keymapManager = KeymapManager.getInstance();
        String[] actionIDs = keymapManager.getActiveKeymap().getActionIds(keyStroke);
        List actionsDisabled = new ArrayList(actionIDs.length);
        for (String actionID : actionIDs) {
            if ((!actionID.equals(excludeActionID)) && (!isRetainedAction(actionID))) {
                if (!installActionHandlerOverride(actionID, DISABLED_ACTION_HANDLER)) {
                    installActionOverride(actionID, new EditorAction(DISABLED_ACTION_HANDLER) {
                    });
                }
                actionsDisabled.add(actionID);
            }
        }
        _disabledActionsForKeystrokes.put(keyStroke, actionsDisabled);
    }



    private static boolean isRetainedAction(String actionID) {
        //System.out.println("ActionUtils.isRetainedAction(actionID=" + actionID);
        return actionID.startsWith("EditorChooseLookupItem");
    }



    public static void enableActionsForKeystroke(KeyStroke keyStroke) {
        //System.out.println("ActionUtils.enableActionsForKeystroke(keyStroke=" + keyStroke);
        List actionIDs = (List) _disabledActionsForKeystrokes.get(keyStroke);
        if (actionIDs != null) {
            for (Object actionID1 : actionIDs) {
                String actionID = (String) actionID1;
                if (!restoreOriginalActionHandler(actionID)) {
                    restoreOriginalAction(actionID);
                }
            }
        }
    }
}