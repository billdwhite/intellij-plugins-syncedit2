package org.ideaplugins.syncedit;

import javax.swing.*;
import java.net.MalformedURLException;
import java.net.URL;

public class Helper {


    public static final String ICON_TOOL_WINDOW = "/org/ideaplugins/syncedit/resources/syncedit.png";
    private static final Icon DEFAULT_ICON = getDefaultIcon();



    public static Icon getIcon(String path) {
        URL url = Helper.class.getResource(path);
        if (url == null) {
            try {
                url = new URL(path);
            }
            catch (MalformedURLException e) {
                return DEFAULT_ICON;
            }
        }

        Icon icon = new ImageIcon(url);
        if ((icon.getIconWidth() < 0) || (icon.getIconHeight() < 0)) {
            return DEFAULT_ICON;
        }

        return icon;
    }



    private static Icon getDefaultIcon() {
        return Helper.getIcon(ICON_TOOL_WINDOW);
        /*
        BufferedImage bi = UIUtil.createImage(18, 18, 3);
        Graphics2D g2 = bi.createGraphics();
        g2.setBackground(Color.red);
        g2.clearRect(0, 0, bi.getWidth(), bi.getHeight());
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(2.0F));
        GeneralPath x = new GeneralPath();
        x.moveTo(0.0F, 0.0F);
        x.lineTo(bi.getWidth() - 1.0F, bi.getHeight() - 1.0F);
        x.moveTo(0.0F, bi.getHeight() - 1.0F);
        x.lineTo(bi.getWidth() - 1.0F, 0.0F);
        g2.draw(x);
        return new ImageIcon(bi);
        */
    }
}