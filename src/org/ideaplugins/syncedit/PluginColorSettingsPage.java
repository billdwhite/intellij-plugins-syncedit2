package org.ideaplugins.syncedit;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public abstract interface PluginColorSettingsPage
extends ColorSettingsPage {


    public abstract void registerPluginColorSettings(
        String paramString1,
        ColorDescriptor[] paramArrayOfColorDescriptor,
        AttributesDescriptor[] paramArrayOfAttributesDescriptor,
        String paramString2);

    @NotNull
    public abstract Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap();
}