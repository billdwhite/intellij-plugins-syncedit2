package org.ideaplugins.syncedit;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel
extends JPanel {


    private static final String ENABLE_PLUGIN = "Enable SyncEdit";
    private static final String ENABLE_PLUGIN_TOOLTIP = "Enable/disable the SyncEdit plugin";
    private static final String ENABLE_WHOLEWORDSELECTION = "Select whole words only";
    private static final String ENABLE_WHOLEWORDSELECTION_TOOLTIP = "When checked, SyncEdit will only select other word instances to edit";

    private JCheckBox _pluginEnabled;
    private JCheckBox _wholeWordSelectionEnabled;



    public SettingsPanel() {
        buildGUI();
    }



    private void buildGUI() {
        this._pluginEnabled = new JCheckBox(SettingsPanel.ENABLE_PLUGIN);
        this._pluginEnabled.setToolTipText(SettingsPanel.ENABLE_PLUGIN_TOOLTIP);

        this._wholeWordSelectionEnabled = new JCheckBox(SettingsPanel.ENABLE_WHOLEWORDSELECTION);
        this._wholeWordSelectionEnabled.setToolTipText(SettingsPanel.ENABLE_WHOLEWORDSELECTION_TOOLTIP);

        setLayout(new BorderLayout());

        JPanel topPane = new JPanel();
        topPane.setLayout(new BoxLayout(topPane, BoxLayout.X_AXIS));
        topPane.setBorder(BorderFactory.createEtchedBorder());
        topPane.add(this._pluginEnabled);
        topPane.add(this._wholeWordSelectionEnabled);

        /*
        JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
        tabbedPane.setPreferredSize(new Dimension(400, 600));
        tabbedPane.insertTab(SettingsPanel.ABOUT, Helpers.getIcon(Helpers.ICON_TOOL_WINDOW), new AboutPanel(), null, 0);
        */

        add(topPane, "North");
        //add(tabbedPane, "Center");
    }



    public boolean isPluginEnabled() {
        return this._pluginEnabled.isSelected();
    }
    public void setPluginEnabled(boolean enabled) {
        this._pluginEnabled.setSelected(enabled);
    }



    public boolean isWholeWordSelectionEnabled() {
        return this._wholeWordSelectionEnabled.isSelected();
    }
    public void setWholeWordSelectionEnabled(boolean enabled) {
        this._wholeWordSelectionEnabled.setSelected(enabled);
    }

}