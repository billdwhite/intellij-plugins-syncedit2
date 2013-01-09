package org.ideaplugins.syncedit;

import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;

import java.awt.*;

public class SyncEditModeColors {

    private static Color DEFAULT_SYNCEDIT_EFFECT_COLOR     = new Color(80,  110, 80);
    private static Color DEFAULT_SYNCEDIT_BACKGROUND_COLOR = new Color(240, 245, 240);

    public static final TextAttributes SyncEditTextAttributes = new TextAttributes(
        EditorColorsManager.getInstance().getGlobalScheme().getAttributes(EditorColors.SEARCH_RESULT_ATTRIBUTES).getForegroundColor(),
        DEFAULT_SYNCEDIT_BACKGROUND_COLOR,
        DEFAULT_SYNCEDIT_EFFECT_COLOR,
        EffectType.BOXED,
        0);

    public static final TextAttributesKey ACTIVE_SYNC_EDIT_RANGE_ATTRIBUTES =
        TextAttributesKey.createTextAttributesKey("ACTIVE_SYNC_EDIT_RANGE",SyncEditTextAttributes);
}