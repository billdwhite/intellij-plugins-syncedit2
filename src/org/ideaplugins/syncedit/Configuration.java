package org.ideaplugins.syncedit;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.DefaultJDOMExternalizer;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class Configuration
implements ApplicationComponent, JDOMExternalizable, Configurable {


    private static final String CONFIGURATION_COMPONENT_NAME = "Settings";
    public static final String PLUGIN_NAME = "SyncEdit";
    public boolean PLUGIN_ENABLED = true;
    public boolean WHOLEWORDSELECTION_ENABLED = true;
    private SettingsPanel _panel;



    public void initComponent() {
    }



    public void disposeComponent() {
    }



    @NotNull
    public String getComponentName() {
        return Configuration.PLUGIN_NAME + "." + Configuration.CONFIGURATION_COMPONENT_NAME;
    }



    public void readExternal(Element element)
    throws InvalidDataException {
        DefaultJDOMExternalizer.readExternal(this, element);
    }



    public void writeExternal(Element element)
    throws WriteExternalException {
        DefaultJDOMExternalizer.writeExternal(this, element);
    }



    public String getDisplayName() {
        return "SyncEdit";
    }



    public Icon getIcon() {
        return Helper.getIcon(Helper.ICON_TOOL_WINDOW);
    }



    public String getHelpTopic() {
        return null;
    }



    public JComponent createComponent() {
        this._panel = new SettingsPanel();
        return this._panel;
    }



    public boolean isModified() {
        return isPluginStateChanged();
    }



    private boolean isPluginStateChanged() {
        return (this._panel.isPluginEnabled() ^ isPluginEnabled() ||
                this._panel.isWholeWordSelectionEnabled() ^ isWholeWordSelectionEnabled());
    }



    public void apply()
    throws ConfigurationException {
        setPluginEnabled(this._panel.isPluginEnabled());
        setWholeWordSelectionEnabled(this._panel.isWholeWordSelectionEnabled());
    }



    public void reset() {
        this._panel.setPluginEnabled(isPluginEnabled());
        this._panel.setWholeWordSelectionEnabled(isWholeWordSelectionEnabled());
    }



    public void disposeUIResources() {
        this._panel = null;
    }



    public boolean isPluginEnabled() {
        return this.PLUGIN_ENABLED;
    }



    private void setPluginEnabled(boolean enabled) {
        this.PLUGIN_ENABLED = enabled;
    }



    public boolean isWholeWordSelectionEnabled() {
        return this.WHOLEWORDSELECTION_ENABLED;
    }



    private void setWholeWordSelectionEnabled(boolean enabled) {
        this.WHOLEWORDSELECTION_ENABLED = enabled;
    }



    public static Configuration getInstance() {
        return ApplicationManager.getApplication().getComponent(Configuration.class);
    }
}