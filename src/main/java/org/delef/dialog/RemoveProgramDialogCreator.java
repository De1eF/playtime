package org.delef.dialog;

import java.awt.*;
import java.util.Arrays;
import javax.swing.*;
import org.delef.Main;
import org.delef.model.Config;
import org.delef.util.Toast;

public class RemoveProgramDialogCreator implements DialogSetUp {
    public void show(Runnable afterConfirm) {
        //dialog
        JDialog removeProgramDialog = new JDialog(Main.getMainForm(), "Remove app");
        Dimension dialogWindowSize = new Dimension(200, 100);
        removeProgramDialog.setMinimumSize(dialogWindowSize);
        removeProgramDialog.setMaximumSize(dialogWindowSize);
        JFrame mainFrame = Main.getMainForm();
        removeProgramDialog.setLocation(
                new Point(
                        mainFrame.getLocation().x + mainFrame.getSize().width / 2,
                        mainFrame.getLocation().y + mainFrame.getSize().height / 2
                )
        );

        //panel container
        JPanel panel = new JPanel();
        panel.setBounds(
                0,
                0,
                dialogWindowSize.width,
                dialogWindowSize.height
        );

        //process entry text
        JTextField removeProgramTextField = new JTextField("Process name");

        //password text
        JPasswordField passwordTextField = new JPasswordField();

        //confirmation button
        JButton removeProgramConfirmButton = new JButton("Confirm");
        removeProgramConfirmButton.addActionListener((e1) -> {
            int toastPosX = removeProgramDialog.getX() + dialogWindowSize.width / 2;
            int toastPosY = removeProgramDialog.getY() + dialogWindowSize.height / 2;

            //check if the password is correct
            if (Arrays.equals(
                    passwordTextField.getPassword(),
                    Config.getConfig().getPassword().toCharArray()
            )) {
                String toRemove = removeProgramTextField.getText();
                if (!Config.getConfig().getSelectedPrograms().contains(toRemove)) {
                    //throw a toast if the program is not in list
                    new Toast(

                            "No such app in list",
                            toastPosX,
                            toastPosY
                    ).showToast();
                    return;
                }

                //remove program from tracked list
                Config.getConfig().getSelectedPrograms().remove(toRemove);
                Config.Save();
                afterConfirm.run();
                removeProgramDialog.dispose();
                return;
            }

            //throw a toast that the password is incorrect
            new Toast(
                    "Wrong password",
                    toastPosX,
                    toastPosY
            ).showToast();
        });

        //adding components to panel
        panel.add(removeProgramTextField);
        panel.add(passwordTextField);
        panel.add(removeProgramConfirmButton);

        removeProgramDialog.add(panel);
        removeProgramDialog.pack();
        removeProgramDialog.setVisible(true);
    }
}
