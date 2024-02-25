package org.example.dialog;

import java.awt.*;
import javax.swing.*;
import org.example.Main;
import org.example.model.Config;

public class PasswordSetUpDialogCreator implements DialogSetUp {
    public void show(Runnable afterConfirm)  {
        //dialog
        JDialog addProgramDialog = new JDialog(Main.getMainFrame(), "DesignatePassword");
        Dimension dialogWindowSize = new Dimension(200, 100);
        addProgramDialog.setMinimumSize(dialogWindowSize);
        addProgramDialog.setMaximumSize(dialogWindowSize);
        JFrame mainFrame = Main.getMainFrame();
        addProgramDialog.setLocation(
                new Point(
                        mainFrame.getLocation().x + mainFrame.getSize().width / 2,
                        mainFrame.getLocation().y + mainFrame.getSize().height / 2
                )
        );

        //panel container
        JPanel panel = new JPanel();
        panel.setBounds(0, 0, dialogWindowSize.width, dialogWindowSize.height);

        //password entry text
        JTextField addPasswordTextField = new JTextField("Password here");
        addPasswordTextField.setBounds(0, 50, dialogWindowSize.width, dialogWindowSize.height / 2);

        //confirmation button
        JButton addProgramConfirmButton = new JButton("Confirm");
        addProgramConfirmButton.setBounds(0, -50, dialogWindowSize.width, dialogWindowSize.height / 2);
        addProgramConfirmButton.addActionListener((e1) -> {
            Config.getConfig().setPassword(addPasswordTextField.getText());
            Config.Save();
            afterConfirm.run();
            addProgramDialog.dispose();
        });

        //adding components to panel
        panel.add(addPasswordTextField);
        panel.add(addProgramConfirmButton);

        addProgramDialog.add(panel);
        addProgramDialog.pack();
        addProgramDialog.setVisible(true);
    }
}
