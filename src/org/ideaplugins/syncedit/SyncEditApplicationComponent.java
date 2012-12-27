package org.ideaplugins.syncedit;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.intellij.openapi.options.colors.ColorSettingsPages;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class SyncEditApplicationComponent
implements ApplicationComponent {


    private static final String DEMO_TEXT =
        "The quick brown fox jumped over the lazy dog.  <sync>This area of text is in SyncEdit mode.  \nThis means " +
        "that any modifications to words in this area will cause identical modifications in \nall other matching words " +
        "within the area.  See the SyncEdit plugin description for more \ninformation.</sync>  The quick brown fox jumped " +
        "over the lazy dog. ";

    public void initComponent() {
        //System.out.println("SyncEditApplicationComponent initComponent()");
        PluginColorSettingsPageImpl.register();
        //AttributesDescriptor activeSyncEditRangeAttributesDescriptor = new AttributesDescriptor("SyncEdit active range", SyncEditModeColors.ACTIVE_SYNC_EDIT_RANGE_ATTRIBUTES);

        //Color editorBackgroundColor = EditorColors.SEARCH_RESULT_ATTRIBUTES.getDefaultAttributes().getBackgroundColor();
        Color editorBackgroundColor = EditorColorsManager.getInstance().getGlobalScheme().getAttributes(EditorColors.SEARCH_RESULT_ATTRIBUTES).getBackgroundColor();

        //TextAttributes myattributes = new TextAttributes(Color.cyan, Color.blue, EditorColors.SEARCH_RESULT_ATTRIBUTES.getDefaultAttributes().getBackgroundColor(), EffectType.BOXED, 0);
        TextAttributes myattributes = new TextAttributes(Color.cyan, Color.blue, editorBackgroundColor, EffectType.BOXED, 0);
        TextAttributesKey myattributeskey = TextAttributesKey.createTextAttributesKey("ACTIVE_SYNC_EDIT_RANGE", myattributes);
        AttributesDescriptor activeSyncEditRangeAttributesDescriptor = new AttributesDescriptor("SyncEdit active range", myattributeskey);

        ColorSettingsPage[] colorSettingsPages = ColorSettingsPages.getInstance().getRegisteredPages();

        for (ColorSettingsPage colorSettingsPage : colorSettingsPages) {
            if ((colorSettingsPage instanceof PluginColorSettingsPage)) {
                PluginColorSettingsPage pluginsColorSettingsPage = (PluginColorSettingsPage) colorSettingsPage;
                pluginsColorSettingsPage.registerPluginColorSettings("SyncEdit", null, new AttributesDescriptor[]{activeSyncEditRangeAttributesDescriptor}, DEMO_TEXT);
                //pluginsColorSettingsPage.getAdditionalHighlightingTagToDescriptorMap().put("sync", SyncEditModeColors.ACTIVE_SYNC_EDIT_RANGE_ATTRIBUTES);
                pluginsColorSettingsPage.getAdditionalHighlightingTagToDescriptorMap().put("sync", myattributeskey);
            }
        }
    }



    public void disposeComponent() {
        //System.out.println("SyncEditApplicationComponent disposeComponent()");
    }



    @NotNull
    public String getComponentName() {
        return "SyncEditApplicationComponent";
    }
}