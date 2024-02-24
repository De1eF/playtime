package org.example.service;

import java.awt.*;
import javax.swing.*;

public class Toast extends JFrame {
    JWindow w;

    public Toast(String message) {
        this(message, 100, 100);
    }

    public Toast(String message, int x, int y) {
        this(message, x, y, 300, 100);
    }

    public Toast(String message, int x, int y, int width, int height) {
        this(message, x, y, width, height, new Color(255, 255, 255, 240));
    }

    public Toast(String message, int x, int y, int width, int height, Color textColor)
    {
        w = new JWindow();

        w.setBackground(new Color(0, 0, 0, 0));

        JPanel p = new JPanel() {
            public void paintComponent(Graphics g)
            {
                int wid = g.getFontMetrics().stringWidth(message);
                int hei = g.getFontMetrics().getHeight();

                g.setColor(Color.black);
                g.fillRect(10, 10, wid + 30, hei + 10);
                g.setColor(Color.black);
                g.drawRect(10, 10, wid + 30, hei + 10);

                // set the color of text
                g.setColor(textColor);
                g.drawString(message, 25, 27);
                int t = 250;

                for (int i = 0; i < 4; i++) {
                    t -= 60;
                    g.setColor(new Color(0, 0, 0, t));
                    g.drawRect(10 - i, 10 - i, wid + 30 + i * 2,
                            hei + 10 + i * 2);
                }
            }
        };

        w.add(p);
        w.setLocation(x, y);
        w.setSize(width, height);
    }

    // function to pop up the toast
    public void showToast()
    {
        try {
            w.setOpacity(1);
            w.setVisible(true);

            // wait for some time
            Thread.sleep(2000);

            // make the message disappear  slowly
            for (double d = 1.0; d > 0.2; d -= 0.1) {
                Thread.sleep(100);
                w.setOpacity((float)d);
            }

            // set the visibility to false
            w.setVisible(false);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
