package org.ideaplugins.syncedit;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.PlainSyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

public class PluginColorSettingsPageImpl
    implements PluginColorSettingsPage {


    private AttributesDescriptor[] _attributeDescriptors;
    private Map<String, AttributesDescriptor> _attributeDescriptorMap = new TreeMap<String, AttributesDescriptor>();
    private ColorDescriptor[] _colorDescriptors;
    private Map<String, ColorDescriptor> _colorDescriptorMap = new TreeMap<String, ColorDescriptor>();
    private String _demoText = null;
    private List<String> _demoTextFragments = new ArrayList<String>();
    private Map<String, TextAttributesKey> _highlightingMap = new HashMap<String, TextAttributesKey>();
    private PlainSyntaxHighlighter _syntaxHighlighter = null;



    private PluginColorSettingsPageImpl() {
        this._highlightingMap.put("info", TextAttributesKey.createTextAttributesKey("PCSP_INFO_TEXT",new TextAttributes(null, null, null, null, 2)));
    }



    public static void register() {
        //System.out.println("PluginColorSettingsPageImpl.register()");
        ColorSettingsPages.getInstance().registerPage(new PluginColorSettingsPageImpl());
    }



    @NotNull
    public String getDisplayName() {
        return "Plug-ins";
    }



    @Nullable
    public Icon getIcon() {
        return new ImageIcon(getClass().getResource("/org/ideaplugins/syncedit/resources/plugincolors.png"));
    }



    public void registerPluginColorSettings(String pluginID, ColorDescriptor[] colorDescriptors, AttributesDescriptor[] attributeDescriptors, String demoText) {
        //System.out.println("PluginColorSettingsPageImpl.registerPluginColorSettings(pluginID=" + pluginID + ", colorDescriptors=" + colorDescriptors + ", attributeDescriptors=" + attributeDescriptors + ", demoText=" + demoText + ")");
        if (colorDescriptors != null) {
            this._colorDescriptors = null;
            for (ColorDescriptor colorDescriptor : colorDescriptors) {
                this._colorDescriptorMap.put(pluginID + "." + colorDescriptor.getDisplayName(), colorDescriptor);
            }
        }

        if (attributeDescriptors != null) {
            this._attributeDescriptors = null;
            for (AttributesDescriptor attributesDescriptor : attributeDescriptors) {
                this._attributeDescriptorMap.put(pluginID + "." + attributesDescriptor.getDisplayName(), attributesDescriptor);
            }
        }

        if (demoText != null) {
            this._demoTextFragments.add(demoText);
            this._demoText = null;
        }
    }



    @NotNull
    public AttributesDescriptor[] getAttributeDescriptors() {
        //System.out.println("PluginColorSettingsPageImpl.getAttributeDescriptors()");
        if (this._attributeDescriptors == null) {
            Collection<AttributesDescriptor> attributesDescriptors = this._attributeDescriptorMap.values();
            this._attributeDescriptors = attributesDescriptors.toArray(new AttributesDescriptor[attributesDescriptors.size()]);
        }
        return this._attributeDescriptors;
    }



    @NotNull
    public ColorDescriptor[] getColorDescriptors() {
        //System.out.println("PluginColorSettingsPageImpl.getColorDescriptions()");
        if (this._colorDescriptors == null) {
            Collection<ColorDescriptor> colorDescriptors = this._colorDescriptorMap.values();
            this._colorDescriptors = colorDescriptors.toArray(new ColorDescriptor[colorDescriptors.size()]);
        }
        return this._colorDescriptors;
    }



    @NotNull
    public SyntaxHighlighter getHighlighter() {
        //System.out.println("PluginColorSettingsPageImpl.getHighlighter()");
        if (this._syntaxHighlighter == null) {
            this._syntaxHighlighter =  new PlainSyntaxHighlighter();
        }
        return this._syntaxHighlighter;
    }



    @NotNull
    public String getDemoText() {
        if (this._demoText == null) {
            this._demoText = "";
            for (String _demoTextFragment : this._demoTextFragments) {
                this._demoText = (this._demoText + _demoTextFragment + "\n\n");
            }
            this._demoText += "<info>This Color Settings Panel has been designed as a central place for any colour settings \nthat are needed to be defined by Plug-ins.  " +
                              "The current implementations of ColorSettingsPanel\n provided in the OpenAPI cannot be easily extended by Plug-ins, but this is an implementation \n" +
                              "that can.  Unfortunately plugins each have their own classloader so that it is currently not \npossible to include color settings for your plugin on " +
                              "this tab.  Vote for request 7636 at \nhttp://www.jetbrains.net/jira/browse/IDEA-7636 to support this.";
        }
        return this._demoText;
    }



    @NotNull
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return this._highlightingMap;
    }
}