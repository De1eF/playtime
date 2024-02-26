package org.delef.dialog;

import java.awt.*;
import java.util.Arrays;
import javax.swing.*;
import lombok.AllArgsConstructor;
import org.delef.Main;
import org.delef.model.Config;
import org.delef.util.Toast;

@AllArgsConstructor
public class ExitDialogCreator implements DialogSetUp {

    public void show(Runnable afterConfirm) {
        //dialog
        JDialog exitDialog = new JDialog(Main.getMainForm(), "Exit");
        Dimension dialogWindowSize = new Dimension(200, 100);
        exitDialog.setMinimumSize(dialogWindowSize);
        exitDialog.setMaximumSize(dialogWindowSize);
        JFrame mainFrame = Main.getMainForm();
        exitDialog.setLocation(
                new Point(
                        mainFrame.getLocation().x + mainFrame.getSize().width / 2,
                        mainFrame.getLocation().y + mainFrame.getSize().height / 2
                )
        );

        //panel container
        JPanel panel = new JPanel();

        //password text
        JPasswordField passwordTextField = new JPasswordField();

        //confirmation button
        JButton confirmButton = new JButton("Confirm");
        confirmButton.addActionListener((e1) -> {
            int toastPosX = exitDialog.getX() + dialogWindowSize.width / 2;
            int toastPosY = exitDialog.getY() + dialogWindowSize.height / 2;

            //check if the password is correct
            if (Arrays.equals(
                    passwordTextField.getPassword(),
                    Config.getConfig().getPassword().toCharArray()
            )) {
                System.exit(0);
                afterConfirm.run();
                exitDialog.dispose();
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
        panel.add(passwordTextField);
        panel.add(confirmButton);

        exitDialog.add(panel);
        exitDialog.pack();
        exitDialog.setVisible(true);
    }
}
