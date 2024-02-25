package org.example.dialog;

import java.awt.*;
import java.time.LocalTime;
import java.util.Arrays;
import javax.swing.*;
import org.example.Main;
import org.example.model.Config;
import org.example.service.Toast;

public class AddTimeDialogCreator implements DialogSetUp {
    public void show(Runnable afterConfirm) {
        //dialog
        JDialog addTimeDialog = new JDialog(Main.getMainFrame(), "Add time");
        Dimension dialogWindowSize = new Dimension(200, 100);
        addTimeDialog.setMinimumSize(dialogWindowSize);
        addTimeDialog.setMaximumSize(dialogWindowSize);
        JFrame mainFrame = Main.getMainFrame();
        addTimeDialog.setLocation(
                new Point(
                        mainFrame.getLocation().x + mainFrame.getSize().width / 2,
                        mainFrame.getLocation().y + mainFrame.getSize().height / 2
                )
        );

        //panel container
        JPanel panel = new JPanel();
        panel.setBounds(0, 0, dialogWindowSize.width, dialogWindowSize.height);

        //minutes to add entry text
        JTextField addMinutesTextField = new JTextField("15");
        addMinutesTextField.setBounds(0, 50, dialogWindowSize.width, dialogWindowSize.height / 2);

        //password text
        JPasswordField passwordTextField = new JPasswordField();
        addMinutesTextField.setBounds(0, -50, dialogWindowSize.width, dialogWindowSize.height / 2);

        //confirmation button
        JButton addProgramConfirmButton = new JButton("Confirm");
        addProgramConfirmButton.setBounds(0, -100, dialogWindowSize.width, dialogWindowSize.height / 2);
        addProgramConfirmButton.addActionListener((e1) -> {
            int toastPosX = addTimeDialog.getX() + dialogWindowSize.width / 2;
            int toastPosY = addTimeDialog.getY() + dialogWindowSize.height / 2;

            //check if the password is correct
            if (Arrays.equals(passwordTextField.getPassword(), Config.getConfig().getPassword().toCharArray())) {
                //add time to bank
                LocalTime timeLeft = LocalTime.parse(Config.getConfig().getTimeBank());
                timeLeft = timeLeft.plusMinutes(Integer.parseInt(addMinutesTextField.getText()));
                Config.getConfig().setTimeBank(timeLeft.toString());
                Config.Save();
                afterConfirm.run();
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
        panel.add(addMinutesTextField);
        panel.add(passwordTextField);
        panel.add(addProgramConfirmButton);

        addTimeDialog.add(panel);
        addTimeDialog.pack();
        addTimeDialog.setVisible(true);
    }
}
