package org.ideaplugins.syncedit;

import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;

import java.awt.*;

public class SyncEditModeColors {


    public static final TextAttributesKey ACTIVE_SYNC_EDIT_RANGE_ATTRIBUTES =
        TextAttributesKey.createTextAttributesKey("ACTIVE_SYNC_EDIT_RANGE",
                                                  new TextAttributes(Color.cyan,
                                                                     Color.blue,
                                                                     EditorColors.SEARCH_RESULT_ATTRIBUTES.getDefaultAttributes().getBackgroundColor(),
                                                                     EffectType.BOXED,
                                                                     0));
    /*
    TextAttributesKey.createTextAttributesKey(
        "ACTIVE_SYNC_EDIT_RANGE",
        new TextAttributes(null, null, EditorColors.SEARCH_RESULT_ATTRIBUTES.getDefaultAttributes().getBackgroundColor(), EffectType.BOXED, 0));
        */
    /*
    private static final TextAttributes DEFAULT_ACTIVE_SYNC_EDIT_RANGE_ATTRIBUTES =
        new TextAttributes(Color.cyan,
                           Color.blue,
                           EditorColors.SEARCH_RESULT_ATTRIBUTES.getDefaultAttributes().getBackgroundColor(),
                           EffectType.BOXED,
                           0);*/
}